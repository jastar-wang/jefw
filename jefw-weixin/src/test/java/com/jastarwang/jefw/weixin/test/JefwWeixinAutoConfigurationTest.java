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

import com.jastarwang.jefw.weixin.JefwWeixinDevMpService;
import com.jastarwang.jefw.weixin.JefwWeixinDevOaService;
import com.jastarwang.jefw.weixin.JefwWeixinPayJsapiService;
import com.jastarwang.jefw.weixin.JefwWeixinPayMpService;
import com.jastarwang.jefw.weixin.WeixinPayChannel;
import com.jastarwang.jefw.weixin.config.JefwWeixinProperties;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

/**
 * 离线验证微信模块和支付服务的自动装配条件。
 *
 * @author Jastar Wang
 * @since 2.5.1
 */
class JefwWeixinAutoConfigurationTest {

    /**
     * 通过应用使用的自动配置入口加载模块。
     *
     * @author Jastar Wang
     * @since 2.5.1
     */
    @EnableAutoConfiguration
    static class Application {
    }

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(Application.class);

    @Test
    void disabledModuleOverridesEnabledPay() {
        runner.withPropertyValues("jefw.weixin.enabled=false", "jefw.weixin.pay.enabled=true")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(JefwWeixinProperties.class)
                            .doesNotHaveBean(JefwWeixinDevOaService.class)
                            .doesNotHaveBean(JefwWeixinDevMpService.class);
                    assertPayAbsent(context);
                });
    }

    @Test
    void disablingPayKeepsDevelopmentServicesWithoutMerchantCredentials() {
        runner.withPropertyValues("jefw.weixin.enabled=true", "jefw.weixin.pay.enabled=false")
                .run(context -> {
                    assertThat(context.getBean(JefwWeixinProperties.class).getPay().isEnabled()).isFalse();
                    assertDevelopmentServicesOnly(context);
                });
    }

    @Test
    void missingSdkOverridesEnabledPay() {
        runner.withClassLoader(new FilteredClassLoader("com.wechat.pay.java"))
                .withPropertyValues("jefw.weixin.enabled=true", "jefw.weixin.pay.enabled=true")
                .run(this::assertDevelopmentServicesOnly);
    }

    @Test
    void legacyConfigurationEnablesPay() {
        assertPayEnabled(runner.withPropertyValues("jefw.weixin.enabled=true"));
    }

    @Test
    void explicitlyEnabledPayCreatesBothServices() {
        assertPayEnabled(runner.withPropertyValues("jefw.weixin.enabled=true", "jefw.weixin.pay.enabled=true"));
    }

    @Test
    void customPaymentServiceTakesPrecedence() {
        JefwWeixinPayMpService custom = mock(JefwWeixinPayMpService.class);
        runner.withPropertyValues("jefw.weixin.enabled=true")
                .withBean(Config.class, () -> mock(RSAAutoCertificateConfig.class, RETURNS_DEEP_STUBS))
                .withBean(JefwWeixinPayMpService.class, () -> custom)
                .run(context -> {
                    assertThat(context.getBean(JefwWeixinPayMpService.class)).isSameAs(custom);
                    assertThat(context).hasSingleBean(JefwWeixinPayJsapiService.class);
                });
    }

    private void assertPayEnabled(ApplicationContextRunner enabledRunner) {
        Config custom = mock(RSAAutoCertificateConfig.class, RETURNS_DEEP_STUBS);
        enabledRunner.withBean(Config.class, () -> custom).run(context -> {
            assertThat(context.getBean(Config.class)).isSameAs(custom);
            assertThat(context.getBean(JefwWeixinPayJsapiService.class).getChannel()).isEqualTo(WeixinPayChannel.JSAPI);
            assertThat(context.getBean(JefwWeixinPayMpService.class).getChannel()).isEqualTo(WeixinPayChannel.MINI_PROGRAM);
        });
    }

    private void assertDevelopmentServicesOnly(AssertableApplicationContext context) {
        assertThat(context).hasSingleBean(JefwWeixinDevOaService.class).hasSingleBean(JefwWeixinDevMpService.class);
        assertPayAbsent(context);
    }

    private void assertPayAbsent(AssertableApplicationContext context) {
        assertThat(context).doesNotHaveBean(Config.class)
                .doesNotHaveBean(JefwWeixinPayJsapiService.class)
                .doesNotHaveBean(JefwWeixinPayMpService.class);
    }
}
