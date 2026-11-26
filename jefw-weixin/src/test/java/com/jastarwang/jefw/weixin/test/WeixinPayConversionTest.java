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
package com.jastarwang.jefw.weixin.test;

import com.jastarwang.jefw.weixin.config.JefwWeixinProperties;
import com.jastarwang.jefw.weixin.impl.JefwWeixinPayJsapiServiceImpl;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

/**
 * 微信支付转换测试类
 *
 * @author Jastar Wang
 * @since 2.5.1
 */
class WeixinPayConversionTest {
    private final JefwWeixinPayJsapiServiceImpl service = new JefwWeixinPayJsapiServiceImpl(
            mock(RSAAutoCertificateConfig.class, RETURNS_DEEP_STUBS), new JefwWeixinProperties());

    @Test
    void readsCallbackHeadersRegardlessOfCase() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("wechatpay-signature", "signature");
        request.addHeader("WECHATPAY-SERIAL", "serial");
        request.addHeader("wechatpay-timestamp", "123");
        request.addHeader("wechatpay-nonce", "nonce");
        request.addHeader("wechatpay-signature-type", "type");
        request.addHeader("request-id", "id");
        Map<String, String> headers = new HashMap<>();
        RequestParam param = ReflectionTestUtils.invokeMethod(service, "buildParseNotifyParam", request, "body", headers);
        assertNotNull(param);
        assertEquals("signature", param.getSignature());
        assertEquals("serial", param.getSerialNumber());
        assertEquals("type", param.getSignType());
        assertEquals("123\nnonce\nbody\n", param.getMessage());
        assertEquals("id", headers.get("Request-ID"));
    }

    @Test
    void convertsExactFenIncludingUpperBound() {
        assertEquals(1, amount("0.010").getTotal());
        assertEquals(Integer.MAX_VALUE, amount("21474836.47").getTotal());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"0", "-0.01", "0.001", "21474836.48", "42949672.97"})
    void rejectsInvalidPrepayAmount(String yuan) {
        assertThrows(IllegalArgumentException.class, () -> amount(yuan));
    }

    private Amount amount(String yuan) {
        BigDecimal value = yuan == null ? null : new BigDecimal(yuan);
        return ReflectionTestUtils.invokeMethod(service, "getAmount", new Object[]{value});
    }
}
