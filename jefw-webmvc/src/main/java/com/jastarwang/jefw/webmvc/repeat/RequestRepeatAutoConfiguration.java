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
package com.jastarwang.jefw.webmvc.repeat;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 在 Servlet MVC 应用中为带注解的方法启用窗口防重。
 *
 * @author Jastar Wang
 * @date 2026/11/25
 * @since 2.6.0
 */
@AutoConfiguration(afterName = "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class RequestRepeatAutoConfiguration {

    @Bean
    Advisor requestRepeatLimitAdvisor(ObjectProvider<RepeatWindow> windows, ObjectProvider<SessionUserProvider> users) {
        MethodInterceptor interceptor = new RequestRepeatInterceptor(windows, users);
        return new DefaultPointcutAdvisor(new AnnotationMatchingPointcut(null, RequestRepeatLimit.class, true), interceptor);
    }

    // 隔离可选依赖，缺少 Spring Data Redis 时仍能安装拦截器并报告缺失条件。
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(StringRedisTemplate.class)
    static class RedisWindowConfiguration {
        @Bean
        @ConditionalOnBean(StringRedisTemplate.class)
        RepeatWindow requestRepeatWindow(StringRedisTemplate template) {
            return (key, seconds) -> template.opsForValue().setIfAbsent(key, "true", seconds, TimeUnit.SECONDS);
        }
    }

    @FunctionalInterface
    interface RepeatWindow {
        Boolean reserve(String key, int seconds);
    }

}
