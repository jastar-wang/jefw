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
package com.jastarwang.jefw.redis.test;

import com.jastarwang.jefw.lock.DistributedLock;
import com.jastarwang.jefw.lock.config.DistributedLockAutoConfiguration;
import com.jastarwang.jefw.lock.impl.RedissonDistributedLock;
import com.jastarwang.jefw.redis.RedisUtil;
import com.jastarwang.jefw.redis.config.RedisAutoConfiguration;
import com.jastarwang.jefw.redis.event.RedisKeyExpiredEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.DefaultMessage;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 离线验证缓存与分布式锁的自动配置条件。
 *
 * @author Jastar Wang
 * @date 2026/11/23
 * @since 2.5.1
 */
class RedisAutoConfigurationTest {
    /**
     * 通过应用的自动配置入口加载 Redis 模块。
     *
     * @author Jastar Wang
     * @date 2026/11/23
     * @since 2.5.1
     */
    @EnableAutoConfiguration
    static class Application {
    }

    /**
     * 模拟未指定与 jefw 的先后关系、按自身顺序注册客户端的第三方配置。
     *
     * @author Jastar Wang
     * @date 2026/11/23
     * @since 2.5.1
     */
    @AutoConfiguration
    @AutoConfigureOrder(50)
    @ConditionalOnProperty("test.redisson.late-client")
    static class LateClientAutoConfiguration {
        @Bean
        RedissonClient redissonClient() {
            return mock(RedissonClient.class);
        }
    }

    /**
     * 模拟在默认顺序之后按锁 Bean 启用的业务配置。
     *
     * @author Jastar Wang
     * @date 2026/11/23
     * @since 2.5.1
     */
    @AutoConfiguration
    @AutoConfigureOrder(100)
    @ConditionalOnBean(DistributedLock.class)
    static class LockConsumerAutoConfiguration {
    }

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(Application.class)
            .withBean(RedisConnectionFactory.class, () -> mock(RedisConnectionFactory.class));

    @Test
    void sdkAloneKeepsCacheAndSkipsLock() {
        runner.run(context -> {
            assertThat(context).hasSingleBean(RedisUtil.class).doesNotHaveBean(DistributedLock.class);
            assertThat(context).doesNotHaveBean("jefwRedisExpirationListenerContainer");
            RedisTemplate<?, ?> template = context.getBean("redisTemplate", RedisTemplate.class);
            assertThat(RedisUtil.template()).isSameAs(template);
            assertThat(template.getKeySerializer()).isInstanceOf(StringRedisSerializer.class);
            assertThat(template.getValueSerializer()).isInstanceOf(GenericJackson2JsonRedisSerializer.class);
        });
    }

    @Test
    void expirationSubscriptionPublishesTheActualChannelAndUtf8KeyWithoutCache() {
        try (var containers = mockConstruction(RedisMessageListenerContainer.class)) {
            runner.withPropertyValues("jefw.redis.expiration.enabled=true", "jefw.redis.cache.enabled=false")
                    .run(context -> {
                        var container = context.getBean("jefwRedisExpirationListenerContainer",
                                RedisMessageListenerContainer.class);
                        var listener = ArgumentCaptor.forClass(MessageListener.class);
                        verify(container).addMessageListener(listener.capture(), eq(new PatternTopic("__keyevent@*__:expired")));

                        List<Object> events = new ArrayList<>();
                        context.getSourceApplicationContext().addApplicationListener(
                                (ApplicationListener<PayloadApplicationEvent<?>>) event -> events.add(event.getPayload()));
                        byte[] channel = "__keyevent@3__:expired".getBytes(StandardCharsets.UTF_8);
                        byte[] key = "订单:42".getBytes(StandardCharsets.UTF_8);
                        listener.getValue().onMessage(new DefaultMessage(channel, key), null);
                        assertThat(events).containsExactly(new RedisKeyExpiredEvent("订单:42", "__keyevent@3__:expired"));
                    });
        }
    }

