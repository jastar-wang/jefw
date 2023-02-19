/*
 * Copyright (c) 2020 Jastar Wang
 * jefw is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.jastarwang.jefw.oss.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 对象存储服务的参数配置
 *
 * @author Jastar Wang
 * @date 2023/2/19
 * @since 1.2.4
 */
@Configuration
@ConfigurationProperties(prefix = "jefw.oss")
@ConditionalOnProperty(value = "jefw.oss.enabled", havingValue = "true", matchIfMissing = true)
public class JefwOssProperties {
    /**
     * 可空，是否启用OSS模块，默认启用
     */
    private Boolean enabled = true;
    /**
     * 阿里云OSS配置
     */
    private Aliyun aliyun;
    /**
     * 腾讯云COS配置
     */
    private Tencent tencent;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Aliyun getAliyun() {
        return aliyun;
    }

    public void setAliyun(Aliyun aliyun) {
        this.aliyun = aliyun;
    }

    public Tencent getTencent() {
        return tencent;
    }

    public void setTencent(Tencent tencent) {
        this.tencent = tencent;
    }

    @Override
    public String toString() {
        return "JefwOssProperties{" +
                "enabled=" + enabled +
                ", aliyun=" + aliyun +
                ", tencent=" + tencent +
                '}';
    }

    public static class Aliyun {
        /**
         * 可空，是否是HTTPS，默认是
         */
        private Boolean https = true;
        /**
         * 非空，密钥ID
         */
        private String accessKeyId;
        /**
         * 非空，密钥
         */
        private String accessKeySecret;
        /**
         * 非空，桶名称（只是为了方便业务，本库中不使用）
         */
        private String bucketName;
        /**
         * 非空，文件访问前缀（如：https://oss.xxx.com/，只是为了方便业务，本库中不使用）
         */
        private String urlPrefix;
        /**
         * 非空，访问站点（如：oss-cn-chengdu.aliyuncs.com）
         */
        private String endpoint;

        public Boolean getHttps() {
            return https;
        }

        public void setHttps(Boolean https) {
            this.https = https;
        }

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getAccessKeySecret() {
            return accessKeySecret;
        }

        public void setAccessKeySecret(String accessKeySecret) {
            this.accessKeySecret = accessKeySecret;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }

        public String getUrlPrefix() {
            return urlPrefix;
        }

        public void setUrlPrefix(String urlPrefix) {
            this.urlPrefix = urlPrefix;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        @Override
        public String toString() {
            return "Aliyun{" +
                    "https=" + https +
                    ", accessKeyId='" + accessKeyId + '\'' +
                    ", accessKeySecret='" + accessKeySecret + '\'' +
                    ", bucketName='" + bucketName + '\'' +
                    ", urlPrefix='" + urlPrefix + '\'' +
                    ", endpoint='" + endpoint + '\'' +
                    '}';
        }
    }

    public static class Tencent {
        /**
         * 可空，是否是HTTPS，默认是
         */
        private Boolean https = true;
        /**
         * 非空，密钥ID
         */
        private String accessKeyId;
        /**
         * 非空，密钥
         */
        private String accessKeySecret;
        /**
         * 非空，桶名称（只是为了方便业务，本库中不使用）
         */
        private String bucketName;
        /**
         * 非空，文件访问前缀（如：https://oss.xxx.com/，只是为了方便业务，本库中不使用）
         */
        private String urlPrefix;
        /**
         * 非空，地区（如：ap-chengdu）
         */
        private String region;

        public Boolean getHttps() {
            return https;
        }

        public void setHttps(Boolean https) {
            this.https = https;
        }

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getAccessKeySecret() {
            return accessKeySecret;
        }

        public void setAccessKeySecret(String accessKeySecret) {
            this.accessKeySecret = accessKeySecret;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }

        public String getUrlPrefix() {
            return urlPrefix;
        }

        public void setUrlPrefix(String urlPrefix) {
            this.urlPrefix = urlPrefix;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        @Override
        public String toString() {
            return "Tencent{" +
                    "https=" + https +
                    ", accessKeyId='" + accessKeyId + '\'' +
                    ", accessKeySecret='" + accessKeySecret + '\'' +
                    ", bucketName='" + bucketName + '\'' +
                    ", urlPrefix='" + urlPrefix + '\'' +
                    ", region='" + region + '\'' +
                    '}';
        }
    }

}
