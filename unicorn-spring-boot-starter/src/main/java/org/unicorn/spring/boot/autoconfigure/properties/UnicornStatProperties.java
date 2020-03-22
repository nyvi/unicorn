package org.unicorn.spring.boot.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

/**
 * @author czk
 */
@ConfigurationProperties("spring.unicorn")
public class UnicornStatProperties {

    /**
     * 是否启动
     */
    private boolean autoStartup = true;

    /**
     * 项目说明
     */
    private Instruction instruction = new Instruction();

    /**
     * 忽略路径
     */
    private Set<String> ignorePath = new HashSet<>();

    /**
     * 项目说明
     */
    public static class Instruction {

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
        private Contact contact = new Contact();

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTermsOfServiceUrl() {
            return termsOfServiceUrl;
        }

        public void setTermsOfServiceUrl(String termsOfServiceUrl) {
            this.termsOfServiceUrl = termsOfServiceUrl;
        }

        public String getLicense() {
            return license;
        }

        public void setLicense(String license) {
            this.license = license;
        }

        public String getLicenseUrl() {
            return licenseUrl;
        }

        public void setLicenseUrl(String licenseUrl) {
            this.licenseUrl = licenseUrl;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Contact getContact() {
            return contact;
        }

        public void setContact(Contact contact) {
            this.contact = contact;
        }
    }

    /**
     * 联系人
     */
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public boolean isAutoStartup() {
        return autoStartup;
    }

    public void setAutoStartup(boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public void setInstruction(Instruction instruction) {
        this.instruction = instruction;
    }

    public Set<String> getIgnorePath() {
        return ignorePath;
    }

    public void setIgnorePath(Set<String> ignorePath) {
        this.ignorePath = ignorePath;
    }

}
