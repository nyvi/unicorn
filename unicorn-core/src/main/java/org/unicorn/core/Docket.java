package org.unicorn.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.unicorn.model.ProjectInstruction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * @author czk
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Docket {

    /**
     * 项目说明
     */
    private ProjectInstruction instruction;

    /**
     * 忽略路径
     */
    private List<Function<String, Boolean>> ignorePath = new ArrayList<>();

    /**
     * 忽略的bean
     */
    private Set<String> ignoreClass = new HashSet<>();

}
