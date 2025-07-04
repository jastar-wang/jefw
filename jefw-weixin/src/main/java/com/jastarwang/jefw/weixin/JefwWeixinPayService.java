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
package com.jastarwang.jefw.weixin;

import com.jastarwang.jefw.weixin.model.pay.OrderResponse;
import com.jastarwang.jefw.weixin.model.pay.RefundRequest;
import com.jastarwang.jefw.weixin.model.pay.RefundResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 微信支付通用接口
 * <p>各个细分渠道通用的接口会定义在此处，细分渠道定制化的接口则定义在对应渠道的接口中</p>
 *
 * @author Jastar Wang
 * @date 2025/4/27
 * @since 2.2.0
 */
public interface JefwWeixinPayService {
    /**
     * 获取实现渠道
     *
     * @return 实现渠道
     */
    WeixinPayChannel getChannel();

    /**
     * 解析微信支付回调
     *
     * @param request     非空，请求对象
     * @param requestBody 非空，回调请求体
     * @return 订单信息
     */
    OrderResponse parseNotifyPay(HttpServletRequest request, String requestBody);

    /**
     * 解析微信退款回调
     *
     * @param request     非空，请求对象
     * @param requestBody 非空，回调请求体
     * @return 退款信息
     */
    RefundResponse parseNotifyRefund(HttpServletRequest request, String requestBody);

    /**
     * 查询订单
     *
     * @param outTradeNo    业务订单号，二者选一即可，都传按业务订单号
     * @param transactionId 微信订单号，二者选一即可，都传按业务订单号
     * @return 订单信息
     */
    OrderResponse query(String outTradeNo, String transactionId);

    /**
     * 查询退款
     *
     * @param outRefundNo 非空，业务退款订单号
     * @return 退款信息
     */
    RefundResponse queryRefund(String outRefundNo);

    /**
     * 申请退款
     *
     * @param param 非空，退款请求
     * @return 退款信息
     */
    RefundResponse refund(RefundRequest param);

    /**
     * 关闭订单
     *
     * @param outTradeNo 业务订单号
     */
    void close(String outTradeNo);
}