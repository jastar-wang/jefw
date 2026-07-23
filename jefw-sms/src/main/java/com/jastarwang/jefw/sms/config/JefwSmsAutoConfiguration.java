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
import com.jastarwang.jefw.sms.JefwSmsTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 短信服务自动配置 - 主入口
 *
 * @author Jastar Wang
 * @date 2023/4/5
 * @since 1.3.0
 */
@Configuration
@ConditionalOnProperty(value = "jefw.sms.enabled", havingValue = "true")
public class JefwSmsAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "jefw.sms")
    public JefwSmsProperties jefwSmsProperties() {
        return new JefwSmsProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public JefwSmsTemplate jefwSmsTemplate(JefwSmsProperties jefwSmsProperties, List<JefwSmsService> jefwSmsServices) {
        return new JefwSmsTemplate(jefwSmsProperties, jefwSmsServices);
    }

}
