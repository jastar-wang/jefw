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
package com.jastarwang.jefw.sms.test;

import com.jastarwang.jefw.sms.JefwSmsException;
import com.jastarwang.jefw.sms.JefwSmsService;
import com.jastarwang.jefw.sms.JefwSmsTemplate;
import com.jastarwang.jefw.sms.SmsChannel;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 按选定的短信渠道验证自动配置，真实发送需要单独开启。
 *
 * @author Jastar Wang
 * @date 2023/4/5
 * @since 1.3.0
 */
@EnabledIfEnvironmentVariable(named = "JEFW_SMS_IT", matches = "true")
@SpringBootTest(classes = JefwSmsServiceIT.Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class JefwSmsServiceIT {

    /**
     * 启动短信测试所选渠道的自动配置。
     *
     * @author Jastar Wang
     * @date 2026/11/20
     * @since 2.5.1
     */
    @SpringBootConfiguration
    @EnableAutoConfiguration
    static class Application {
    }

    @Autowired
    private JefwSmsTemplate template;

    @Autowired
    private List<JefwSmsService> services;

    private static SmsChannel channel;

    @BeforeAll
    static void selectChannel() {
        channel = switch (required("JEFW_SMS_DEFAULT_CHANNEL")) {
            case "tencent" -> {
                required("JEFW_SMS_TENCENT_APP_ID");
                required("JEFW_SMS_TENCENT_SECRET_ID");
                required("JEFW_SMS_TENCENT_SECRET_KEY");
                required("JEFW_SMS_TENCENT_SIGN_NAME");
                yield SmsChannel.TENCENT;
            }
            case "dh3tong" -> {
                required("JEFW_SMS_DH3TONG_ACCOUNT");
                required("JEFW_SMS_DH3TONG_PASSWORD");
                yield SmsChannel.DH3TONG;
            }
            default -> throw new IllegalArgumentException("JEFW_SMS_DEFAULT_CHANNEL 须为 tencent 或 dh3tong");
        };
    }

    @Test
    void routesOnlyToSelectedChannel() {
        assertEquals(1, services.size(), "只应注册选中的短信服务");
        assertEquals(channel, services.get(0).getChannel());
        assertEquals(channel, template.getDefaultChannel());
        assertSame(services.get(0), template.getDefaultService());
        assertSame(services.get(0), template.getService(channel));
        if (channel == SmsChannel.TENCENT) {
            assertInstanceOf(SmsClient.class, services.get(0).prototype());
        }
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "JEFW_SMS_SEND", matches = "true")
    void sendsOneTestMessage() {
        String phone = required("JEFW_SMS_PHONES");
        assertFalse(phone.contains(","), "真实发送仅接受一个测试手机号");
        String message = required(channel == SmsChannel.TENCENT
                ? "JEFW_SMS_TENCENT_TEMPLATE_ID" : "JEFW_SMS_DH3TONG_TEMPLATE");

        // 腾讯云按顺序读取参数，大汉三通按模板中的 {code}、{time} 替换。
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("code", "123456");
        parameters.put("time", 3);
        try {
            template.send(channel, phone, message, parameters);
        } catch (JefwSmsException e) {
            // 厂商的原始异常可能含有手机号及完整请求，避免写入测试报告。
            fail("短信发送失败，请在所选服务商的控制台核对发送记录");
        }
    }

    private static String required(String name) {
        String value = System.getenv(name);
        assertTrue(value != null && !value.isBlank(), () -> "请设置环境变量 " + name);
        return value;
    }
}
