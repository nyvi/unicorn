package org.unicorn.demo.control;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.unicorn.annotations.Api;
import org.unicorn.annotations.ApiOperation;
import org.unicorn.demo.domain.dto.DemoDTO;
import org.unicorn.demo.domain.req.DemoRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author czk
 */
@Api("demo")
@RestController
public class DemoControl {

    @ApiOperation("获取字符串")
    @RequestMapping("getString")
    String getString() {
        return "ok";
    }

    @ApiOperation("返回实体")
    @PostMapping("getDo")
    DemoDTO getDo(@RequestBody DemoRequest request) {
        DemoDTO demoDTO = new DemoDTO();
        demoDTO.setStringField("123");
        demoDTO.setIntField(1);
        demoDTO.setLongField(2L);
        demoDTO.setBigDecimalField(BigDecimal.ZERO);
        return demoDTO;
    }

    @ApiOperation("列表")
    @PostMapping("list")
    List<DemoDTO> list(@RequestBody DemoRequest request) {
        ArrayList<DemoDTO> list = new ArrayList<>();
        DemoDTO demoDTO = new DemoDTO();
        demoDTO.setStringField("123");
        demoDTO.setIntField(1);
        demoDTO.setLongField(2L);
        demoDTO.setBigDecimalField(BigDecimal.ZERO);
        list.add(demoDTO);
        return list;
    }

    @ApiOperation("map")
    @PostMapping("getMap")
    Map<String, DemoDTO> getMap(@RequestBody DemoRequest request) {
        Map<String, DemoDTO> objectObjectHashMap = new HashMap<>();
        DemoDTO demoDTO = new DemoDTO();
        demoDTO.setStringField("123");
        demoDTO.setIntField(1);
        demoDTO.setLongField(2L);
        demoDTO.setBigDecimalField(BigDecimal.ZERO);
        objectObjectHashMap.put("test", demoDTO);
        return objectObjectHashMap;
    }
}
