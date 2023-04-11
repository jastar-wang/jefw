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

    @Order(200)
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
