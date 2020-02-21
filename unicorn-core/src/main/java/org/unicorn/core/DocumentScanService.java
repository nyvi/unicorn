package org.unicorn.core;

import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.unicorn.annotations.Api;
import org.unicorn.annotations.ApiIgnore;
import org.unicorn.annotations.ApiModel;
import org.unicorn.annotations.ApiModelProperty;
import org.unicorn.annotations.ApiOperation;
import org.unicorn.util.ArrayUtils;
import org.unicorn.util.ClassUtils;
import org.unicorn.util.ReflectionUtils;
import org.unicorn.util.StrUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * bean 扫描组装
 *
 * @author czk
 */
@Component
public class DocumentScanService {

    private final ApplicationContext applicationContext;

    public void scan() {
        // 所有Controller的Class
        List<Class<?>> allControlList = ReflectionUtils.getAnnotationClass(applicationContext, Controller.class, RestController.class);

        for (Class<?> beanClass : allControlList) {
            ApiIgnore apiIgnore = beanClass.getAnnotation(ApiIgnore.class);
            if (apiIgnore == null) {
                Class<?> userClass = ClassUtils.getUserClass(beanClass);
                Document document = this.getDocument(userClass);
                DocumentCache.addDocument(userClass.getName(), document);
            }
        }
    }

    private Document getDocument(Class<?> beanClass) {
        Document document = new Document();
        document.setName(beanClass.getSimpleName());
        Api annotation = AnnotationUtils.findAnnotation(beanClass, Api.class);
        document.setDesc(annotation != null ? annotation.value() : null);
        // 类上的RequestMapping修饰的路径
        String basePath = this.getBasePath(beanClass);
        // Control下的所有方法
        Method[] declaredMethods = beanClass.getDeclaredMethods();

        List<ApiInfo> methodList = new ArrayList<>(declaredMethods.length);
        for (Method declaredMethod : declaredMethods) {
            this.getApiInfo(methodList, declaredMethod, basePath);
        }

        document.setApiList(methodList);
        return document;
    }

    /**
     * 获取接口详情
     */
    private void getApiInfo(List<ApiInfo> methodList, Method method, String basePath) {
        ApiIgnore apiIgnore = method.getAnnotation(ApiIgnore.class);
        if (apiIgnore != null) {
            return;
        }
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
        if (requestMapping == null || ArrayUtils.isEmpty(requestMapping.value())) {
            return;
        }

        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
        String desc = apiOperation != null ? apiOperation.value() : method.getName();

        // 请求方法
        RequestMethod[] requestMethods = ArrayUtils.defaultIfEmpty(requestMapping.method(), RequestMethod.values());
        List<String> methods = Arrays.stream(requestMethods).map(RequestMethod::name).collect(Collectors.toList());

        // 参数
        Parameter[] parameters = method.getParameters();
        List<ModelType> parameterList = new ArrayList<>(parameters.length);
        ModelType model;
        if (ArrayUtils.isNotEmpty(parameters)) {
            for (Parameter parameter : parameters) {
                Class<?> paramType = parameter.getType();
                model = new ModelType();
                model.setType(paramType.getTypeName());
                ApiModel apiModel = AnnotationUtils.getAnnotation(paramType, ApiModel.class);
                model.setDesc(apiModel != null ? apiModel.value() : null);
                parameterList.add(model);
                this.setModelInfo(paramType);
            }
        }
        model = new ModelType();
        Type genericReturnType = method.getGenericReturnType();
        model.setType(genericReturnType.getTypeName());
        ApiModel apiModel = method.getReturnType().getAnnotation(ApiModel.class);
        model.setDesc(apiModel != null ? apiModel.value() : null);
        this.setModelInfo(method.getReturnType());
        this.setGenericType(genericReturnType);

        ApiInfo apiInfo;
        String[] pathList = requestMapping.value();
        for (String path : pathList) {
            apiInfo = new ApiInfo();
            apiInfo.setDesc(desc);
            apiInfo.setMethods(methods);
            apiInfo.setResponse(model);
            apiInfo.setParameters(parameterList);
            apiInfo.setPath(StrUtils.pathRationalize(basePath + path));
            methodList.add(apiInfo);
        }
    }

    /**
     * 设置泛型对象
     */
    private void setGenericType(Type type) {
        if (type instanceof ParameterizedType) {
            Type[] actualTypeArray = ((ParameterizedType) type).getActualTypeArguments();
            for (Type actualType : actualTypeArray) {
                if (actualType instanceof ParameterizedType) {
                    this.setGenericType(actualType);
                } else {
                    this.setModelInfo((Class<?>) actualType);
                }
            }
        } else {
            this.setModelInfo(type.getClass());
        }
    }

    /**
     * 设置模型对象
     */
    private void setModelInfo(Class<?> paramType) {
        if (!ReflectionUtils.isJavaType(paramType) && !DocumentCache.containsModel(paramType.getTypeName())) {
            List<Field> fieldList = ReflectionUtils.getFieldList(paramType);
            ModelInfo info;
            List<ModelInfo> infoList = new ArrayList<>(fieldList.size());
            for (Field field : fieldList) {
                info = new ModelInfo();
                ApiModelProperty apiModelProperty = AnnotationUtils.getAnnotation(field, ApiModelProperty.class);
                if (apiModelProperty != null) {
                    if (apiModelProperty.hidden()) {
                        continue;
                    }
                    info.setRequired(apiModelProperty.required());
                    info.setDesc(apiModelProperty.value());
                }
                if (!info.isRequired()) {
                    info.setRequired(isRequired(field));
                }
                info.setName(field.getName());
                info.setType(field.getType().getTypeName());
                infoList.add(info);
            }
            DocumentCache.addModel(paramType.getTypeName(), infoList);
        }
    }

    /**
     * 判断是否必填
     */
    private boolean isRequired(Field field) {
        NotNull notNull = AnnotationUtils.getAnnotation(field, NotNull.class);
        if (notNull != null) {
            return true;
        }
        NotBlank notBlank = AnnotationUtils.getAnnotation(field, NotBlank.class);
        if (notBlank != null) {
            return true;
        }
        NotEmpty notEmpty = AnnotationUtils.getAnnotation(field, NotEmpty.class);
        return notEmpty != null;
    }

    /**
     * 获取control上的请求路径
     */
    private String getBasePath(Class<?> beanClass) {
        RequestMapping requestMapping = AnnotationUtils.findAnnotation(beanClass, RequestMapping.class);
        if (requestMapping != null && ArrayUtils.isNotEmpty(requestMapping.value())) {
            return "/" + requestMapping.value()[0] + "/";
        }
        return "/";
    }

    public DocumentScanService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
