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
package com.jastarwang.jefw.weixin.model.dev;

import com.jastarwang.jefw.weixin.model.BaseResponse;

import java.io.Serial;

/**
 * 小程序用户手机号信息
 *
 * @author Jastar Wang
 * @since 2.2.0
 */
public class PhoneNumberResponse extends BaseResponse {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户绑定的手机号（国外手机号会有区号）
     */
    private String phoneNumber;
    /**
     * 没有区号的手机号
     */
    private String purePhoneNumber;
    /**
     * 区号
     */
    private String countryCode;
    /**
     * 数据水印
     */
    private Watermark watermark;

    /**
     * 微信手机号数据附带的应用标识与时间戳。
     *
     * @author Jastar Wang
     * @since 2.2.0
     */
    public static class Watermark {
        /**
         * 用户获取手机号操作的时间戳
         */
        private Long timestamp;
        /**
         * 小程序appid
         */
        private String appId;

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        @Override
        public String toString() {
            return "Watermark{" +
                    "timestamp=" + timestamp +
                    ", appId='" + appId + '\'' +
                    '}';
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPurePhoneNumber() {
        return purePhoneNumber;
    }

    public void setPurePhoneNumber(String purePhoneNumber) {
        this.purePhoneNumber = purePhoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Watermark getWatermark() {
        return watermark;
    }

    public void setWatermark(Watermark watermark) {
        this.watermark = watermark;
    }

    @Override
    public String toString() {
        return "PhoneNumberResponse{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", purePhoneNumber='" + purePhoneNumber + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", watermark=" + watermark +
                "} " + super.toString();
    }

}