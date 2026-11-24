/*
 * Copyright 2020 Jastar Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jastarwang.jefw.oss.config;

import cn.hutool.core.util.StrUtil;
import com.jastarwang.jefw.oss.OssChannel;

import java.util.Objects;
import java.util.Set;

/**
 * 对象存储服务的参数配置
 *
 * @author Jastar Wang
 * @date 2023/2/19
 * @since 1.2.4
 */
public class JefwOssProperties {
    /**
     * 可空，是否启用OSS模块，默认禁用
     */
    private Boolean enabled = false;
    /**
     * 可空，默认存储渠道。省略时，按 Spring 注入顺序取第一个已启用的服务。
     * 多渠道时建议显式指定，enabled-channels 的书写顺序不决定默认渠道。
     */
    private OssChannel defaultChannel;
    /**
     * 可空，启用的存储渠道。省略时按类路径中的 SDK 自动装配，显式配置时至少选择一个渠道。
     */
    private Set<OssChannel> enabledChannels;
    /**
     * 阿里云OSS配置
     */
    private Aliyun aliyun;
    /**
     * 腾讯云COS配置
     */
    private Tencent tencent;
    /**
     * 七牛云KODO配置
     */
    private Qiniu qiniu;
    /**
     * MinIO配置
     */
    private Minio minio;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public OssChannel getDefaultChannel() {
        return defaultChannel;
    }

    public void setDefaultChannel(OssChannel defaultChannel) {
        this.defaultChannel = defaultChannel;
    }

    public Set<OssChannel> getEnabledChannels() {
        return enabledChannels;
    }

    public void setEnabledChannels(Set<OssChannel> enabledChannels) {
        this.enabledChannels = enabledChannels;
    }

    /**
     * 校验渠道名单和默认渠道的配置。
     */
    public void validate() {
        if (enabledChannels == null) {
            return;
        }
        if (enabledChannels.isEmpty()) {
            throw new IllegalStateException("jefw.oss.enabled-channels must contain at least one channel. To disable OSS, set jefw.oss.enabled=false.");
        }
        if (enabledChannels.stream().anyMatch(Objects::isNull)) {
            throw new IllegalStateException("jefw.oss.enabled-channels contains an empty entry. Specify a channel for each item.");
        }
        if (defaultChannel != null && !enabledChannels.contains(defaultChannel)) {
            throw new IllegalStateException("jefw.oss.default-channel=" + defaultChannel +
                    " must be included in jefw.oss.enabled-channels=" + enabledChannels);
        }
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

    public Qiniu getQiniu() {
        return qiniu;
    }

    public void setQiniu(Qiniu qiniu) {
        this.qiniu = qiniu;
    }

    public Minio getMinio() {
        return minio;
    }

    public void setMinio(Minio minio) {
        this.minio = minio;
    }

    @Override
    public String toString() {
        return "JefwOssProperties{" +
                "enabled=" + enabled +
                ", defaultChannel=" + defaultChannel +
                ", enabledChannels=" + enabledChannels +
                ", aliyun=" + aliyun +
                ", tencent=" + tencent +
                ", qiniu=" + qiniu +
                ", minio=" + minio +
                '}';
    }

    public static class Base {
        /**
         * 可空，是否是HTTPS，默认是
         */
        protected Boolean https = true;
        /**
         * 非空，密钥ID
         */
        protected String accessKeyId;
        /**
         * 非空，密钥
         */
        protected String accessKeySecret;
        /**
         * 非空，桶名称（只是为了方便业务，本库中不使用）
         */
        protected String bucketName;
        /**
         * 非空，文件访问前缀（如：https://oss.xxx.com/，只是为了方便业务，本库中不使用）
         */
        protected String urlPrefix;

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
    }

    public static class Aliyun extends Base {
        /**
         * 非空，访问站点（如：oss-cn-chengdu.aliyuncs.com）
         */
        private String endpoint;

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
                    ", accessKeySecret='" + mask(accessKeySecret) + '\'' +
                    ", bucketName='" + bucketName + '\'' +
                    ", urlPrefix='" + urlPrefix + '\'' +
                    ", endpoint='" + endpoint + '\'' +
                    '}';
        }
    }

    public static class Tencent extends Base {
        /**
         * 非空，地区（如：ap-chengdu）
         */
        private String region;

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
                    ", accessKeySecret='" + mask(accessKeySecret) + '\'' +
                    ", bucketName='" + bucketName + '\'' +
                    ", urlPrefix='" + urlPrefix + '\'' +
                    ", region='" + region + '\'' +
                    '}';
        }
    }

    public static class Qiniu extends Base {
        /**
         * 非空，地区（如：z0）
         */
        private String region;

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        @Override
        public String toString() {
            return "Qiniu{" +
                    "https=" + https +
                    ", accessKeyId='" + accessKeyId + '\'' +
                    ", accessKeySecret='" + mask(accessKeySecret) + '\'' +
                    ", bucketName='" + bucketName + '\'' +
                    ", urlPrefix='" + urlPrefix + '\'' +
                    ", region='" + region + '\'' +
                    '}';
        }
    }

    public static class Minio extends Base {
        /**
         * 非空，访问站点（如：127.0.0.1:9000 或 https://minio.xxx.com）
         */
        private String endpoint;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        /**
         * 返回带协议的 MinIO 端点。
         */
        public String endpointUrl() {
            if (StrUtil.startWithAnyIgnoreCase(endpoint, "http://", "https://")) {
                return endpoint;
            }
            return (getHttps() ? "https://" : "http://") + endpoint;
        }

        @Override
        public String toString() {
            return "Minio{" +
                    "https=" + https +
                    ", accessKeyId='" + accessKeyId + '\'' +
                    ", accessKeySecret='" + mask(accessKeySecret) + '\'' +
                    ", bucketName='" + bucketName + '\'' +
                    ", urlPrefix='" + urlPrefix + '\'' +
                    ", endpoint='" + endpoint + '\'' +
                    '}';
        }
    }

    private static String mask(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        if (value.length() <= 4) {
            return "*".repeat(value.length());
        }
        return value.substring(0, 2) + "***" + value.substring(value.length() - 2);
    }

}
