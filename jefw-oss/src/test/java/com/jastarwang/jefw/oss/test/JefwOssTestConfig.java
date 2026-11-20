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

import com.jastarwang.jefw.oss.config.*;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Locale;

/**
 * 只加载选定渠道及公共模板，避免其他 SDK 因缺少凭据而阻止测试启动。
 *
 * @author Jastar Wang
 * @date 2025/4/22
 * @since 2.1.0
 */
@SpringBootConfiguration
@EnableConfigurationProperties
@Import({JefwOssAutoConfiguration.class, JefwOssTestConfig.ProviderSelector.class})
class JefwOssTestConfig {

    /**
     * 根据测试渠道导入对应厂商的自动配置。
     *
     * @author Jastar Wang
     * @date 2026/11/20
     * @since 2.5.1
     */
    static class ProviderSelector implements ImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata metadata) {
            String channel = System.getenv().getOrDefault("JEFW_OSS_DEFAULT_CHANNEL", "");
            Class<?> config = switch (channel.toLowerCase(Locale.ROOT)) {
                case "aliyun" -> AliyunJefwOssAutoConfiguration.class;
                case "tencent" -> TencentJefwOssAutoConfiguration.class;
                case "qiniu" -> QiniuJefwOssAutoConfiguration.class;
                case "minio" -> MinioJefwOssAutoConfiguration.class;
                default -> throw new IllegalArgumentException(
                        "请将 JEFW_OSS_DEFAULT_CHANNEL 设置为 aliyun、tencent、qiniu 或 minio");
            };
            return new String[]{config.getName()};
        }
    }
}
