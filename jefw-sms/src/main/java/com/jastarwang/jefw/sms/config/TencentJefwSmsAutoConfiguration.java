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

import com.jastarwang.jefw.sms.JefwSmsService;
import com.jastarwang.jefw.sms.impl.TencentJefwSmsService;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * 短信服务自动配置 - 腾讯云
 *
 * @author Jastar Wang
 * @date 2023/4/5
 * @since 1.3.0
 */
@Configuration
@ConditionalOnClass(SmsClient.class)
@ConditionalOnProperty(value = "jefw.sms.enabled", havingValue = "true")
public class TencentJefwSmsAutoConfiguration {

    @Order(320)
    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnMissingBean(name = "tencentJefwSmsService")
    public JefwSmsService tencentJefwSmsService(JefwSmsProperties jefwSmsProperties) {
        Credential cred = new Credential(jefwSmsProperties.getTencent().getSecretId(), jefwSmsProperties.getTencent().getSecretKey());
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(jefwSmsProperties.getTencent().getEndpoint());
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        SmsClient client = new SmsClient(cred, jefwSmsProperties.getTencent().getRegion(), clientProfile);
        return new TencentJefwSmsService(jefwSmsProperties, client);
    }

}
