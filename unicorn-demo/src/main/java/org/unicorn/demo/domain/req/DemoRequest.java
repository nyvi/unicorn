package org.unicorn.demo.domain.req;

import lombok.Data;
import org.unicorn.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * @author czk
 */
@Data
public class DemoRequest {

    @ApiModelProperty("String 类型")
    private String stringField;

    @ApiModelProperty("Integer 类型")
    private Integer intField;

    @ApiModelProperty("Long 类型")
    private Long longField;

    @ApiModelProperty("BigDecimal 类型")
    private BigDecimal bigDecimalField;
}
