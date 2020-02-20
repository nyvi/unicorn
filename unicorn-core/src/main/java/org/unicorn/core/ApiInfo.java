package org.unicorn.core;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author czk
 */
@Data
public class ApiInfo implements Serializable {

    private static final long serialVersionUID = 7768357304074145232L;
    /**
     * 路径
     */
    private String path;

    /**
     * 描述
     */
    private String desc;

    /**
     * 请求方式 post get ..
     */
    private List<String> methods;

    /**
     * 入参
     */
    private List<ModelType> parameters;

    /**
     * 出参
     */
    private ModelType response;
}
