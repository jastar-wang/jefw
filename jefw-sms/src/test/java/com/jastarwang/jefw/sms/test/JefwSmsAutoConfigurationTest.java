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

import com.jastarwang.jefw.sms.JefwSmsService;
import com.jastarwang.jefw.sms.JefwSmsTemplate;
import com.jastarwang.jefw.sms.SmsChannel;
import com.jastarwang.jefw.sms.config.JefwSmsProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.MapPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 离线验证短信渠道的装配条件和默认路由。
 *
 * @author Jastar Wang
 * @date 2026/11/23
 * @since 2.5.1
 */
class JefwSmsAutoConfigurationTest {
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(JefwSmsServiceIT.Application.class)
            .withPropertyValues("jefw.sms.enabled=true");

    private final ApplicationContextRunner withDh3Tong = runner.withPropertyValues(
            "jefw.sms.dh3tong.api-url=http://localhost/test");

    private final ApplicationContextRunner withBoth = withDh3Tong.withPropertyValues(
            "jefw.sms.tencent.secret-id=test-id", "jefw.sms.tencent.secret-key=test-key",
            "jefw.sms.tencent.region=ap-beijing", "jefw.sms.tencent.endpoint=localhost");

    @Test
    void selectsDh3TongWithTencentSdkPresentAndNoTencentCredentials() {
        withDh3Tong.withPropertyValues("jefw.sms.enabled-channels=dh3tong").run(context -> {
            assertThat(context.getBeansOfType(JefwSmsService.class)).containsOnlyKeys("dh3tongJefwSmsService");
            assertThat(context.getBean(JefwSmsTemplate.class).getDefaultChannel()).isEqualTo(SmsChannel.DH3TONG);
        });
    }

    @Test
    void sendingThroughUnavailableChannelReportsAvailableChannels() {
        withBoth.withPropertyValues("jefw.sms.enabled-channels=tencent").run(context -> {
            JefwSmsTemplate template = context.getBean(JefwSmsTemplate.class);
            assertThatIllegalArgumentException().isThrownBy(() -> template.send(SmsChannel.DH3TONG, "test", "test", null))
                    .withMessageContaining("DH3TONG").withMessageContaining("[TENCENT]");
        });
    }

    @Test
    void reinitializingTemplateKeepsSingleChannelRoute() {
        withDh3Tong.withPropertyValues("jefw.sms.enabled-channels=dh3tong").run(context -> {
            JefwSmsTemplate template = context.getBean(JefwSmsTemplate.class);
            JefwSmsService service = context.getBean(JefwSmsService.class);
            template.afterPropertiesSet();
            assertThat(template.getDefaultService()).isSameAs(service);
            assertThat(template.getService(SmsChannel.DH3TONG)).isSameAs(service);
        });
    }

    @Test
    void rejectsDuplicateEnabledChannel() {
        JefwSmsService service = mock(JefwSmsService.class);
        when(service.getChannel()).thenReturn(SmsChannel.DH3TONG);
        withDh3Tong.withPropertyValues("jefw.sms.enabled-channels=dh3tong")
                .withBean("customDh3Tong", JefwSmsService.class, () -> service)
                .run(context -> assertThat(context.getStartupFailure()).rootCause()
                        .isInstanceOf(IllegalStateException.class).hasMessageContaining("Multiple JefwSmsService")
                        .hasMessageContaining("DH3TONG"));
    }

