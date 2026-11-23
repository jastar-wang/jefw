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
package com.jastarwang.jefw.oss.config;

import com.jastarwang.jefw.oss.JefwOssService;
import com.jastarwang.jefw.oss.OssChannel;
import com.jastarwang.jefw.oss.impl.MinioJefwOssService;
import io.minio.MinioAsyncClient;
import io.minio.MinioClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * OSS对象存储自动配置 - MinIO
 *
 * @author Jastar Wang
 * @date 2026/4/27
 * @since 2.3.0
 */
@Configuration
@ConditionalOnOssChannel(OssChannel.MINIO)
@ConditionalOnClass(MinioClient.class)
@ConditionalOnProperty(value = "jefw.oss.enabled", havingValue = "true")
public class MinioJefwOssAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MinioClient.class)
    public MinioClient minioOssClient(JefwOssProperties jefwOssProperties) {
        JefwOssProperties.Minio config = jefwOssProperties.getMinio();
        return MinioClient.builder()
                .endpoint(config.endpointUrl())
                .credentials(config.getAccessKeyId(), config.getAccessKeySecret())
                .build();
    }

    @Order(250)
    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnMissingBean(name = "minioJefwOssService")
    public JefwOssService minioJefwOssService(MinioClient minioOssClient, JefwOssProperties jefwOssProperties,
                                              ObjectProvider<MinioAsyncClient> listingClient) {
        return new MinioJefwOssService(minioOssClient, jefwOssProperties.getMinio(), listingClient.getIfAvailable());
    }

}
