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

import com.jastarwang.jefw.weixin.model.pay.PrepayJsapiRequest;
import com.jastarwang.jefw.weixin.model.pay.PrepayJsapiResponse;

/**
 * 微信支付 JSAPI 接口。
 *
 * @author Jastar Wang
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