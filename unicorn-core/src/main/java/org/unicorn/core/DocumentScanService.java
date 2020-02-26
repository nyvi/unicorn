package org.unicorn.core;

import com.google.common.collect.Maps;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.unicorn.annotations.Api;
import org.unicorn.annotations.ApiIgnore;
import org.unicorn.annotations.ApiModel;
import org.unicorn.annotations.ApiModelProperty;
import org.unicorn.annotations.ApiOperation;
import org.unicorn.util.ArrayUtils;
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
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        // 扫描所有Controller
        for (Class<?> beanClass : allControlList) {
            // ignore不处理
            if (beanClass.isAnnotationPresent(ApiIgnore.class)) {
                continue;
            }

            // control可能会被CGLIB代理, 需要获取原本的Class
            Class<?> userClass = ClassUtils.getUserClass(beanClass);
            Document document = this.getDocument(userClass);

            String simpleClassName = ReflectionUtils.getSimpleTypeName(userClass.getName());
            DocumentCache.addDocument(simpleClassName, document);
        }
    }

    /**
     * 获取接口文档
     */
    private Document getDocument(Class<?> beanClass) {
        // 类上的RequestMapping修饰的路径
        String basePath = this.getBasePath(beanClass);

        // Control下的所有方法
        Method[] declaredMethods = beanClass.getDeclaredMethods();
        List<ApiInfo> methodList = new ArrayList<>(declaredMethods.length);
        for (Method declaredMethod : declaredMethods) {
            this.getApiInfo(methodList, declaredMethod, basePath);
        }

        Api annotation = AnnotationUtils.findAnnotation(beanClass, Api.class);

        return Document.builder()
                .name(beanClass.getSimpleName())
                .desc(annotation != null ? annotation.value() : null)
                .apiList(methodList)
                .build();
    }

    /**
     * 获取接口详情
     */
    private void getApiInfo(List<ApiInfo> methodList, Method method, String basePath) {
        if (method.isAnnotationPresent(ApiIgnore.class)) {
            return;
        }
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
        if (requestMapping == null || ArrayUtils.isEmpty(requestMapping.value())) {
            return;
        }

        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
        String desc = apiOperation != null ? apiOperation.value() : method.getName();

        // 请求方法
        RequestMethod[] methods = ArrayUtils.defaultIfEmpty(requestMapping.method(), RequestMethod.values());

        String[] consumes = requestMapping.consumes();
        String[] produces = ArrayUtils.defaultIfEmpty(requestMapping.produces(), new String[]{MediaType.ALL_VALUE});

        // 参数
        Parameter[] parameters = method.getParameters();
        List<ModelType> parameterList = new ArrayList<>(parameters.length);
        if (ArrayUtils.isNotEmpty(parameters)) {
            for (Parameter parameter : parameters) {
                Class<?> paramType = parameter.getType();
                ApiModel apiModel = AnnotationUtils.getAnnotation(paramType, ApiModel.class);
                parameterList.add(
                        ModelType.builder()
                                .desc(apiModel != null ? apiModel.value() : parameter.getName())
                                .type(ReflectionUtils.getSimpleTypeName(parameter.getParameterizedType()))
                                .build()
                );
                this.setModelInfo(paramType);
                if (ArrayUtils.isEmpty(consumes) && parameter.isAnnotationPresent(RequestBody.class)) {
                    consumes = new String[]{MediaType.APPLICATION_JSON_VALUE};
                }
            }
        }
        Type genericReturnType = method.getGenericReturnType();
        this.setModelInfo(method.getReturnType());
        this.setGenericModelInfo(genericReturnType);
        this.setGenericType(genericReturnType, method.getReturnType());

        ApiModel apiModel = method.getReturnType().getAnnotation(ApiModel.class);
        ModelType returnModel = ModelType.builder()
                .desc(apiModel != null ? apiModel.value() : null)
                .type(ReflectionUtils.getSimpleTypeName(genericReturnType))
                .build();

        String[] pathList = requestMapping.value();
        for (String path : pathList) {
            methodList.add(
                    ApiInfo.builder()
                            .desc(desc)
                            .methods(methods)
                            .response(returnModel)
                            .consumes(consumes)
                            .produces(produces)
                            .parameters(parameterList)
                            .path(StrUtils.pathRationalize(basePath + path))
                            .build()
            );
        }
    }

    private void setGenericType(Type genericReturnType, Class<?> returnType) {
        String simpleName = ReflectionUtils.getSimpleTypeName(genericReturnType);
        if (genericReturnType instanceof ParameterizedType && !DocumentCache.containsModel(simpleName)) {
            Map<String, String> genericMap = Maps.newHashMap();
            Type rawType = ((ParameterizedType) genericReturnType).getRawType();
            TypeVariable<?>[] typeParameters = ((Class<?>) rawType).getTypeParameters();
            Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
            if (typeParameters.length == actualTypeArguments.length) {
                for (int i = 0; i < typeParameters.length; i++) {
                    Type actualTypeArgument = actualTypeArguments[i];
                    String simpleTypeName = ReflectionUtils.getSimpleTypeName(actualTypeArgument);
                    genericMap.put(typeParameters[i].getName(), simpleTypeName);
                    if (actualTypeArgument instanceof ParameterizedType) {
                        setGenericType(actualTypeArgument, (Class<?>) ((ParameterizedType) actualTypeArgument).getRawType());
                    }
                }
            }
            List<Field> fieldList = ReflectionUtils.getFieldList(returnType);
            List<ModelInfo> infoList = new ArrayList<>(fieldList.size());
            for (Field field : fieldList) {
                boolean required = false;
                String desc = null;
                ApiModelProperty apiModelProperty = AnnotationUtils.getAnnotation(field, ApiModelProperty.class);
                if (apiModelProperty != null) {
                    if (apiModelProperty.hidden()) {
                        continue;
                    }
                    desc = apiModelProperty.value();
                    required = apiModelProperty.required();
                }
                if (!required) {
                    required = isRequired(field);
                }
                Type type = field.getGenericType();
                String simpleTypeName = ReflectionUtils.getSimpleTypeName(field.getGenericType());
                if (type instanceof ParameterizedType) {
                    Type[] actualTypeList = ((ParameterizedType) type).getActualTypeArguments();
                    for (Type actualType : actualTypeList) {
                        String ownType = actualType.getTypeName();
                        String actualName = genericMap.getOrDefault(ownType, type.getTypeName());
                        simpleTypeName = simpleTypeName.replace("<" + ownType, "<" + actualName);
                        simpleTypeName = simpleTypeName.replace(ownType + ">", actualName + ">");
                        simpleTypeName = simpleTypeName.replace("," + ownType + ",", "," + actualName + ",");
                    }
                } else if (ReflectionUtils.isGenericType(type)) {
                    simpleTypeName = genericMap.getOrDefault(type.getTypeName(), type.getTypeName());
                }

                infoList.add(
                        ModelInfo.builder()
                                .name(field.getName())
                                .type(simpleTypeName)
                                .desc(desc)
                                .required(required)
                                .build()
                );
            }
            DocumentCache.addModel(simpleName, infoList);
        }

    }

    /**
     * 设置泛型对象
     */
    private void setGenericModelInfo(Type type) {
        if (type instanceof ParameterizedType) {
            Type[] actualTypeArray = ((ParameterizedType) type).getActualTypeArguments();
            for (Type actualType : actualTypeArray) {
                if (actualType instanceof ParameterizedType) {
                    this.setGenericModelInfo(actualType);
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
        String simpleTypeName = ReflectionUtils.getSimpleTypeName(paramType.getTypeName());
        if (!ReflectionUtils.isJavaType(paramType.getTypeName()) && !DocumentCache.containsModel(simpleTypeName)) {
            List<Field> fieldList = ReflectionUtils.getFieldList(paramType);
            List<ModelInfo> infoList = new ArrayList<>(fieldList.size());
            for (Field field : fieldList) {
                boolean required = false;
                String desc = null;
                ApiModelProperty apiModelProperty = AnnotationUtils.getAnnotation(field, ApiModelProperty.class);
                if (apiModelProperty != null) {
                    if (apiModelProperty.hidden()) {
                        continue;
                    }
                    required = apiModelProperty.required();
                    desc = apiModelProperty.value();
                }
                if (!required) {
                    required = isRequired(field);
                }
                infoList.add(
                        ModelInfo.builder()
                                .name(field.getName())
                                .type(ReflectionUtils.getSimpleTypeName(field.getGenericType()))
                                .required(required)
                                .desc(desc)
                                .build()
                );
            }
            DocumentCache.addModel(simpleTypeName, infoList);
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
