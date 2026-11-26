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
import com.jastarwang.jefw.oss.JefwOssTemplate;
import com.jastarwang.jefw.oss.OssChannel;
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
 * OSS对象存储自动配置-主入口
 *
 * @author Jastar Wang
 * @since 1.3.0
 */
@Configuration
@ConditionalOnProperty(value = "jefw.oss.enabled", havingValue = "true")
public class JefwOssAutoConfiguration {

    @Bean(initMethod = "validate")
    @ConfigurationProperties(prefix = "jefw.oss")
    public JefwOssProperties jefwOssProperties() {
        return new JefwOssProperties();
    }

    @Bean
    @Lazy(false)
    InitializingBean jefwOssChannelValidator(JefwOssProperties properties,
                                             ObjectProvider<JefwOssTemplate> templates,
                                             ObjectProvider<JefwOssService> services) {
        return () -> {
            Set<OssChannel> selected = properties.getEnabledChannels();
            if (selected == null) {
                return;
            }
            Set<OssChannel> missing = EnumSet.copyOf(selected);
            // 模板已完成初始化，直接检查其路由可复用已注入的服务实例。
            for (JefwOssTemplate template : templates) {
                missing.removeIf(channel -> {
                    JefwOssService service = template.getService(channel);
                    return service != null && service.getChannel() == channel;
                });
                if (missing.isEmpty()) {
                    return;
                }
            }
            // 自定义模板可以自行管理路由；它未公开的渠道仍可由应用注册的服务提供。
            for (JefwOssService service : services) {
                missing.remove(service.getChannel());
                if (missing.isEmpty()) {
                    return;
                }
            }
            throw new IllegalStateException("jefw.oss.enabled-channels includes " + missing.iterator().next() +
                    " but no service is available. Add the provider SDK or supply a JefwOssService for this channel.");
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public JefwOssTemplate jefwOssTemplate(JefwOssProperties jefwOssProperties, List<JefwOssService> jefwOssServices) {
        return new JefwOssTemplate(jefwOssProperties, jefwOssServices);
    }

}
