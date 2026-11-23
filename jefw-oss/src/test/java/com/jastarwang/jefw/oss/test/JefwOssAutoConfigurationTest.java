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
package com.jastarwang.jefw.oss.test;

import com.jastarwang.jefw.oss.JefwOssService;
import com.jastarwang.jefw.oss.JefwOssTemplate;
import com.jastarwang.jefw.oss.OssChannel;
import com.jastarwang.jefw.oss.config.JefwOssProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 离线验证多厂商 SDK 共存时的渠道装配和默认路由。
 *
 * @author Jastar Wang
 * @date 2026/11/23
 * @since 2.5.1
 */
class JefwOssAutoConfigurationTest {
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(JefwOssTestConfig.class)
            .withPropertyValues("jefw.oss.enabled=true");

    private final ApplicationContextRunner withMinioCredentials = runner.withPropertyValues(
            "jefw.oss.minio.endpoint=http://127.0.0.1:9000",
            "jefw.oss.minio.access-key-id=test-user",
            "jefw.oss.minio.access-key-secret=test-secret");

    @Test
    void configuresOnlySelectedProviderWithAllSdksPresent() {
        withMinioCredentials.withPropertyValues("jefw.oss.enabled-channels=minio").run(context -> {
            assertThat(context).hasSingleBean(JefwOssService.class).hasBean("minioOssClient")
                    .doesNotHaveBean("aliyunOssClient").doesNotHaveBean("tencentOssClient")
                    .doesNotHaveBean("qiniuOssClient");
            assertThat(context.getBean(JefwOssTemplate.class).getDefaultChannel()).isEqualTo(OssChannel.MINIO);
        });
    }

    @Test
    void indexedListEnablesBothServicesAndRoutesToExplicitDefault() {
        withMinioCredentials.withPropertyValues("jefw.oss.enabled-channels[0]=qiniu",
                "jefw.oss.enabled-channels[1]=minio", "jefw.oss.default-channel=minio",
                "jefw.oss.qiniu.region=z0", "jefw.oss.qiniu.access-key-id=test-user",
                "jefw.oss.qiniu.access-key-secret=test-secret").run(context -> {
            assertThat(context.getBeansOfType(JefwOssService.class))
                    .containsOnlyKeys("qiniuJefwOssService", "minioJefwOssService");
            JefwOssTemplate template = context.getBean(JefwOssTemplate.class);
            assertThat(template.getDefaultService()).isSameAs(template.getService(OssChannel.MINIO));
            assertThat(template.getService(OssChannel.QINIU)).isSameAs(context.getBean("qiniuJefwOssService"));
        });
    }

    @Test
    void omittedListDetectsInstalledSdk() {
        withMinioCredentials.withClassLoader(new FilteredClassLoader("com.aliyun.oss", "com.qcloud.cos", "com.qiniu"))
                .run(context -> assertThat(context.getBean(JefwOssTemplate.class).getDefaultChannel())
                        .isEqualTo(OssChannel.MINIO));
    }

    @Test
    void rejectsEmptyList() {
        runner.withPropertyValues("jefw.oss.enabled-channels=").run(context ->
                assertThat(context.getStartupFailure()).rootCause().hasMessageContaining("enabled-channels must contain"));
    }

    @Test
    void rejectsDefaultOutsideSelection() {
        runner.withPropertyValues("jefw.oss.enabled-channels=minio", "jefw.oss.default-channel=tencent").run(context ->
                assertThat(context.getStartupFailure()).rootCause().hasMessageContaining("default-channel=TENCENT")
                        .hasMessageContaining("enabled-channels=[MINIO]"));
    }

    @Test
    void reportsMissingProviderEvenWhenAnotherSelectedSdkIsAvailable() {
        withMinioCredentials.withClassLoader(new FilteredClassLoader("com.qiniu"))
                .withPropertyValues("jefw.oss.enabled-channels=minio,qiniu").run(context ->
                        assertThat(context.getStartupFailure()).rootCause().hasMessageContaining("enabled-channels includes QINIU"));
    }

