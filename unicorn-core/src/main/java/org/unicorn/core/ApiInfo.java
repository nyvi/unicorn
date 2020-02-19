package org.unicorn.core;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author czk
 */
@Data
public class ApiInfo implements Serializable {

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
    private List<String> methodList;

    /**
     * 入参
     */
    private List<Model> parameterList;

    /**
     * 出参
     */
    private Model response;
}
