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
package com.jastarwang.jefw.weixin.config;

import com.jastarwang.jefw.weixin.JefwWeixinDevMpService;
import com.jastarwang.jefw.weixin.JefwWeixinDevOaService;
import com.jastarwang.jefw.weixin.impl.JefwWeixinDevMpServiceImpl;
import com.jastarwang.jefw.weixin.impl.JefwWeixinDevOaServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信开发自动配置-主入口
 *
 * @author Jastar Wang
 * @date 2025/4/9
 * @since 2.2.0
 */
@Configuration
@ConditionalOnProperty(value = "jefw.weixin.enabled", havingValue = "true")
public class JefwWeixinAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "jefw.weixin")
    public JefwWeixinProperties jefwWeixinProperties() {
        return new JefwWeixinProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public JefwWeixinDevOaService jefwWeixinDevOaService() {
        return new JefwWeixinDevOaServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public JefwWeixinDevMpService jefwWeixinDevMpService() {
        return new JefwWeixinDevMpServiceImpl();
    }

}
