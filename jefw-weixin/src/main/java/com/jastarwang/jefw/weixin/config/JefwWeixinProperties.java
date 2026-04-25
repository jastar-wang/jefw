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
package com.jastarwang.jefw.weixin.config;

/**
 * 微信开发的参数配置
 *
 * @author Jastar Wang
 * @date 2025/4/9
 * @since 2.2.0
 */
public class JefwWeixinProperties {
    /**
     * 可空，是否启用微信模块，默认禁用
     */
    private Boolean enabled = false;
    /**
     * 小程序
     */
    private MiniProgram mp;
    /**
     * 公众号
     */
    private OfficialAccount oa;
    /**
     * 微信支付
     */
    private Pay pay;

    public static class MiniProgram {
        private String appId;
        private String appSecret;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getAppSecret() {
            return appSecret;
        }

        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }

        @Override
        public String toString() {
            return "MiniProgram{" +
                    "appId='" + appId + '\'' +
                    ", appSecret='" + mask(appSecret) + '\'' +
                    '}';
        }
    }

    public static class OfficialAccount {
        private String appId;
        private String appSecret;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getAppSecret() {
            return appSecret;
        }

        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }

        @Override
        public String toString() {
            return "OfficialAccount{" +
                    "appId='" + appId + '\'' +
                    ", appSecret='" + mask(appSecret) + '\'' +
                    '}';
        }
    }

    public static class Pay {
        /**
         * 微信支付：商户号
         */
        private String mchId;
        /**
         * 微信支付：商户证书序列号
         */
        private String mchSerialNo;
        /**
         * 微信支付：商户API V3密钥
         */
        private String apiV3Key;
        /**
         * 微信支付：商户API私钥路径（放在resource目录下，无需带classpath）
         */
        private String privateKeyPath;

        public String getMchId() {
            return mchId;
        }

        public void setMchId(String mchId) {
            this.mchId = mchId;
        }

        public String getMchSerialNo() {
            return mchSerialNo;
        }

        public void setMchSerialNo(String mchSerialNo) {
            this.mchSerialNo = mchSerialNo;
        }

        public String getApiV3Key() {
            return apiV3Key;
        }

        public void setApiV3Key(String apiV3Key) {
            this.apiV3Key = apiV3Key;
        }

        public String getPrivateKeyPath() {
            return privateKeyPath;
        }

        public void setPrivateKeyPath(String privateKeyPath) {
            this.privateKeyPath = privateKeyPath;
        }

        @Override
        public String toString() {
            return "Pay{" +
                    "mchId='" + mchId + '\'' +
                    ", mchSerialNo='" + mchSerialNo + '\'' +
                    ", apiV3Key='" + mask(apiV3Key) + '\'' +
                    ", privateKeyPath='" + privateKeyPath + '\'' +
                    '}';
        }
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public MiniProgram getMp() {
        return mp;
    }

    public void setMp(MiniProgram mp) {
        this.mp = mp;
    }

    public OfficialAccount getOa() {
        return oa;
    }

    public void setOa(OfficialAccount oa) {
        this.oa = oa;
    }

    public Pay getPay() {
        return pay;
    }

    public void setPay(Pay pay) {
        this.pay = pay;
    }

    @Override
    public String toString() {
        return "JefwWeixinProperties{" +
                "enabled=" + enabled +
                ", mp=" + mp +
                ", oa=" + oa +
                ", pay=" + pay +
                '}';
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
