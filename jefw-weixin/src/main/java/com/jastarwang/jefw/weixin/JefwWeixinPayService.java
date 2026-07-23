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