    @Test
    void excludesUnselectedUserServiceFromTemplateRoutes() {
        JefwSmsService tencent = mock(JefwSmsService.class);
        when(tencent.getChannel()).thenReturn(SmsChannel.TENCENT);
        withDh3Tong.withPropertyValues("jefw.sms.enabled-channels=dh3tong")
                .withBean("customTencent", JefwSmsService.class, () -> tencent).run(context -> {
                    JefwSmsTemplate template = context.getBean(JefwSmsTemplate.class);
                    assertThat(context.getBean("customTencent")).isSameAs(tencent);
                    assertThat(template.getService(SmsChannel.TENCENT)).isNull();
                    assertThat(template.getDefaultChannel()).isEqualTo(SmsChannel.DH3TONG);
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"http://localhost/test", "${JEFW_TEST_MISSING_DH3TONG_URL}"})
    void excludesDh3TongWithoutResolvingItsApiUrl(String apiUrl) {
        withBoth.withPropertyValues("jefw.sms.enabled-channels=tencent", "jefw.sms.dh3tong.api-url=" + apiUrl)
                .run(context -> assertThat(context.getBeansOfType(JefwSmsService.class)).containsOnlyKeys("tencentJefwSmsService"));
    }

    @Test
    void selectsBothProvidersAndRoutesToConfiguredDefault() {
        withBoth.withPropertyValues("jefw.sms.enabled-channels[0]=dh3tong", "jefw.sms.enabled-channels[1]=tencent",
                "jefw.sms.default-channel=dh3tong").run(context -> {
            JefwSmsTemplate template = context.getBean(JefwSmsTemplate.class);
            assertThat(template.getDefaultService()).isSameAs(context.getBean("dh3tongJefwSmsService"));
            assertThat(template.getService(SmsChannel.TENCENT)).isSameAs(context.getBean("tencentJefwSmsService"));
        });
    }

    @Test
    void omittedListKeepsProviderConditionsAndDefaultOrder() {
        withBoth.run(context -> {
            assertThat(context.getBeansOfType(JefwSmsService.class))
                    .containsOnlyKeys("tencentJefwSmsService", "dh3tongJefwSmsService");
            assertThat(context.getBean(JefwSmsTemplate.class).getDefaultChannel()).isEqualTo(SmsChannel.TENCENT);
        });
    }

    @Test
    void selectedTencentRequiresSdkOrCustomService() {
        withDh3Tong.withClassLoader(new FilteredClassLoader("com.tencentcloudapi"))
                .withPropertyValues("jefw.sms.enabled-channels=dh3tong,tencent").run(context ->
                        assertThat(context.getStartupFailure()).rootCause().hasMessageContaining("TENCENT"));
    }

    @Test
    void selectedDh3TongStillRequiresApiUrlOrCustomService() {
        runner.withPropertyValues("jefw.sms.enabled-channels=dh3tong").run(context ->
                assertThat(context.getStartupFailure()).rootCause().hasMessageContaining("DH3TONG")
                        .hasMessageContaining("api-url"));
    }

    @Test
    void legacyConfigurationKeepsDh3TongPropertyPresenceRule() {
        runner.withClassLoader(new FilteredClassLoader("com.tencentcloudapi"))
                .withPropertyValues("jefw.sms.dh3tong.api-url=").run(context ->
                        assertThat(context).hasBean("dh3tongJefwSmsService"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " \t "})
    void selectedDh3TongRejectsBlankApiUrl(String apiUrl) {
        runner.withPropertyValues("jefw.sms.enabled-channels=dh3tong")
                .withInitializer(context -> context.getEnvironment().getPropertySources().addFirst(
                        new MapPropertySource("sms-test", Map.of("jefw.sms.dh3tong.api-url", apiUrl))))
                .run(context -> assertThat(context.getStartupFailure()).rootCause()
                        .hasMessageContaining("DH3TONG").hasMessageContaining("api-url"));
    }

    @Test
    void customDh3TongServiceCanReplaceProviderWithBlankApiUrl() {
        JefwSmsService service = mock(JefwSmsService.class);
        when(service.getChannel()).thenReturn(SmsChannel.DH3TONG);
        runner.withBean("customDh3Tong", JefwSmsService.class, () -> service)
                .withPropertyValues("jefw.sms.enabled-channels=dh3tong", "jefw.sms.dh3tong.api-url=")
                .run(context -> {
                    assertThat(context).doesNotHaveBean("dh3tongJefwSmsService");
                    assertThat(context.getBean(JefwSmsTemplate.class).getDefaultService()).isSameAs(service);
                });
    }

    @Test
    void rejectsEmptyList() {
        runner.withPropertyValues("jefw.sms.enabled-channels=").run(context ->
                assertThat(context.getStartupFailure()).rootCause().hasMessageContaining("enabled-channels must contain"));
    }

    @Test
    void rejectsEmptyEntryInChannelList() {
        withDh3Tong.withPropertyValues("jefw.sms.enabled-channels=dh3tong,").run(context ->
                assertThat(context.getStartupFailure()).rootCause().isInstanceOf(IllegalStateException.class)
                        .hasMessageContaining("jefw.sms.enabled-channels").hasMessageContaining("empty entry"));
    }

    @Test
    void rejectsDefaultOutsideSelection() {
        withDh3Tong.withPropertyValues("jefw.sms.enabled-channels=dh3tong", "jefw.sms.default-channel=tencent")
                .run(context -> assertThat(context.getStartupFailure()).rootCause().hasMessageContaining("default-channel=TENCENT")
                        .hasMessageContaining("enabled-channels=[DH3TONG]"));
    }

    @Test
    void disabledModuleTakesPrecedence() {
        runner.withPropertyValues("jefw.sms.enabled=false", "jefw.sms.enabled-channels=tencent").run(context ->
                assertThat(context).doesNotHaveBean(JefwSmsTemplate.class).doesNotHaveBean(JefwSmsService.class));
    }

    @Test
    void fallbackToAnotherChannelDoesNotSatisfySelection() {
        JefwSmsService fallback = mock(JefwSmsService.class);
        when(fallback.getChannel()).thenReturn(SmsChannel.DH3TONG);
        JefwSmsTemplate template = mock(JefwSmsTemplate.class);
        when(template.getService(any(SmsChannel.class))).thenReturn(fallback);
        runner.withClassLoader(new FilteredClassLoader("com.tencentcloudapi"))
                .withBean(JefwSmsService.class, () -> fallback)
                .withBean(JefwSmsTemplate.class, () -> template)
                .withPropertyValues("jefw.sms.enabled-channels=dh3tong,tencent").run(context ->
                        assertThat(context.getStartupFailure()).rootCause().hasMessageContaining("TENCENT"));
    }

    @Test
    void customTemplateStillRequiresSelectedService() {
        runner.withBean(JefwSmsTemplate.class, () -> mock(JefwSmsTemplate.class))
                .withPropertyValues("jefw.sms.enabled-channels=dh3tong").run(context ->
                        assertThat(context.getStartupFailure()).rootCause().hasMessageContaining("DH3TONG"));
    }

    /**
     * 通过普通集合注入接收应用自定义的短信服务。
     *
     * @author Jastar Wang
     * @date 2026/11/23
     * @since 2.5.1
     */
    static class CustomTemplateConfiguration {
        @Bean
        JefwSmsTemplate customTemplate(JefwSmsProperties properties, List<JefwSmsService> services) {
            return new JefwSmsTemplate(properties, services);
        }
    }

    @Test
    void customTemplateReusesRoutedPrototypeWithoutProviderSdk() {
        List<JefwSmsService> created = new ArrayList<>();
        runner.withUserConfiguration(CustomTemplateConfiguration.class)
                .withClassLoader(new FilteredClassLoader("com.tencentcloudapi"))
                .withPropertyValues("jefw.sms.enabled-channels=tencent")
                .withBean(JefwSmsService.class, () -> {
                    JefwSmsService service = mock(JefwSmsService.class);
                    when(service.getChannel()).thenReturn(SmsChannel.TENCENT);
                    created.add(service);
                    return service;
                }, definition -> definition.setScope("prototype"))
                .run(context -> assertThat(created).containsExactly(
                        context.getBean(JefwSmsTemplate.class).getDefaultService()));
    }
}
