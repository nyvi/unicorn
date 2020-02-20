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
import org.unicorn.annotations.ApiOperation;
import org.unicorn.util.ArrayUtils;
import org.unicorn.util.ReflectionUtils;
import org.unicorn.util.StrUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
public class DocumentScan {

    private final ApplicationContext applicationContext;

    public void scan() {
        // 所有Controller的Class
        List<Class<?>> allControlList = ReflectionUtils.getAnnotationClass(applicationContext, Controller.class, RestController.class);

        for (Class<?> beanClass : allControlList) {
            ApiIgnore apiIgnore = beanClass.getAnnotation(ApiIgnore.class);
            if (apiIgnore == null) {
                Document document = this.getDocument(beanClass);
                // 如果是CGLIB代理的类需要截取
                String beanName = StrUtils.substringBefore(beanClass.getName(), "$$");
                DocumentCache.addDocument(beanName, document);
            }
        }
    }

    private Document getDocument(Class<?> beanClass) {
        Document document = new Document();
        document.setName(StrUtils.substringBefore(beanClass.getSimpleName(), "$$"));
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
    private void getApiInfo(List<ApiInfo> methodList, Method declaredMethod, String basePath) {
        ApiIgnore apiIgnore = declaredMethod.getAnnotation(ApiIgnore.class);
        if (apiIgnore != null) {
            return;
        }
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(declaredMethod, RequestMapping.class);
        if (requestMapping == null || ArrayUtils.isEmpty(requestMapping.value())) {
            return;
        }

        ApiOperation apiOperation = declaredMethod.getAnnotation(ApiOperation.class);
        String desc = apiOperation != null ? apiOperation.value() : declaredMethod.getName();

        // 请求方法
        RequestMethod[] requestMethods = ArrayUtils.defaultIfEmpty(requestMapping.method(), RequestMethod.values());
        List<String> methods = Arrays.stream(requestMethods).map(RequestMethod::name).collect(Collectors.toList());

        // 参数
        Parameter[] parameters = declaredMethod.getParameters();
        List<Model> parameterList = new ArrayList<>(parameters.length);
        Model model;
        if (ArrayUtils.isNotEmpty(parameters)) {
            for (Parameter parameter : parameters) {
                model = new Model();
                model.setType(parameter.getType().getTypeName());
                ApiModel apiModel = AnnotationUtils.getAnnotation(parameter.getType(), ApiModel.class);
                model.setDesc(apiModel != null ? apiModel.value() : null);
                parameterList.add(model);
            }
        }
        model = new Model();
        Type genericReturnType = declaredMethod.getGenericReturnType();
        model.setType(genericReturnType.getTypeName());
        ApiModel apiModel = declaredMethod.getReturnType().getAnnotation(ApiModel.class);
        model.setDesc(apiModel != null ? apiModel.value() : null);

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
     * 获取control上的请求路径
     */
    private String getBasePath(Class<?> beanClass) {
        RequestMapping requestMapping = AnnotationUtils.findAnnotation(beanClass, RequestMapping.class);
        if (requestMapping != null && ArrayUtils.isNotEmpty(requestMapping.value())) {
            return "/" + requestMapping.value()[0] + "/";
        }
        return "/";
    }

    public DocumentScan(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