    @Test
    void closingOldContextPreservesNewTemplateUntilItsOwnerCloses() {
        runner.run(first -> runner.run(second -> {
            first.close();
            assertThat(RedisUtil.template()).isSameAs(second.getBean("redisTemplate"));
        }));
        runner.withPropertyValues("jefw.redis.cache.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(RedisUtil.class);
                    assertThat(RedisUtil.template()).isNull();
                });
    }

    @Test
    void closingChildRestoresParentTemplate() {
        runner.run(parent -> {
            RedisTemplate<?, ?> parentTemplate = parent.getBean("redisTemplate", RedisTemplate.class);
            RedisTemplate<String, Object> childTemplate = new RedisTemplate<>();
            childTemplate.setConnectionFactory(parent.getBean(RedisConnectionFactory.class));
            new ApplicationContextRunner().withParent(parent).withUserConfiguration(Application.class)
                    .withBean("redisTemplate", RedisTemplate.class, () -> childTemplate)
                    .run(child -> {
                        assertThat(child.containsLocalBean("redisUtil")).isTrue();
                        assertThat(RedisUtil.template()).isSameAs(childTemplate);
                    });
            assertThat(RedisUtil.template()).isSameAs(parentTemplate);
        });
        assertThat(RedisUtil.template()).isNull();
    }

    @Test
    void createsLockForLateThirdPartyClient() {
        runner.withPropertyValues("test.redisson.late-client=true")
                .run(context -> assertThat(context).hasSingleBean(RedissonClient.class)
                        .hasSingleBean(RedissonDistributedLock.class));
    }

    @Test
    void disablingLockAlsoSkipsLateClient() {
        runner.withPropertyValues("test.redisson.late-client=true", "jefw.redis.lock.enabled=false")
                .run(context -> assertThat(context).hasSingleBean(RedissonClient.class)
                        .doesNotHaveBean(DistributedLock.class));
    }

    @Test
    void disablingCacheKeepsDefaultLockWithUserClient() {
        runner.withPropertyValues("jefw.redis.cache.enabled=false")
                .withBean(RedissonClient.class, () -> mock(RedissonClient.class))
                .run(context -> assertThat(context).doesNotHaveBean(RedisAutoConfiguration.class)
                        .doesNotHaveBean(RedisUtil.class).hasSingleBean(RedissonDistributedLock.class));
    }

    @Test
    void disablingLockKeepsCacheEvenWithClient() {
        runner.withPropertyValues("jefw.redis.lock.enabled=false")
                .withBean(RedissonClient.class, () -> mock(RedissonClient.class))
                .run(context -> assertThat(context).hasSingleBean(RedisUtil.class).doesNotHaveBean(DistributedLock.class));
    }

    @Test
    void createsDefaultLockBetweenClientAndConsumerAutoConfigurations() {
        new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(
                        DistributedLockAutoConfiguration.class, RedissonAutoConfigurationV2.class, LockConsumerAutoConfiguration.class))
                .run(context -> assertThat(context).hasSingleBean(RedissonDistributedLock.class)
                        .hasSingleBean(LockConsumerAutoConfiguration.class));
    }

    @Test
    void backsOffForUserLockWithDifferentName() {
        DistributedLock custom = mock(DistributedLock.class);
        runner.withBean(RedissonClient.class, () -> mock(RedissonClient.class))
                .withBean("customLock", DistributedLock.class, () -> custom)
                .run(context -> assertThat(context.getBean(DistributedLock.class)).isSameAs(custom));
    }

    @Test
    void enablingLockStillRequiresSdk() {
        runner.withClassLoader(new FilteredClassLoader("org.redisson"))
                .withPropertyValues("jefw.redis.lock.enabled=true")
                .run(context -> assertThat(context).hasSingleBean(RedisUtil.class).doesNotHaveBean(DistributedLock.class));
    }
}
