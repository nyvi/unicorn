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
import org.unicorn.annotations.ApiOperation;
import org.unicorn.util.ArrayUtils;
import org.unicorn.util.StrUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
        List<Class<?>> controllerList = this.getAllController();

        for (Class<?> beanClass : controllerList) {
            Document document = this.getDocument(beanClass);
            DocumentCache.addDocument(beanClass.getName(), document);
        }
        System.err.println(DocumentCache.getAll());
    }

    private Document getDocument(Class<?> beanClass) {
        Document document = new Document();
        document.setName(beanClass.getSimpleName());
        Api annotation = AnnotationUtils.findAnnotation(beanClass, Api.class);
        if (annotation != null) {
            document.setDesc(annotation.value());
        }
        String basePath = this.getBasePath(beanClass);

        Method[] declaredMethods = beanClass.getDeclaredMethods();

        List<MethodInfo> methodList = new ArrayList<>(declaredMethods.length);

        for (Method declaredMethod : declaredMethods) {
            this.getMethodInfo(methodList, declaredMethod, basePath);
        }

        document.setMethodList(methodList);
        return document;
    }

    private void getMethodInfo(List<MethodInfo> methodList, Method declaredMethod, String basePath) {
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(declaredMethod, RequestMapping.class);
        if (requestMapping == null || ArrayUtils.isEmpty(requestMapping.value())) {
            return;
        }
        MethodInfo methodInfo;
        String[] pathList = requestMapping.value();
        ApiOperation apiOperation = declaredMethod.getAnnotation(ApiOperation.class);
        String desc = apiOperation != null ? apiOperation.value() : declaredMethod.getName();

        List<String> methods = Arrays.stream(ArrayUtils.defaultIfEmpty(requestMapping.method(), RequestMethod.values()))
                .map(RequestMethod::name).collect(Collectors.toList());

        for (String path : pathList) {
            methodInfo = new MethodInfo();
            methodInfo.setDesc(desc);
            methodInfo.setMethodList(methods);
            methodInfo.setPath(StrUtils.pathRationalize(basePath + path));
            methodList.add(methodInfo);
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

    /**
     * 获取所有控制层
     */
    private List<Class<?>> getAllController() {
        Map<String, Object> controllerMap = applicationContext.getBeansWithAnnotation(Controller.class);
        controllerMap.putAll(applicationContext.getBeansWithAnnotation(RestController.class));

        List<Class<?>> beanList = new ArrayList<>(controllerMap.size());
        for (Object value : controllerMap.values()) {
            Class<?> beanClass = value.getClass();
            beanList.add(beanClass);
        }

        return beanList;
    }

    public DocumentScan(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
