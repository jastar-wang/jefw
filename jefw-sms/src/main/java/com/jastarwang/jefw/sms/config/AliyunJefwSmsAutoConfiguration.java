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
package com.jastarwang.jefw.sms.config;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import com.jastarwang.jefw.sms.JefwSmsService;
import com.jastarwang.jefw.sms.SmsChannel;
import com.jastarwang.jefw.sms.impl.AliyunJefwSmsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;

/**
 * 短信服务自动配置 - 阿里云
 *
 * @author Jastar Wang
 * @date 2026/11/24
 * @since 2.5.1
 */
@Configuration
@ConditionalOnSmsChannel(value = SmsChannel.ALIYUN, requiredProperty = "jefw.sms.aliyun.access-key-id")
@ConditionalOnClass(Client.class)
@ConditionalOnProperty(value = "jefw.sms.enabled", havingValue = "true")
public class AliyunJefwSmsAutoConfiguration {

    @Order(310)
    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnMissingBean(name = "aliyunJefwSmsService")
    public JefwSmsService aliyunJefwSmsService(JefwSmsProperties properties) throws Exception {
        JefwSmsProperties.Aliyun aliyun = properties.getAliyun();
        Assert.hasText(aliyun.getAccessKeyId(), "jefw.sms.aliyun.access-key-id is required");
        Assert.hasText(aliyun.getAccessKeySecret(), "jefw.sms.aliyun.access-key-secret is required");
        Assert.hasText(aliyun.getSignName(), "jefw.sms.aliyun.sign-name is required");
        Config config = new Config().setAccessKeyId(aliyun.getAccessKeyId())
                .setAccessKeySecret(aliyun.getAccessKeySecret()).setEndpoint(aliyun.getEndpoint());
        return new AliyunJefwSmsService(aliyun, new Client(config));
    }

}
