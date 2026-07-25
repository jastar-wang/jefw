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
import com.jastarwang.jefw.oss.impl.TencentJefwOssService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * OSS对象存储自动配置 - 腾讯云
 *
 * @author Jastar Wang
 * @date 2023/2/20
 * @since 1.2.4
 */
@Configuration
@ConditionalOnClass(COSClient.class)
@ConditionalOnProperty(value = "jefw.oss.enabled", havingValue = "true")
public class TencentJefwOssAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(COSClient.class)
    public COSClient tencentOssClient(JefwOssProperties jefwOssProperties) {
        COSCredentials cred = new BasicCOSCredentials(
                jefwOssProperties.getTencent().getAccessKeyId(),
                jefwOssProperties.getTencent().getAccessKeySecret()
        );
        Region region = new Region(jefwOssProperties.getTencent().getRegion());
        ClientConfig clientConfig = new ClientConfig(region);
        clientConfig.setHttpProtocol(jefwOssProperties.getTencent().getHttps() ? HttpProtocol.https : HttpProtocol.http);
        return new COSClient(cred, clientConfig);
    }

    @Order(230)
    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnMissingBean(name = "tencentJefwOssService")
    public JefwOssService tencentJefwOssService(COSClient tencentOssClient, JefwOssProperties jefwOssProperties) {
        return new TencentJefwOssService(tencentOssClient, jefwOssProperties.getTencent());
    }

}
