package org.unicorn.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 项目说明
 *
 * @author czk
 */
@Data
@NoArgsConstructor
public class ProjectInstruction {

    /**
     * 标题
     */
    private String title;
    /**
     * 描述
     */
    private String description;
    /**
     * 服务地址
     */
    private String termsOfServiceUrl;
    /**
     * license
     */
    private String license;
    /**
     * licenseUrl
     */
    private String licenseUrl;
    /**
     * 版本号
     */
    private String version;

    /**
     * 联系人
     */
    private Contact contact;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Contact {

        /**
         * 姓名
         */
        private String name;

        /**
         * url
         */
        private String url;

        /**
         * 邮件地址
         */
        private String email;
    }

}
