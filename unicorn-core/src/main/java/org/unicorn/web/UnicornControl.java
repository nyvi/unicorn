package org.unicorn.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.unicorn.annotations.Api;
import org.unicorn.annotations.ApiOperation;

/**
 * @author czk
 */
@Api("接口文档")
@RestController
@RequestMapping("/api/")
public class UnicornControl {

    @ApiOperation("接口信息")
    @RequestMapping({"/api-docs", "/getAPI"})
    String getApiDocs() {
        return "ok";
    }
}
