package org.unicorn.spring.boot.autoconfigure;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.unicorn.core.Docket;
import org.unicorn.model.ProjectInstruction;
import org.unicorn.spring.boot.autoconfigure.properties.UnicornStatProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * @author czk
 */
@Configuration
@AutoConfigureOrder(Integer.MAX_VALUE)
@EnableConfigurationProperties(UnicornStatProperties.class)
public class UnicornAutoConfigure {

    @Bean
    public Docket docket(UnicornStatProperties properties) {
        System.setProperty("spring.unicorn.auto-startup", Boolean.toString(properties.isAutoStartup()));
        // 忽略类的class
        Set<String> ignoreClass = properties.getIgnoreClass();
        if (CollectionUtils.isEmpty(ignoreClass)) {
            ignoreClass.add("org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController");
        }
        // 忽略路径
        Set<String> ignorePath = properties.getIgnorePath();
        List<Function<String, Boolean>> ignoreList = new ArrayList<>(ignorePath.size());
        for (String path : ignorePath) {
            ignoreList.add(r -> new AntPathMatcher().match(path, r));
        }

        ProjectInstruction projectInstruction = new ProjectInstruction();
        BeanUtils.copyProperties(properties.getInstruction(), projectInstruction);

        ProjectInstruction.Contact contact = new ProjectInstruction.Contact();
        BeanUtils.copyProperties(properties.getInstruction().getContact(), contact);
        projectInstruction.setContact(contact);

        return Docket.builder()
                .ignoreClass(properties.getIgnoreClass())
                .ignorePath(ignoreList)
                .instruction(projectInstruction)
                .build();
    }

}
