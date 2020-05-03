package org.unicorn.demo.control;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.unicorn.annotations.Api;
import org.unicorn.annotations.ApiOperation;
import org.unicorn.demo.domain.dto.DemoDTO;
import org.unicorn.demo.domain.dto.Result;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author czk
 */
@Api("test")
@RestController
public class TestControl {

    @ApiOperation("Result 返回")
    @RequestMapping("listTest")
    Result<List<DemoDTO>> listTest() {
        Result<List<DemoDTO>> result = new Result<>();
        List<DemoDTO> list = new ArrayList<>();
        DemoDTO demoDTO = new DemoDTO();
        demoDTO.setStringField("123");
        demoDTO.setIntField(1);
        demoDTO.setLongField(2L);
        demoDTO.setBigDecimalField(BigDecimal.ZERO);
        list.add(demoDTO);
        result.setData(list);
        result.setCode(200);
        result.setMsg("success");
        return result;
    }
}
