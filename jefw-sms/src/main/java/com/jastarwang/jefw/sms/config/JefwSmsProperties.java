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
package com.jastarwang.jefw.sms.config;

import com.jastarwang.jefw.sms.SmsChannel;

import java.util.Objects;
import java.util.Set;

/**
 * 短信服务的参数配置
 *
 * @author Jastar Wang
 * @since 1.3.0
 */
public class JefwSmsProperties {
    /**
     * 可空，是否启用SMS模块，默认禁用
     */
    private Boolean enabled = false;
    /**
     * 可空，默认短信渠道。省略时，按 Spring 注入顺序取第一个已启用的服务。
     * 多渠道时建议显式指定，enabled-channels 的书写顺序不决定默认渠道。
     */
    private SmsChannel defaultChannel;
    /**
     * 可空，启用的短信渠道。省略时按厂商 SDK 和渠道配置装配。
     */
    private Set<SmsChannel> enabledChannels;
    /**
     * 阿里云
     */
    private Aliyun aliyun;
    /**
     * 腾讯云
     */
    private Tencent tencent;
    /**
     * 大汉三通
     */
    private Dh3Tong dh3tong;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public SmsChannel getDefaultChannel() {
        return defaultChannel;
    }

    public void setDefaultChannel(SmsChannel defaultChannel) {
        this.defaultChannel = defaultChannel;
    }

    public Set<SmsChannel> getEnabledChannels() {
        return enabledChannels;
    }

    public void setEnabledChannels(Set<SmsChannel> enabledChannels) {
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
            throw new IllegalStateException("jefw.sms.enabled-channels must contain at least one channel. To disable SMS, set jefw.sms.enabled=false.");
        }
        if (enabledChannels.stream().anyMatch(Objects::isNull)) {
            throw new IllegalStateException("jefw.sms.enabled-channels contains an empty entry. Specify a channel for each item.");
        }
        if (defaultChannel != null && !enabledChannels.contains(defaultChannel)) {
            throw new IllegalStateException("jefw.sms.default-channel=" + defaultChannel +
                    " must be included in jefw.sms.enabled-channels=" + enabledChannels);
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

    public Dh3Tong getDh3tong() {
        return dh3tong;
    }

    public void setDh3tong(Dh3Tong dh3tong) {
        this.dh3tong = dh3tong;
    }

    @Override
    public String toString() {
        return "JefwSmsProperties{" +
                "enabled=" + enabled +
                ", defaultChannel=" + defaultChannel +
                ", enabledChannels=" + enabledChannels +
                ", aliyun=" + aliyun +
                ", tencent=" + tencent +
                ", dh3tong=" + dh3tong +
                '}';
    }

    /**
     * 阿里云短信服务配置。
     *
     * @author Jastar Wang
     * @since 2.5.1
     */
    public static class Aliyun {
        /**
         * 非空，访问密钥 ID。
         */
        private String accessKeyId;
        /**
         * 非空，访问密钥 Secret。
         */
        private String accessKeySecret;
        /**
         * 非空，已审核通过的短信签名。
         */
        private String signName;
        /**
         * 可空，短信服务接入域名，默认使用公共端点。
         */
        private String endpoint = "dysmsapi.aliyuncs.com";

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

        public String getSignName() {
            return signName;
        }

        public void setSignName(String signName) {
            this.signName = signName;
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
                    "accessKeyId='" + mask(accessKeyId) + '\'' +
                    ", accessKeySecret='" + mask(accessKeySecret) + '\'' +
                    ", signName='" + signName + '\'' +
                    ", endpoint='" + endpoint + '\'' +
                    '}';
        }
    }

    /**
     * 腾讯云短信发送配置。
     *
     * @author Jastar Wang
     * @since 1.3.0
     */
    public static class Tencent {
        /**
         * 非空，短信应用ID
         */
        private String appId;
        /**
         * 非空，SecretId
         */
        private String secretId;
        /**
         * 非空，SecretKey
         */
        private String secretKey;
        /**
         * 非空，调用地址，一般形式为 *.tencentcloudapi.com
         */
        private String endpoint;
        /**
         * 非空，地域
         */
        private String region;
        /**
         * 非空，短信签名
         */
        private String signName;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getSecretId() {
            return secretId;
        }

        public void setSecretId(String secretId) {
            this.secretId = secretId;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getSignName() {
            return signName;
        }

        public void setSignName(String signName) {
            this.signName = signName;
        }

        @Override
        public String toString() {
            return "Tencent{" +
                    "appId='" + appId + '\'' +
                    ", secretId='" + mask(secretId) + '\'' +
                    ", secretKey='" + mask(secretKey) + '\'' +
                    ", endpoint='" + endpoint + '\'' +
                    ", region='" + region + '\'' +
                    ", signName='" + signName + '\'' +
                    '}';
        }
    }

    /**
     * 大汉三通短信发送配置。
     *
     * @author Jastar Wang
     * @since 1.3.0
     */
    public static class Dh3Tong {
        /**
         * 账号
         */
        private String account;
        /**
         * 密码
         */
        private String pwd;
        /**
         * API地址
         */
        private String apiUrl;

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getPwd() {
            return pwd;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }

        public String getApiUrl() {
            return apiUrl;
        }

        public void setApiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
        }

        @Override
        public String toString() {
            return "Dh3Tong{" +
                    "account='" + account + '\'' +
                    ", pwd='" + mask(pwd) + '\'' +
                    ", apiUrl='" + apiUrl + '\'' +
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
