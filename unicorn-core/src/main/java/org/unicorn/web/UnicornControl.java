package org.unicorn.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.unicorn.annotations.ApiIgnore;
import org.unicorn.core.Document;
import org.unicorn.core.DocumentCache;
import org.unicorn.core.ModelInfo;

import java.util.List;
import java.util.Map;

/**
 * @author czk
 */
@ApiIgnore
@RestController
@RequestMapping("/api/")
public class UnicornControl {

    @GetMapping("api-docs")
    Map<String, Document> apiDocs() {
        return DocumentCache.getAllDocs();
    }

    @GetMapping("api-model")
    Map<String, List<ModelInfo>> apiModel() {
        return DocumentCache.getAllModel();
    }

}
