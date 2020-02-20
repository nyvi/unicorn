package org.unicorn.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.unicorn.annotations.ApiIgnore;
import org.unicorn.annotations.ApiOperation;
import org.unicorn.core.Document;
import org.unicorn.core.DocumentCache;

import java.util.Map;

/**
 * @author czk
 */

@RestController
@RequestMapping("/api/")
public class UnicornControl {

    @ApiIgnore
    @ApiOperation("接口信息")
    @PostMapping("test")
    TestModel getApiDocs(TestModel testModel) {
        return null;
    }


    @ApiOperation("接口信息")
    @GetMapping("api-docs")
    Map<String, Document> apiDocs() {
        return DocumentCache.getAll();
    }
}
