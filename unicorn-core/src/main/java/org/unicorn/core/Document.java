package org.unicorn.core;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 文档
 *
 * @author czk
 */
@Data
@Builder
public class Document implements Serializable {

    private static final long serialVersionUID = -3425954352359765321L;

    /**
     * 类名称
     */
    private String name;

    /**
     * 类名称描述
     */
    private String desc;

    /**
     * 方法
     */
    private List<ApiInfo> apiList;

}
