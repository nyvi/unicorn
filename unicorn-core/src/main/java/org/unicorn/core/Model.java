package org.unicorn.core;

import lombok.Data;

import java.io.Serializable;

/**
 * @author czk
 */
@Data
public class Model implements Serializable {

    /**
     * 参数类型
     */
    private String type;

    /**
     * 参数描述
     */
    private String desc;
}
