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
import com.jastarwang.jefw.sms.SmsChannel;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * 短信服务自动配置 - 主入口
 *
 * @author Jastar Wang
 * @since 1.3.0
 */
@Configuration
@ConditionalOnProperty(value = "jefw.sms.enabled", havingValue = "true")
public class JefwSmsAutoConfiguration {

    @Bean(initMethod = "validate")
    @ConfigurationProperties(prefix = "jefw.sms")
    public JefwSmsProperties jefwSmsProperties() {
        return new JefwSmsProperties();
    }

    @Bean
    @Lazy(false)
    InitializingBean jefwSmsChannelValidator(JefwSmsProperties properties,
                                             ObjectProvider<JefwSmsTemplate> templates,
                                             ObjectProvider<JefwSmsService> services) {
        return () -> {
            Set<SmsChannel> selected = properties.getEnabledChannels();
            if (selected == null) {
                return;
            }
            Set<SmsChannel> missing = EnumSet.copyOf(selected);
            // 模板已完成初始化，直接检查其路由可复用已注入的服务实例。
            for (JefwSmsTemplate template : templates) {
                missing.removeIf(channel -> {
                    JefwSmsService service = template.getService(channel);
                    return service != null && service.getChannel() == channel;
                });
                if (missing.isEmpty()) {
                    return;
                }
            }
            // 自定义模板可以自行管理路由；它未公开的渠道仍可由应用注册的服务提供。
            for (JefwSmsService service : services) {
                missing.remove(service.getChannel());
                if (missing.isEmpty()) {
                    return;
                }
            }
            throw new IllegalStateException("jefw.sms.enabled-channels includes " + missing.iterator().next() +
                    " but no service is available. Check the provider SDK and configuration " +
                    "(jefw.sms.aliyun.access-key-id or jefw.sms.dh3tong.api-url), or supply a JefwSmsService for this channel.");
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public JefwSmsTemplate jefwSmsTemplate(JefwSmsProperties jefwSmsProperties, List<JefwSmsService> jefwSmsServices) {
        return new JefwSmsTemplate(jefwSmsProperties, jefwSmsServices);
    }

}
