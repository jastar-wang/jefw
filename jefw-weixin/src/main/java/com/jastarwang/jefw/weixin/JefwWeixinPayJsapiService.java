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

import com.jastarwang.jefw.weixin.model.pay.PrepayJsapiRequest;
import com.jastarwang.jefw.weixin.model.pay.PrepayJsapiResponse;

/**
 * 微信支付JSAPI接口
 *
 * @author Jastar Wang
 * @date 2025/4/27
 * @since 2.2.0
 */
public interface JefwWeixinPayJsapiService extends JefwWeixinPayService {
    /**
     * 发起预支付下单
     *
     * @param request 非空，预支付参数
     * @return 非空，预支付响应
     */
    PrepayJsapiResponse prepay(PrepayJsapiRequest request);
}