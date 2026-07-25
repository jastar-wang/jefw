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

import cn.hutool.core.math.MathUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.wechat.pay.java.service.payments.model.Transaction;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 支付订单请求响应
 *
 * @author Jastar Wang
 * @date 2022/12/28
 * @since 2.2.0
 */
public class OrderResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 应用ID
     */
    private String appId;
    /**
     * 商户订单号
     */
    private String outTradeNo;
    /**
     * 微信订单号
     */
    private String transactionId;
    /**
     * 附加数据
     */
    private String attach;
    /**
     * 交易状态描述
     */
    private String tradeStateDesc;

    /**
     * 支付完成时间（内部转换）
     */
    private LocalDateTime successTime;
    /**
     * 用户openid（内部转换）
     */
    private String payerOpenid;
    /**
     * 交易金额（内部转换）
     */
    private BigDecimal totalAmount;
    /**
     * 用户实付交易金额（内部转换）
     */
    private BigDecimal payerTotalAmount;
    /**
     * 交易状态（内部转换）
     */
    private TradeStateEnum tradeState;
    /**
     * 交易类型（内部转换）
     */
    private TradeTypeEnum tradeType;

    /**
     * 回调场景返回：原请求头（Wechatpay-*）转json后的数据
     */
    private String originHeader;
    /**
     * 回调场景返回：原请求体数据
     */
    private String originBody;
    /**
     * 所有场景返回：原请求体解密出来后的resource字段转json后的数据
     * <p>（即为Transaction订单对象）</p>
     */
    private String originBodyResource;

    public OrderResponse() {
    }

    public OrderResponse(Transaction transaction) {
        this.appId = transaction.getAppid();
        this.outTradeNo = transaction.getOutTradeNo();
        this.transactionId = transaction.getTransactionId();
        this.attach = transaction.getAttach();
        this.tradeStateDesc = transaction.getTradeStateDesc();
        if (transaction.getPayer() != null) {
            this.payerOpenid = transaction.getPayer().getOpenid();
        }
        if (StrUtil.isNotBlank(transaction.getSuccessTime())) {
            this.successTime = LocalDateTime.parse(transaction.getSuccessTime(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        if (transaction.getAmount() != null) {
            if (transaction.getAmount().getTotal() != null) {
                this.totalAmount = BigDecimal.valueOf(MathUtil.centToYuan(transaction.getAmount().getTotal()));
            }
            if (transaction.getAmount().getPayerTotal() != null) {
                this.payerTotalAmount = BigDecimal.valueOf(MathUtil.centToYuan(transaction.getAmount().getPayerTotal()));
            }
        }
        if (transaction.getTradeState() != null) {
            this.tradeState = TradeStateEnum.valueOf(transaction.getTradeState().name());
        }
        if (transaction.getTradeType() != null) {
            this.tradeType = TradeTypeEnum.valueOf(transaction.getTradeType().name());
        }
        this.originBodyResource = JSONUtil.toJsonStr(transaction);
    }

    public enum TradeStateEnum {
        /**
         * 支付成功
         */
        SUCCESS,
        /**
         * 转入退款
         */
        REFUND,
        /**
         * 未支付
         */
        NOTPAY,
        /**
         * 已关闭
         */
        CLOSED,
        /**
         * 已撤销（付款码支付）
         */
        REVOKED,
        /**
         * 用户支付中（付款码支付）
         */
        USERPAYING,
        /**
         * 支付失败(其他原因，如银行返回失败)
         */
        PAYERROR,

        ACCEPT;
    }

    public enum TradeTypeEnum {
        /**
         * 公众号支付、小程序支付
         */
        JSAPI,
        /**
         * 扫码支付
         */
        NATIVE,
        /**
         * APP支付
         */
        APP,
        /**
         * 付款码支付
         */
        MICROPAY,
        /**
         * H5支付
         */
        MWEB,
        /**
         * 刷脸支付
         */
        FACEPAY;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getTradeStateDesc() {
        return tradeStateDesc;
    }

    public void setTradeStateDesc(String tradeStateDesc) {
        this.tradeStateDesc = tradeStateDesc;
    }

    public LocalDateTime getSuccessTime() {
        return successTime;
    }

    public void setSuccessTime(LocalDateTime successTime) {
        this.successTime = successTime;
    }

    public String getPayerOpenid() {
        return payerOpenid;
    }

    public void setPayerOpenid(String payerOpenid) {
        this.payerOpenid = payerOpenid;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPayerTotalAmount() {
        return payerTotalAmount;
    }

    public void setPayerTotalAmount(BigDecimal payerTotalAmount) {
        this.payerTotalAmount = payerTotalAmount;
    }

    public TradeStateEnum getTradeState() {
        return tradeState;
    }

    public void setTradeState(TradeStateEnum tradeState) {
        this.tradeState = tradeState;
    }

    public TradeTypeEnum getTradeType() {
        return tradeType;
    }

    public void setTradeType(TradeTypeEnum tradeType) {
        this.tradeType = tradeType;
    }

    public String getOriginHeader() {
        return originHeader;
    }

    public void setOriginHeader(String originHeader) {
        this.originHeader = originHeader;
    }

    public String getOriginBody() {
        return originBody;
    }

    public void setOriginBody(String originBody) {
        this.originBody = originBody;
    }

    public String getOriginBodyResource() {
        return originBodyResource;
    }

    public void setOriginBodyResource(String originBodyResource) {
        this.originBodyResource = originBodyResource;
    }

    @Override
    public String toString() {
        return "OrderResponse{" +
                "appId='" + appId + '\'' +
                ", outTradeNo='" + outTradeNo + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", attach='" + attach + '\'' +
                ", tradeStateDesc='" + tradeStateDesc + '\'' +
                ", successTime=" + successTime +
                ", payerOpenid='" + payerOpenid + '\'' +
                ", totalAmount=" + totalAmount +
                ", payerTotalAmount=" + payerTotalAmount +
                ", tradeState=" + tradeState +
                ", tradeType=" + tradeType +
                ", originHeader='" + originHeader + '\'' +
                ", originBody='" + originBody + '\'' +
                ", originBodyResource='" + originBodyResource + '\'' +
                '}';
    }

}
