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
