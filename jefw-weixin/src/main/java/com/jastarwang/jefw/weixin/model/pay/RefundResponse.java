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
import com.wechat.pay.java.service.refund.model.Amount;
import com.wechat.pay.java.service.refund.model.Refund;
import com.wechat.pay.java.service.refund.model.RefundNotification;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 退款请求响应
 *
 * @author Jastar Wang
 * @since 2.2.0
 */
public class RefundResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 微信退款订单号
     */
    private String refundId;
    /**
     * 业务退款订单号
     */
    private String outRefundNo;
    /**
     * 原交易微信订单号
     */
    private String transactionId;
    /**
     * 原交易业务订单号
     */
    private String outTradeNo;
    /**
     * 退款入账账户
     */
    private String userReceivedAccount;
    /**
     * 退款成功时间（内部转换）
     */
    private LocalDateTime successTime;
    /**
     * 退款受理时间（内部转换）
     */
    private LocalDateTime createTime;
    /**
     * 订单金额（内部转换）
     */
    private BigDecimal totalAmount;
    /**
     * 退款金额（内部转换）
     */
    private BigDecimal refundAmount;
    /**
     * 用户支付金额：现金支付金额（内部转换）
     */
    private BigDecimal payerTotalAmount;
    /**
     * 用户退款金额：退款给用户的金额（内部转换）
     */
    private BigDecimal payerRefundAmount;
    /**
     * 应结退款金额：去掉非充值代金券退款金额后的退款金额，退款金额=申请退款金额-非充值代金券退款金额，退款金额<=申请退款金额（内部转换）
     */
    private BigDecimal settlementRefundAmount;
    /**
     * 应结订单金额：应结订单金额=订单金额-免充值代金券金额，应结订单金额<=订单金额（内部转换）
     */
    private BigDecimal settlementTotalAmount;
    /**
     * 优惠退款金额：优惠退款金额<=退款金额，退款金额-代金券或立减优惠退款金额为现金，说明详见代金券或立减优惠（内部转换）
     */
    private BigDecimal discountRefundAmount;
    /**
     * 手续费退款金额（内部转换）
     */
    private BigDecimal refundFeeAmount;
    /**
     * 退款渠道（内部转换）
     */
    private Channel channel;
    /**
     * 退款状态（内部转换）
     */
    private Status status;

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
     * <p>（即为Refund或RefundNotification订单对象）</p>
     */
    private String originBodyResource;

    public RefundResponse() {
    }

    public RefundResponse(Refund refund) {
        this.refundId = refund.getRefundId();
        this.outRefundNo = refund.getOutRefundNo();
        this.transactionId = refund.getTransactionId();
        this.outTradeNo = refund.getOutTradeNo();
        this.userReceivedAccount = refund.getUserReceivedAccount();
        if (StrUtil.isNotBlank(refund.getSuccessTime())) {
            this.successTime = LocalDateTime.parse(refund.getSuccessTime(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        if (StrUtil.isNotBlank(refund.getCreateTime())) {
            this.createTime = LocalDateTime.parse(refund.getCreateTime(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        if (refund.getAmount() != null) {
            Amount amount = refund.getAmount();
            if (amount.getTotal() != null) {
                this.totalAmount = BigDecimal.valueOf(MathUtil.centToYuan(amount.getTotal()));
            }
            if (amount.getRefund() != null) {
                this.refundAmount = BigDecimal.valueOf(MathUtil.centToYuan(amount.getRefund()));
            }
            if (amount.getPayerTotal() != null) {
                this.payerTotalAmount = BigDecimal.valueOf(MathUtil.centToYuan(amount.getPayerTotal()));
            }
            if (amount.getPayerRefund() != null) {
                this.payerRefundAmount = BigDecimal.valueOf(MathUtil.centToYuan(amount.getPayerRefund()));
            }
            if (amount.getSettlementTotal() != null) {
                this.settlementTotalAmount = BigDecimal.valueOf(MathUtil.centToYuan(amount.getSettlementTotal()));
            }
            if (amount.getSettlementRefund() != null) {
                this.settlementRefundAmount = BigDecimal.valueOf(MathUtil.centToYuan(amount.getSettlementRefund()));
            }
            if (amount.getDiscountRefund() != null) {
                this.discountRefundAmount = BigDecimal.valueOf(MathUtil.centToYuan(amount.getDiscountRefund()));
            }
            if (amount.getRefundFee() != null) {
                this.refundFeeAmount = BigDecimal.valueOf(MathUtil.centToYuan(amount.getRefundFee()));
            }
        }
        if (refund.getChannel() != null) {
            this.channel = Channel.valueOf(refund.getChannel().name());
        }
        if (refund.getStatus() != null) {
            this.status = Status.valueOf(refund.getStatus().name());
        }
        this.originBodyResource = JSONUtil.toJsonStr(refund);
    }

    public RefundResponse(RefundNotification refund) {
        this.refundId = refund.getRefundId();
        this.outRefundNo = refund.getOutRefundNo();
        this.transactionId = refund.getTransactionId();
        this.outTradeNo = refund.getOutTradeNo();
        this.userReceivedAccount = refund.getUserReceivedAccount();
        if (StrUtil.isNotBlank(refund.getSuccessTime())) {
            this.successTime = LocalDateTime.parse(refund.getSuccessTime(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        if (StrUtil.isNotBlank(refund.getCreateTime())) {
            this.createTime = LocalDateTime.parse(refund.getCreateTime(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        if (refund.getAmount() != null) {
            Amount amount = refund.getAmount();
            if (amount.getTotal() != null) {
                this.totalAmount = BigDecimal.valueOf(MathUtil.centToYuan(amount.getTotal()));
            }
            if (amount.getRefund() != null) {
                this.refundAmount = BigDecimal.valueOf(MathUtil.centToYuan(amount.getRefund()));
            }
            if (amount.getPayerTotal() != null) {
                this.payerTotalAmount = BigDecimal.valueOf(MathUtil.centToYuan(amount.getPayerTotal()));
            }
            if (amount.getPayerRefund() != null) {
                this.payerRefundAmount = BigDecimal.valueOf(MathUtil.centToYuan(amount.getPayerRefund()));
            }
            if (amount.getSettlementTotal() != null) {
                this.settlementTotalAmount = BigDecimal.valueOf(MathUtil.centToYuan(amount.getSettlementTotal()));
            }
            if (amount.getSettlementRefund() != null) {
                this.settlementRefundAmount = BigDecimal.valueOf(MathUtil.centToYuan(amount.getSettlementRefund()));
            }
            if (amount.getDiscountRefund() != null) {
                this.discountRefundAmount = BigDecimal.valueOf(MathUtil.centToYuan(amount.getDiscountRefund()));
            }
            if (amount.getRefundFee() != null) {
                this.refundFeeAmount = BigDecimal.valueOf(MathUtil.centToYuan(amount.getRefundFee()));
            }
        }
        if (refund.getChannel() != null) {
            this.channel = Channel.valueOf(refund.getChannel().name());
        }
        if (refund.getRefundStatus() != null) {
            this.status = Status.valueOf(refund.getRefundStatus().name());
        }
        this.originBodyResource = JSONUtil.toJsonStr(refund);
    }

    /**
     * 微信支付退款渠道。
     *
     * @author Jastar Wang
     * @since 2.2.0
     */
    public enum Channel {
        /**
         * 原路退款
         */
        ORIGINAL,
        /**
         * 退回到余额
         */
        BALANCE,
        /**
         * 原账户异常退到其他余额账户
         */
        OTHER_BALANCE,
        /**
         * 原银行卡异常退到其他银行卡
         */
        OTHER_BANKCARD;
    }

    /**
     * 微信支付退款状态。
     *
     * @author Jastar Wang
     * @since 2.2.0
     */
    public enum Status {
        /**
         * 退款成功
         */
        SUCCESS,
        /**
         * 退款关闭
         */
        CLOSED,
        /**
         * 退款处理中
         */
        PROCESSING,
        /**
         * 退款异常
         */
        ABNORMAL;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public String getOutRefundNo() {
        return outRefundNo;
    }

    public void setOutRefundNo(String outRefundNo) {
        this.outRefundNo = outRefundNo;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getUserReceivedAccount() {
        return userReceivedAccount;
    }

    public void setUserReceivedAccount(String userReceivedAccount) {
        this.userReceivedAccount = userReceivedAccount;
    }

    public LocalDateTime getSuccessTime() {
        return successTime;
    }

    public void setSuccessTime(LocalDateTime successTime) {
        this.successTime = successTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public BigDecimal getPayerTotalAmount() {
        return payerTotalAmount;
    }

    public void setPayerTotalAmount(BigDecimal payerTotalAmount) {
        this.payerTotalAmount = payerTotalAmount;
    }

    public BigDecimal getPayerRefundAmount() {
        return payerRefundAmount;
    }

    public void setPayerRefundAmount(BigDecimal payerRefundAmount) {
        this.payerRefundAmount = payerRefundAmount;
    }

    public BigDecimal getSettlementRefundAmount() {
        return settlementRefundAmount;
    }

    public void setSettlementRefundAmount(BigDecimal settlementRefundAmount) {
        this.settlementRefundAmount = settlementRefundAmount;
    }

    public BigDecimal getSettlementTotalAmount() {
        return settlementTotalAmount;
    }

    public void setSettlementTotalAmount(BigDecimal settlementTotalAmount) {
        this.settlementTotalAmount = settlementTotalAmount;
    }

    public BigDecimal getDiscountRefundAmount() {
        return discountRefundAmount;
    }

    public void setDiscountRefundAmount(BigDecimal discountRefundAmount) {
        this.discountRefundAmount = discountRefundAmount;
    }

    public BigDecimal getRefundFeeAmount() {
        return refundFeeAmount;
    }

    public void setRefundFeeAmount(BigDecimal refundFeeAmount) {
        this.refundFeeAmount = refundFeeAmount;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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
        return "RefundResponse{" +
                "refundId='" + refundId + '\'' +
                ", outRefundNo='" + outRefundNo + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", outTradeNo='" + outTradeNo + '\'' +
                ", userReceivedAccount='" + userReceivedAccount + '\'' +
                ", successTime=" + successTime +
                ", createTime=" + createTime +
                ", totalAmount=" + totalAmount +
                ", refundAmount=" + refundAmount +
                ", payerTotalAmount=" + payerTotalAmount +
                ", payerRefundAmount=" + payerRefundAmount +
                ", settlementRefundAmount=" + settlementRefundAmount +
                ", settlementTotalAmount=" + settlementTotalAmount +
                ", discountRefundAmount=" + discountRefundAmount +
                ", refundFeeAmount=" + refundFeeAmount +
                ", channel=" + channel +
                ", status=" + status +
                ", originHeader='" + originHeader + '\'' +
                ", originBody='" + originBody + '\'' +
                ", originBodyResource='" + originBodyResource + '\'' +
                '}';
    }

}
