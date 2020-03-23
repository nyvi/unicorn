package org.unicorn.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.unicorn.annotations.ApiIgnore;
import org.unicorn.annotations.ApiModel;
import org.unicorn.annotations.ApiModelProperty;
import org.unicorn.annotations.ApiOperation;
import org.unicorn.model.ApiInfo;
import org.unicorn.model.Document;
import org.unicorn.model.ModelInfo;
import org.unicorn.model.ModelType;
import org.unicorn.util.ArrayUtils;
import org.unicorn.util.ReflectionUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author czk
 */
@Component
@AllArgsConstructor
public class DocumentService {

    private final WebApplicationContext applicationContext;

    private final Docket docket;

    public void mappingScan() {
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = mapping.getHandlerMethods();
        List<Api> apiList = new ArrayList<>();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethodMap.entrySet()) {
            HandlerMethod method = entry.getValue();
            boolean ignore = method.hasMethodAnnotation(ApiIgnore.class);
            if (ignore) {
                continue;
            }

            RequestMappingInfo mappingInfo = entry.getKey();

            org.unicorn.annotations.Api annotation = AnnotationUtils.getAnnotation(method.getBeanType(), org.unicorn.annotations.Api.class);

            String desc = this.methodDesc(method);

            String[] consumes = this.getConsumes(mappingInfo);
            String[] produces = this.getProduces(mappingInfo);
            RequestMethod[] methods = this.getRequestMethod(mappingInfo);
            MethodParameter[] methodParameters = method.getMethodParameters();
            if (ArrayUtils.isEmpty(consumes)) {
                for (MethodParameter parameter : methodParameters) {
                    if (parameter.getParameter().isAnnotationPresent(RequestBody.class)) {
                        consumes = new String[]{MediaType.APPLICATION_JSON_VALUE};
                        break;
                    }
                }
            }
            List<ModelType> request = Arrays.stream(methodParameters).map(this::getModelType).collect(Collectors.toList());

            ModelType returnType = getModelType(method.getReturnType());

            Set<String> patterns = mappingInfo.getPatternsCondition().getPatterns();

            List<Function<String, Boolean>> ignorePath = docket.getIgnorePath();
            List<String> collect = patterns.stream().filter(r -> {
                boolean flag = false;
                for (Function<String, Boolean> ignoreFunction : ignorePath) {
                    if (ignoreFunction.apply(r)) {
                        flag = true;
                        break;
                    }
                }
                return !flag;
            }).collect(Collectors.toList());

            for (String url : collect) {
                Api api = new Api();
                api.setBean(method.getBean().toString());
                api.setBeanName(annotation != null ? annotation.value() : null);
                api.setPath(url);
                api.setDesc(desc);
                api.setConsumes(consumes);
                api.setProduces(produces);
                api.setMethods(methods);
                api.setParameters(request);
                api.setResponse(returnType);
                apiList.add(api);
            }

        }

        Map<String, List<Api>> collect = apiList.stream().collect(Collectors.groupingBy(Api::getBean, Collectors.toList()));
        for (Map.Entry<String, List<Api>> entry : collect.entrySet()) {
            Document document = Document.builder()
                    .name(entry.getKey())
                    .desc(entry.getValue().get(0).getBeanName())
                    .apiList(this.copy(entry.getValue()))
                    .build();
            DocumentCache.addDocument(entry.getKey(), document);
        }
    }

    private List<ApiInfo> copy(List<Api> apiList) {
        List<ApiInfo> resultList = new ArrayList<>();
        for (Api api : apiList) {
            ApiInfo apiInfo = new ApiInfo();
            BeanUtils.copyProperties(api, apiInfo);
            resultList.add(apiInfo);
        }
        return resultList;
    }

    private String methodDesc(HandlerMethod method) {
        ApiOperation methodAnnotation = method.getMethodAnnotation(ApiOperation.class);
        return methodAnnotation != null ? methodAnnotation.value() : null;
    }

    private ModelType getModelType(MethodParameter methodParameter) {
        ApiModel apiModel = methodParameter.getParameterType().getAnnotation(ApiModel.class);
        this.setGenericModelInfo(methodParameter.getGenericParameterType());
        this.setGenericType(methodParameter.getGenericParameterType(), methodParameter.getParameterType());
        return ModelType.builder()
                .desc(apiModel != null ? apiModel.value() : null)
                .type(ReflectionUtils.getSimpleTypeName(methodParameter.getGenericParameterType()))
                .build();
    }

    private RequestMethod[] getRequestMethod(RequestMappingInfo mappingInfo) {
        return mappingInfo.getMethodsCondition().getMethods().toArray(new RequestMethod[0]);
    }

    private String[] getConsumes(RequestMappingInfo mappingInfo) {
        return mappingInfo.getConsumesCondition().getConsumableMediaTypes()
                .stream().map(MediaType::toString).toArray(String[]::new);
    }

    private String[] getProduces(RequestMappingInfo mappingInfo) {
        return mappingInfo.getProducesCondition().getProducibleMediaTypes()
                .stream().map(MediaType::toString).toArray(String[]::new);
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
        if (!ReflectionUtils.isJavaType(paramType.getTypeName()) && DocumentCache.notExistModel(simpleTypeName)) {
            List<Field> fieldList = ReflectionUtils.getFieldList(paramType);
            List<ModelInfo> infoList = new ArrayList<>(fieldList.size());
            for (Field field : fieldList) {
                String desc = null;
                boolean required = false;
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
     * 设置泛型对象
     */
    private void setGenericType(Type genericReturnType, Class<?> returnType) {
        String simpleName = ReflectionUtils.getSimpleTypeName(genericReturnType);
        if (genericReturnType instanceof ParameterizedType && DocumentCache.notExistModel(simpleName)) {
            Map<String, String> genericMap = this.getGenericMap(genericReturnType);

            List<Field> fieldList = ReflectionUtils.getFieldList(returnType);
            List<ModelInfo> infoList = new ArrayList<>(fieldList.size());
            for (Field field : fieldList) {
                String desc = null;
                boolean required = false;
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
     * 获取泛型变量与泛型类型的对应关系
     * 如: key:T value: java.lang.String
     *
     * @param type 泛型类型
     * @return 对应关系
     */
    private Map<String, String> getGenericMap(Type type) {
        ParameterizedType genericReturnType = (ParameterizedType) type;
        Map<String, String> genericMap = new HashMap<>(16);
        Type rawType = genericReturnType.getRawType();
        // value
        Type[] actualTypeArguments = genericReturnType.getActualTypeArguments();
        // key
        TypeVariable<?>[] typeParameters = ((Class<?>) rawType).getTypeParameters();

        if (typeParameters.length == actualTypeArguments.length) {
            for (int i = 0; i < typeParameters.length; i++) {
                Type actualType = actualTypeArguments[i];
                String simpleName = ReflectionUtils.getSimpleTypeName(actualType);
                genericMap.put(typeParameters[i].getName(), simpleName);
                // 泛型里面还有泛型. 递归
                if (actualType instanceof ParameterizedType) {
                    this.setGenericType(actualType, (Class<?>) ((ParameterizedType) actualType).getRawType());
                }
            }
        }
        return genericMap;
    }


    @Data
    public static class Api {

        private String bean;

        private String beanName;

        private String path;

        private String desc;

        private String[] consumes;

        private String[] produces;

        private RequestMethod[] methods;

        private List<ModelType> parameters;

        private ModelType response;
    }
}
