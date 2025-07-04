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
import java.math.BigDecimal;

/**
 * 退款请求参数
 *
 * @author Jastar Wang
 * @date 2022/12/28
 * @since 2.2.0
 */
public class RefundRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 非空，原交易微信订单号
     */
    private String transactionId;
    /**
     * 非空，原交易业务订单号
     */
    private String outTradeNo;
    /**
     * 非空，新生成的退款订单号
     */
    private String outRefundNo;
    /**
     * 非空，原交易订单金额
     */
    private BigDecimal totalAmount;
    /**
     * 非空，退款金额
     */
    private BigDecimal refundAmount;
    /**
     * 非空，回调通知地址
     */
    private String notifyUrl;
    /**
     * 可空，退款原因（下发给用户）
     */
    private String reason;

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

    public String getOutRefundNo() {
        return outRefundNo;
    }

    public void setOutRefundNo(String outRefundNo) {
        this.outRefundNo = outRefundNo;
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

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "RefundRequest{" +
                "transactionId='" + transactionId + '\'' +
                ", outTradeNo='" + outTradeNo + '\'' +
                ", outRefundNo='" + outRefundNo + '\'' +
                ", totalAmount=" + totalAmount +
                ", refundAmount=" + refundAmount +
                ", notifyUrl='" + notifyUrl + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }

}
