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

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.jastarwang.jefw.oss.OssService;
import com.jastarwang.jefw.oss.impl.OssServiceAliyunImpl;
import com.jastarwang.jefw.oss.impl.OssServiceTencentImpl;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OSS对象存储自动配置
 * FIXME 优化为可动态选择实现
 *
 * @author Jastar Wang
 * @date 2021/9/14
 * @since 1.2.0
 */
@Configuration
@ConditionalOnProperty(value = "jefw.oss.enabled", havingValue = "true", matchIfMissing = true)
public class JefwOssAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "jefw.oss")
    public JefwOssProperties jefwOssProperties() {
        return new JefwOssProperties();
    }

    // ========================= 阿里云 ============================= //

    /**
     * 当系统依赖了aliyun-sdk-oss并且此bean不存在时，才会启用该配置
     * （这里用检测 {@link OSS} 是否存在来表示）
     */
    @Bean
    @ConditionalOnClass(OSS.class)
    @ConditionalOnMissingBean(OSS.class)
    public OSS aliyunOssClient(JefwOssProperties jefwOssProperties) {
        return new OSSClientBuilder().build(
                jefwOssProperties.getAliyun().getEndpoint(),
                jefwOssProperties.getAliyun().getAccessKeyId(),
                jefwOssProperties.getAliyun().getAccessKeySecret()
        );
    }

    /**
     * 当系统依赖了aliyun-sdk-oss时，才会启用该配置
     * （这里用检测 {@link OSS} 是否存在来表示）
     */
    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnClass(OSS.class)
    public OssService ossServiceAliyunImpl(OSS oss) {
        return new OssServiceAliyunImpl(oss);
    }

    // ========================= 腾讯云 ============================= //

    /**
     * 当系统依赖了tencent-sdk-cos并且此bean不存在时，才会启用该配置
     * （这里用检测 {@link COSClient} 是否存在来表示）
     */
    @Bean
    @ConditionalOnClass(COSClient.class)
    @ConditionalOnMissingBean(COSClient.class)
    public COSClient cosClient(JefwOssProperties jefwOssProperties) {
        COSCredentials cred = new BasicCOSCredentials(
                jefwOssProperties.getTencent().getAccessKeyId(),
                jefwOssProperties.getTencent().getAccessKeySecret()
        );
        Region region = new Region(jefwOssProperties.getTencent().getRegion());
        ClientConfig clientConfig = new ClientConfig(region);
        clientConfig.setHttpProtocol(jefwOssProperties.getTencent().getHttps() ? HttpProtocol.https : HttpProtocol.http);
        return new COSClient(cred, clientConfig);
    }

    /**
     * 当系统依赖了tencent-sdk-cos时，才会启用该配置
     * （这里用检测 {@link COSClient} 是否存在来表示）
     */
    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnClass(COSClient.class)
    public OssService ossServiceTencentImpl(COSClient cosClient) {
        return new OssServiceTencentImpl(cosClient);
    }

}
