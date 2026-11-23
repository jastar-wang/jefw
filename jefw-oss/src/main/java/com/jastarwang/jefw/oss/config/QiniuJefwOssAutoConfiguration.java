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
import com.jastarwang.jefw.oss.impl.QiniuJefwOssService;
import com.jastarwang.jefw.oss.model.QiniuOssClient;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Region;
import com.qiniu.util.Auth;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * OSS对象存储自动配置 - 七牛云
 *
 * @author Jastar Wang
 * @date 2025/4/21
 * @since 2.1.0
 */
@Configuration
@ConditionalOnOssChannel(OssChannel.QINIU)
@ConditionalOnClass(BucketManager.class)
@ConditionalOnProperty(value = "jefw.oss.enabled", havingValue = "true")
public class QiniuJefwOssAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(QiniuOssClient.class)
    public QiniuOssClient qiniuOssClient(JefwOssProperties jefwOssProperties) {
        JefwOssProperties.Qiniu config = jefwOssProperties.getQiniu();
        Auth auth = Auth.create(config.getAccessKeyId(), config.getAccessKeySecret());
        Region region = Region.createWithRegionId(config.getRegion());
        return new QiniuOssClient(auth, region);
    }

    @Order(240)
    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnMissingBean(name = "qiniuJefwOssService")
    public JefwOssService qiniuJefwOssService(QiniuOssClient qiniuOssClient, JefwOssProperties jefwOssProperties) {
        return new QiniuJefwOssService(qiniuOssClient, jefwOssProperties.getQiniu());
    }

}
