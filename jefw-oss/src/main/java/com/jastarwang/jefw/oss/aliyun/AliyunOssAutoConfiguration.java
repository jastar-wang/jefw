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
package com.jastarwang.jefw.oss.aliyun;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云OSS自动配置类
 * <p>
 * 当系统依赖了aliyun-sdk-oss时，才会启用该配置
 * （这里用检测 {@link OSS} 是否存在来表示）
 * </p>
 *
 * @author Jastar Wang
 * @date 2021/9/14
 * @since 1.2.0
 */
@Configuration
@ConditionalOnClass(OSS.class)
public class AliyunOssAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "oss.aliyun")
    public AliyunOssProperties aliyunOssProperties() {
        return new AliyunOssProperties();
    }

    @Bean
    @ConditionalOnMissingBean(OSS.class)
    public OSS aliyunOssClient(AliyunOssProperties properties) {
        return new OSSClientBuilder().build(
                properties.getEndpoint(),
                properties.getAccessKeyId(),
                properties.getAccessKeySecret()
        );
    }

    /**
     * 本方法是为了注入 AliyunOssUtil 里的成员变量
     *
     * @return AliyunOssUtil 对象，请使用类的静态调用而不是本对象
     */
    @Bean
    public AliyunOssUtil aliyunOssUtil() {
        return new AliyunOssUtil();
    }

}
