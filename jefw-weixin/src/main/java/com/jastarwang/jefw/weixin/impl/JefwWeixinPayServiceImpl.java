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
package com.jastarwang.jefw.weixin.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.math.MathUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.jastarwang.jefw.weixin.JefwWeixinPayService;
import com.jastarwang.jefw.weixin.config.JefwWeixinProperties;
import com.jastarwang.jefw.weixin.model.pay.OrderResponse;
import com.jastarwang.jefw.weixin.model.pay.RefundRequest;
import com.jastarwang.jefw.weixin.model.pay.RefundResponse;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.*;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 微信支付通用接口实现基类
 *
 * @author Jastar Wang
 * @date 2025/4/27
 * @since 2.2.0
 */
public abstract class JefwWeixinPayServiceImpl implements JefwWeixinPayService {

    protected final Config config;
    protected final JefwWeixinProperties jefwWeixinProperties;
    protected final RefundService refundService;
    protected final NotificationParser notificationParser;
    // 用于获取微信回调请求头中的数据以签名，注意不能修改顺序
    protected final List<String> WX_HEADER_KEYS = List.of("Wechatpay-Signature", "Wechatpay-Nonce", "Wechatpay-Timestamp",
            "Wechatpay-Serial", "Wechatpay-Signature-Type", "Request-ID");

    protected JefwWeixinPayServiceImpl(Config config, JefwWeixinProperties jefwWeixinProperties) {
        this.config = config;
        this.jefwWeixinProperties = jefwWeixinProperties;
        this.refundService = new RefundService.Builder().config(config).build();
        this.notificationParser = new NotificationParser((NotificationConfig) config);
    }

    protected abstract String getAppId();

    @Override
    public OrderResponse parseNotifyPay(HttpServletRequest request, String requestBody) {
        Map<String, String> wxHeaderMap = new HashMap<>(8);
        Transaction transaction = notificationParser.parse(buildParseNotifyParam(request, requestBody, wxHeaderMap), Transaction.class);
        OrderResponse response = new OrderResponse(transaction);
        response.setOriginHeader(JSONUtil.toJsonStr(wxHeaderMap));
        response.setOriginBody(requestBody);
        return response;
    }

    @Override
    public RefundResponse parseNotifyRefund(HttpServletRequest request, String requestBody) {
        Map<String, String> wxHeaderMap = new HashMap<>(8);
        RefundNotification notification = notificationParser.parse(buildParseNotifyParam(request, requestBody, wxHeaderMap), RefundNotification.class);
        RefundResponse response = new RefundResponse(notification);
        response.setOriginHeader(JSONUtil.toJsonStr(wxHeaderMap));
        response.setOriginBody(requestBody);
        return response;
    }

    @Override
    public RefundResponse queryRefund(String outRefundNo) {
        QueryByOutRefundNoRequest request = new QueryByOutRefundNoRequest();
        request.setOutRefundNo(outRefundNo);
        Refund refund = refundService.queryByOutRefundNo(request);
        return new RefundResponse(refund);
    }

    @Override
    public RefundResponse refund(RefundRequest param) {
        CreateRequest request = new CreateRequest();
        request.setTransactionId(param.getTransactionId());
        request.setOutTradeNo(param.getOutTradeNo());
        request.setOutRefundNo(param.getOutRefundNo());
        if (StrUtil.isNotBlank(param.getReason())) {
            request.setReason(param.getReason());
        }
        request.setNotifyUrl(param.getNotifyUrl());
        AmountReq amount = new AmountReq();
        amount.setCurrency("CNY");
        amount.setRefund(MathUtil.yuanToCent(param.getRefundAmount().doubleValue()));
        amount.setTotal(MathUtil.yuanToCent(param.getTotalAmount().doubleValue()));
        request.setAmount(amount);
        Refund refund = refundService.create(request);
        return new RefundResponse(refund);
    }

    private RequestParam buildParseNotifyParam(HttpServletRequest request, String requestBody, Map<String, String> wxHeaderMap) {
        // 获取请求头中的微信请求头
        Map<String, String> headerMap = getHeaderMap(request);
        WX_HEADER_KEYS.forEach(k -> wxHeaderMap.put(k, headerMap.get(k)));
        // 包装参数
        return new RequestParam.Builder()
                .serialNumber(wxHeaderMap.get(WX_HEADER_KEYS.get(3)))
                .timestamp(wxHeaderMap.get(WX_HEADER_KEYS.get(2)))
                .nonce(wxHeaderMap.get(WX_HEADER_KEYS.get(1)))
                .signature(wxHeaderMap.get(WX_HEADER_KEYS.get(0)))
                .signType(wxHeaderMap.get(WX_HEADER_KEYS.get(4)))
                .body(requestBody)
                .build();
    }

    /**
     * 工具方法：获取请求所有的头（header）信息
     * <p>见 hutool 的 JakartaServletUtil#getHeaderMap()方法</p>
     */
    protected Map<String, String> getHeaderMap(HttpServletRequest request) {
        final Map<String, String> headerMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        final Enumeration<String> names = request.getHeaderNames();
        String name;
        while (names.hasMoreElements()) {
            name = names.nextElement();
            headerMap.put(name, request.getHeader(name));
        }
        return headerMap;
    }

    /**
     * 工具方法：将时间对象格式化为 <a href="https://ijmacd.github.io/rfc3339-iso8601/">RFC 3339</a>格式
     * （微信支付中用到了，此格式不常用，即：yyyy-MM-dd'T'HH:mm:ssXXX）
     *
     * @param dateTime 时间
     * @return 格式化后的字符串，如：2023-02-11T01:26:19.147+08:00
     * @see cn.hutool.core.date.DatePattern#UTC_WITH_XXX_OFFSET_PATTERN
     */
    protected String formatRFC3339(LocalDateTime dateTime) {
        if (dateTime != null) {
            return dateTime.atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern(DatePattern.UTC_WITH_XXX_OFFSET_PATTERN));
        }
        return null;
    }

}
