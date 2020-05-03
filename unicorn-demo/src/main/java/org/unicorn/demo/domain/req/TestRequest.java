package org.unicorn.demo.domain.req;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.unicorn.annotations.ApiModelProperty;

/**
 * @author czk
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TestRequest extends DemoRequest {

    @ApiModelProperty("Test")
    private String testField;
}
