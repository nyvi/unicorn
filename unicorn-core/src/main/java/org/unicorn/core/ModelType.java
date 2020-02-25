package org.unicorn.core;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 模型类型
 *
 * @author czk
 */
@Data
@Builder
public class ModelType implements Serializable {

    private static final long serialVersionUID = 2939204079622006600L;
    /**
     * 参数类型
     */
    private String type;

    /**
     * 参数描述
     */
    private String desc;
}
