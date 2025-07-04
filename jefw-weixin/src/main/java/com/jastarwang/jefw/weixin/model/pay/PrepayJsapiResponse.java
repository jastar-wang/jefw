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
package com.jastarwang.jefw.weixin.model.pay;

import java.io.Serial;
import java.io.Serializable;

/**
 * 预支付请求响应（JSAPI）
 *
 * @author Jastar Wang
 * @date 2022/12/28
 * @since 2.2.0
 */
public class PrepayJsapiResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 调用微信预支付接口返回的数据
     */
    private String prepayId;
    /**
     * 固定：prepay_id=上面的prepayId
     */
    private String prepayPkg;
    /**
     * 当前时间戳（秒，10位数字）
     */
    private String timeStamp;
    /**
     * 随机字符串
     */
    private String nonceStr;
    /**
     * 签名方式，默认为RSA，仅支持RSA
     */
    private String signType;
    /**
     * 签名内容
     */
    private String paySign;

    public String getPrepayId() {
        return prepayId;
    }

    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId;
    }

    public String getPrepayPkg() {
        return prepayPkg;
    }

    public void setPrepayPkg(String prepayPkg) {
        this.prepayPkg = prepayPkg;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getPaySign() {
        return paySign;
    }

    public void setPaySign(String paySign) {
        this.paySign = paySign;
    }

    @Override
    public String toString() {
        return "PrepayJsapiResponse{" +
                "prepayId='" + prepayId + '\'' +
                ", prepayPkg='" + prepayPkg + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", nonceStr='" + nonceStr + '\'' +
                ", signType='" + signType + '\'' +
                ", paySign='" + paySign + '\'' +
                '}';
    }

}
