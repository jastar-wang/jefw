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

import com.jastarwang.jefw.weixin.*;
import com.jastarwang.jefw.weixin.config.JefwWeixinProperties;
import com.jastarwang.jefw.weixin.model.dev.AccessTokenResponse;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.cipher.SignatureResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 每次运行一个场景，公众号、小程序、商户支付可分别使用独立的测试账户。
 *
 * @author Jastar Wang
 * @date 2025/7/4
 * @since 2.2.0
 */
@EnabledIfEnvironmentVariable(named = "JEFW_WEIXIN_IT", matches = "true")
@SpringBootTest(classes = JefwWeixinServiceIT.Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class JefwWeixinServiceIT {

    /**
     * 启动当前微信测试场景所需的自动配置。
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
    private ApplicationContext context;

    @Autowired
    private JefwWeixinProperties properties;

    @BeforeAll
    static void validateScenario() {
        switch (required("JEFW_WEIXIN_SCENARIO")) {
            case "oa", "mp" -> {
                String prefix = "oa".equals(System.getenv("JEFW_WEIXIN_SCENARIO")) ? "JEFW_WEIXIN_OA_" : "JEFW_WEIXIN_MP_";
                required(prefix + "APP_ID");
                required(prefix + "APP_SECRET");
            }
            case "pay" -> {
                required("JEFW_WEIXIN_PAY_MCH_ID");
                required("JEFW_WEIXIN_PAY_MCH_SERIAL_NO");
                required("JEFW_WEIXIN_PAY_API_V3_KEY");
                String key = required("JEFW_WEIXIN_PAY_PRIVATE_KEY_PATH");
                assertNotNull(JefwWeixinServiceIT.class.getClassLoader().getResource(key),
                        "请用 -Dmaven.test.additionalClasspath 指定密钥所在目录，并将私钥路径设为相对资源名");
            }
            default -> throw new IllegalArgumentException("JEFW_WEIXIN_SCENARIO 须为 oa、mp 或 pay");
        }
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "JEFW_WEIXIN_SCENARIO", matches = "oa")
    void obtainsOfficialAccountToken() {
        JefwWeixinDevOaService service = context.getBean(JefwWeixinDevOaService.class);
        assertEquals(WeixinDevChannel.OFFICIAL_ACCOUNT, service.getChannel());
        verifyStableToken(service, properties.getOa().getAppId(), properties.getOa().getAppSecret());
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "JEFW_WEIXIN_SCENARIO", matches = "mp")
    void obtainsMiniProgramToken() {
        JefwWeixinDevMpService service = context.getBean(JefwWeixinDevMpService.class);
        assertEquals(WeixinDevChannel.MINI_PROGRAM, service.getChannel());
        verifyStableToken(service, properties.getMp().getAppId(), properties.getMp().getAppSecret());
    }

    private void verifyStableToken(JefwWeixinDevService service, String appId, String secret) {
        // 使用稳定版接口，不强制刷新已有凭证；断言只报告错误码，避免泄露令牌。
        assertTrue(context.getBeansOfType(Config.class).isEmpty(), "开发场景不应加载支付配置");
        AccessTokenResponse response = service.getStableAccessToken(appId, secret, false);
        assertNotNull(response);
        assertEquals(0, response.getErrcode());
        assertTrue(response.getAccessToken() != null && !response.getAccessToken().isBlank(), "缺少令牌");
        assertTrue(response.getExpiresIn() != null && response.getExpiresIn() > 0, "令牌有效期应大于零");
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "JEFW_WEIXIN_SCENARIO", matches = "pay")
    void signsLocallyWithConfiguredMerchant() {
        assertEquals(WeixinPayChannel.JSAPI, context.getBean(JefwWeixinPayJsapiService.class).getChannel());
        assertEquals(WeixinPayChannel.MINI_PROGRAM, context.getBean(JefwWeixinPayMpService.class).getChannel());
        // SDK 初始化时可能下载平台证书。此处仅签名本地测试文本，不创建交易或退款。
        SignatureResult signature = context.getBean(Config.class).createSigner().sign("jefw-weixin-it");
        assertEquals(properties.getPay().getMchSerialNo(), signature.getCertificateSerialNumber());
        assertTrue(signature.getSign() != null && !signature.getSign().isBlank(), "应返回商户签名");
    }

    private static String required(String name) {
        String value = System.getenv(name);
        assertTrue(value != null && !value.isBlank(), () -> "请设置环境变量 " + name);
        return value;
    }
}
