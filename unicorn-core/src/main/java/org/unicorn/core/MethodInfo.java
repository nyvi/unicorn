package org.unicorn.core;

import lombok.Data;

import java.util.List;

/**
 * @author czk
 */
@Data
public class MethodInfo {

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

}
