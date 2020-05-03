package org.unicorn.demo.domain.dto;

import lombok.Data;
import org.unicorn.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author czk
 */
@Data
public class DemoDTO implements Serializable {

    private static final long serialVersionUID = -5776057381291392582L;
    
    @ApiModelProperty("String 类型")
    private String stringField;

    @ApiModelProperty("Integer 类型")
    private Integer intField;

    @ApiModelProperty("Long 类型")
    private Long longField;

    @ApiModelProperty("BigDecimal 类型")
    private BigDecimal bigDecimalField;
}
