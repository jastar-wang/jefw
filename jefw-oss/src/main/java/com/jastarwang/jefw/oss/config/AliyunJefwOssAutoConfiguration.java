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

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.jastarwang.jefw.oss.JefwOssService;
import com.jastarwang.jefw.oss.OssChannel;
import com.jastarwang.jefw.oss.impl.AliyunJefwOssService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * OSS对象存储自动配置 - 阿里云
 *
 * @author Jastar Wang
 * @since 1.2.0
 */
@Configuration
@ConditionalOnOssChannel(OssChannel.ALIYUN)
@ConditionalOnClass(OSS.class)
@ConditionalOnProperty(value = "jefw.oss.enabled", havingValue = "true")
public class AliyunJefwOssAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(OSS.class)
    public OSS aliyunOssClient(JefwOssProperties jefwOssProperties) {
        return new OSSClientBuilder().build(
                jefwOssProperties.getAliyun().getEndpoint(),
                jefwOssProperties.getAliyun().getAccessKeyId(),
                jefwOssProperties.getAliyun().getAccessKeySecret()
        );
    }

    @Order(220)
    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnMissingBean(name = "aliyunJefwOssService")
    public JefwOssService aliyunJefwOssService(OSS aliyunOssClient, JefwOssProperties jefwOssProperties) {
        return new AliyunJefwOssService(aliyunOssClient, jefwOssProperties.getAliyun());
    }

}
