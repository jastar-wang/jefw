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
@ConditionalOnProperty(value = "jefw.oss.enabled", havingValue = "true", matchIfMissing = true)
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

    @Order(300)
    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnMissingBean(name = "tencentJefwOssService")
    public JefwOssService tencentJefwOssService(COSClient cosClient) {
        return new TencentJefwOssService(cosClient);
    }

}
