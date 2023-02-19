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
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OSS对象存储自动配置 - 阿里云
 *
 * @author Jastar Wang
 * @date 2021/9/14
 * @since 1.2.0
 */
@Configuration
@ConditionalOnClass(OSS.class)
@ConditionalOnProperty(value = "jefw.oss.enabled", havingValue = "true", matchIfMissing = true)
public class OssAliyunAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(OSS.class)
    public OSS aliyunOssClient(JefwOssProperties jefwOssProperties) {
        return new OSSClientBuilder().build(
                jefwOssProperties.getAliyun().getEndpoint(),
                jefwOssProperties.getAliyun().getAccessKeyId(),
                jefwOssProperties.getAliyun().getAccessKeySecret()
        );
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnClass(OSS.class)
    public OssService ossServiceAliyunImpl(OSS oss) {
        return new OssServiceAliyunImpl(oss);
    }

}
