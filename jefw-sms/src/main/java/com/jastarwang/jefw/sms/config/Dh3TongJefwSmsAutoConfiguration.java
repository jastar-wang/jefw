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
import com.jastarwang.jefw.sms.impl.Dh3TongJefwSmsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * 短信服务自动配置 - 大汉三通
 *
 * @author Jastar Wang
 * @date 2023/4/5
 * @since 1.3.0
 */
@Configuration
@ConditionalOnProperty(value = "jefw.sms.enabled", havingValue = "true")
@ConditionalOnProperty(prefix = "jefw.sms.dh3tong", name = "api-url")
public class Dh3TongJefwSmsAutoConfiguration {

    @Order(330)
    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnMissingBean(name = "dh3tongJefwSmsService")
    public JefwSmsService dh3tongJefwSmsService(JefwSmsProperties jefwSmsProperties) {
        return new Dh3TongJefwSmsService(jefwSmsProperties);
    }

}
