package org.unicorn.core;

import lombok.Data;

import java.io.Serializable;

/**
 * 模型详情
 *
 * @author czk
 */
@Data
public class ModelInfo implements Serializable {

    private static final long serialVersionUID = 989099154530552311L;

    /**
     * 字段名称
     */
    private String name;

    /**
     * 字段类型
     */
    private String type;

    /**
     * 字段描述
     */
    private String desc;

    /**
     * 约束
     */
    private String constraint;
}