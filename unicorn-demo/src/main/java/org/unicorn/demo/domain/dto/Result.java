package org.unicorn.demo.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author czk
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = -8888592905239088520L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String msg;

    /**
     * 返回数据
     */
    private T data;
}
