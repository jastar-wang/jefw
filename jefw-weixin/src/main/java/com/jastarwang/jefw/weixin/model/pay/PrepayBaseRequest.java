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
package com.jastarwang.jefw.weixin.model.pay;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预支付请求基础参数
 *
 * @author Jastar Wang
 * @since 2.2.0
 */
public class PrepayBaseRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 非空，商品描述
     * <p>最长127个字符，用户在微信账单中可见</p>
     */
    protected String description;
    /**
     * 非空，商户订单号
     * <p>6-32位，只能是数字、大小写字母_-*</p>
     */
    protected String outTradeNo;
    /**
     * 非空，订单金额（单位：CNY）
     */
    protected BigDecimal totalAmount;
    /**
     * 非空，回调通知地址
     */
    protected String notifyUrl;
    /**
     * 非空，用户IP地址
     */
    protected String payerClientIp;

    /**
     * 可空，订单支付失效时间（如：2018-06-08T10:34:56+08:00）
     * <p>超过此时间后用户无法再支付，但这不意味着订单关闭，关闭订单需要调用关单接口</p>
     */
    protected LocalDateTime payExpireTime;
    /**
     * 可空，自定义附加数据
     * <p>最长128字符，对用户不可见，查询订单接口和回调通知接口均会返回此信息</p>
     */
    protected String attach;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getPayerClientIp() {
        return payerClientIp;
    }

    public void setPayerClientIp(String payerClientIp) {
        this.payerClientIp = payerClientIp;
    }

    public LocalDateTime getPayExpireTime() {
        return payExpireTime;
    }

    public void setPayExpireTime(LocalDateTime payExpireTime) {
        this.payExpireTime = payExpireTime;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    @Override
    public String toString() {
        return "PrepayBaseRequest{" +
                "description='" + description + '\'' +
                ", outTradeNo='" + outTradeNo + '\'' +
                ", totalAmount=" + totalAmount +
                ", notifyUrl='" + notifyUrl + '\'' +
                ", payerClientIp='" + payerClientIp + '\'' +
                ", payExpireTime=" + payExpireTime +
                ", attach='" + attach + '\'' +
                '}';
    }

}
