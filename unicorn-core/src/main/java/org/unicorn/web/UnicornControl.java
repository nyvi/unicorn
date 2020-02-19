package org.unicorn.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.unicorn.annotations.Api;
import org.unicorn.annotations.ApiOperation;

import java.util.Map;

/**
 * @author czk
 */
@Api("接口文档")
@RestController
@RequestMapping("/api/")
public class UnicornControl {

    @ApiOperation("接口信息")
    @PostMapping({"/api-docs", "/getAPI"})
    TestModel getApiDocs(TestModel testModel) {
        return null;
    }

    @ApiOperation("接口信息")
    @GetMapping({"test"})
    Map<String, Integer> test() {
        return null;
    }
}
