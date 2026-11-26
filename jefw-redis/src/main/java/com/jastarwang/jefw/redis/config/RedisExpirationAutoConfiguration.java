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
package com.jastarwang.jefw.redis.config;

import com.jastarwang.jefw.redis.event.RedisKeyExpiredEvent;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.nio.charset.StandardCharsets;

/**
 * 订阅键过期事件。通知参数由 Redis 运维方设置，订阅端无需 CONFIG 权限。
 *
 * @author Jastar Wang
 * @since 2.6.0
 */
@AutoConfiguration
@ConditionalOnClass(RedisMessageListenerContainer.class)
@ConditionalOnProperty(name = "jefw.redis.expiration.enabled", havingValue = "true")
public class RedisExpirationAutoConfiguration {

    @Bean
    public RedisMessageListenerContainer jefwRedisExpirationListenerContainer(
            RedisConnectionFactory connectionFactory, ApplicationEventPublisher publisher) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener((message, pattern) -> publisher.publishEvent(new RedisKeyExpiredEvent(
                        new String(message.getBody(), StandardCharsets.UTF_8),
                        new String(message.getChannel(), StandardCharsets.UTF_8))),
                new PatternTopic("__keyevent@*__:expired"));
        return container;
    }

}
