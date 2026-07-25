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

/**
 * 预支付请求参数（JSAPI）
 *
 * @author Jastar Wang
 * @date 2022/12/28
 * @since 2.2.0
 */
public class PrepayJsapiRequest extends PrepayBaseRequest {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 非空，用户openid
     */
    private String payerOpenid;

    public String getPayerOpenid() {
        return payerOpenid;
    }

    public void setPayerOpenid(String payerOpenid) {
        this.payerOpenid = payerOpenid;
    }

    @Override
    public String toString() {
        return "PrepayJsapiRequest{" +
                "payerOpenid='" + payerOpenid + '\'' +
                "} " + super.toString();
    }

}
