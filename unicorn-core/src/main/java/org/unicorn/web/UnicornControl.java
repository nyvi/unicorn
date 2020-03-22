package org.unicorn.web;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.unicorn.annotations.ApiIgnore;
import org.unicorn.core.Docket;
import org.unicorn.core.DocumentCache;
import org.unicorn.model.Document;
import org.unicorn.model.ModelInfo;
import org.unicorn.model.ProjectInstruction;

import java.util.List;
import java.util.Map;

/**
 * @author czk
 */
@ApiIgnore
@RestController("unicornControl")
@AllArgsConstructor
@RequestMapping("/unicorn/")
public class UnicornControl {

    private final ApplicationContext applicationContext;

    @GetMapping("api-docs")
    Map<String, Document> apiDocs() {
        return DocumentCache.getAllDocs();
    }

    @GetMapping("api-model")
    Map<String, List<ModelInfo>> apiModel() {
        return DocumentCache.getAllModel();
    }


    @GetMapping("api-instruction")
    ProjectInstruction instruction() {
        Docket docket = applicationContext.getBean(Docket.class);
        return docket.getInstruction();
    }


}
