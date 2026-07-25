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

/**
 * 短信服务的参数配置
 *
 * @author Jastar Wang
 * @date 2023/4/5
 * @since 1.3.0
 */
public class JefwSmsProperties {
    /**
     * 可空，是否启用SMS模块，默认禁用
     */
    private Boolean enabled = false;
    /**
     * 可空，默认短信渠道
     * <li>若只检测到一个短信服务，则使用该服务</li>
     * <li>若有多个短信服务，则取第一个服务（根据枚举的顺序）</li>
     */
    private SmsChannel defaultChannel;
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
                ", tencent=" + tencent +
                ", dh3tong=" + dh3tong +
                '}';
    }

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
