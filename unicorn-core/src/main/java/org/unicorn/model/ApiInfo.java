package org.unicorn.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;
import java.util.List;

/**
 * @author czk
 */
@Data
@Builder
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
    private RequestMethod[] methods;

    /**
     * Content-Type
     */
    private String[] consumes;

    /**
     * Accept
     */
    private String[] produces;

    /**
     * 入参
     */
    private List<ModelType> parameters;

    /**
     * 出参
     */
    private ModelType response;
}
