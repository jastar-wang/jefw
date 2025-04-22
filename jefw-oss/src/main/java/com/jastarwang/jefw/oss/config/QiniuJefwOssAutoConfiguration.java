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

    @Order(400)
    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnMissingBean(name = "qiniuJefwOssService")
    public JefwOssService qiniuJefwOssService(QiniuOssClient qiniuOssClient, JefwOssProperties jefwOssProperties) {
        return new QiniuJefwOssService(qiniuOssClient, jefwOssProperties.getQiniu());
    }

}