    /**
     * 使用普通集合注入创建应用自定义的存储模板。
     *
     * @author Jastar Wang
     * @date 2026/11/23
     * @since 2.5.1
     */
    static class CustomTemplateConfiguration {
        @Bean
        JefwOssTemplate customTemplate(JefwOssProperties properties, List<JefwOssService> services) {
            return new JefwOssTemplate(properties, services);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void validatesAndRoutesTheSamePrototypeService(boolean customTemplate) {
        List<JefwOssService> created = new ArrayList<>();
        ApplicationContextRunner application = customTemplate
                ? runner.withUserConfiguration(CustomTemplateConfiguration.class) : runner;
        application.withPropertyValues("jefw.oss.enabled-channels=local")
                .withBean(JefwOssService.class, () -> {
                    JefwOssService service = mock(JefwOssService.class);
                    when(service.getChannel()).thenReturn(OssChannel.LOCAL);
                    created.add(service);
                    return service;
                }, definition -> definition.setScope("prototype"))
                .run(context -> assertThat(created).containsExactly(
                        context.getBean(JefwOssTemplate.class).getDefaultService()));
    }

    @Test
    void lookingUpListBeansDoesNotCreateOssServices() {
        runner.withBean(JefwOssTemplate.class, () -> mock(JefwOssTemplate.class))
                .withClassLoader(new FilteredClassLoader("com.aliyun.oss", "com.qcloud.cos", "com.qiniu", "io.minio"))
                .withBean(JefwOssService.class, () -> {
                    throw new AssertionError("查询 List Bean 时不应创建渠道服务");
                }, definition -> definition.setScope("prototype"))
                .run(context -> assertThat(context.getBeansOfType(List.class)).isEmpty());
    }

    @Test
    void customTemplateStillRequiresSelectedChannelService() {
        runner.withBean(JefwOssTemplate.class, () -> mock(JefwOssTemplate.class))
                .withClassLoader(new FilteredClassLoader("io.minio"))
                .withPropertyValues("jefw.oss.enabled-channels=minio").run(context ->
                        assertThat(context.getStartupFailure()).rootCause().hasMessageContaining("enabled-channels includes MINIO"));
    }

    @Test
    void customTemplateCanUseUserServiceWithoutSdk() {
        JefwOssTemplate template = mock(JefwOssTemplate.class);
        JefwOssService service = mock(JefwOssService.class);
        when(service.getChannel()).thenReturn(OssChannel.MINIO);
        runner.withBean(JefwOssTemplate.class, () -> template)
                .withBean(JefwOssService.class, () -> service)
                .withClassLoader(new FilteredClassLoader("io.minio"))
                .withPropertyValues("jefw.oss.enabled-channels=minio").run(context ->
                        assertThat(context.getBean(JefwOssTemplate.class)).isSameAs(template));
    }

    @Test
    void missingSdksWithoutSelectionRetainsExistingError() {
        runner.withClassLoader(new FilteredClassLoader("com.aliyun.oss", "com.qcloud.cos", "com.qiniu", "io.minio"))
                .run(context -> assertThat(context.getStartupFailure()).rootCause().hasMessageContaining("no available JefwOssService"));
    }

    @Test
    void acceptsUserServiceForSelectedChannelWithoutSdk() {
        JefwOssService service = mock(JefwOssService.class);
        when(service.getChannel()).thenReturn(OssChannel.TENCENT);
        runner.withBean(JefwOssService.class, () -> service)
                .withClassLoader(new FilteredClassLoader("com.qcloud.cos"))
                .withPropertyValues("jefw.oss.enabled-channels=tencent").run(context ->
                        assertThat(context.getBean(JefwOssTemplate.class).getDefaultService()).isSameAs(service));
    }

    @Test
    void disabledModuleTakesPrecedenceOverSelection() {
        runner.withPropertyValues("jefw.oss.enabled=false", "jefw.oss.enabled-channels=minio").run(context ->
                assertThat(context).doesNotHaveBean(JefwOssTemplate.class).doesNotHaveBean(JefwOssService.class));
    }

}
