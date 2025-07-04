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
