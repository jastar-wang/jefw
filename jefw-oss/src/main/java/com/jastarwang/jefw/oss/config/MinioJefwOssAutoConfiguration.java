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
package com.jastarwang.jefw.oss.config;

import cn.hutool.core.util.StrUtil;
import com.jastarwang.jefw.oss.JefwOssService;
import com.jastarwang.jefw.oss.impl.MinioJefwOssService;
import io.minio.MinioClient;
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
@ConditionalOnClass(MinioClient.class)
@ConditionalOnProperty(value = "jefw.oss.enabled", havingValue = "true")
public class MinioJefwOssAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MinioClient.class)
    public MinioClient minioOssClient(JefwOssProperties jefwOssProperties) {
        JefwOssProperties.Minio config = jefwOssProperties.getMinio();
        String endpoint = config.getEndpoint();
        if (!StrUtil.startWithAnyIgnoreCase(endpoint, "http://", "https://")) {
            endpoint = (config.getHttps() ? "https://" : "http://") + endpoint;
        }
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(config.getAccessKeyId(), config.getAccessKeySecret())
                .build();
    }

    @Order(250)
    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnMissingBean(name = "minioJefwOssService")
    public JefwOssService minioJefwOssService(MinioClient minioOssClient, JefwOssProperties jefwOssProperties) {
        return new MinioJefwOssService(minioOssClient, jefwOssProperties.getMinio());
    }

}
