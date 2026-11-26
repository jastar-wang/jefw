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
package com.jastarwang.jefw.lock.config;

import com.jastarwang.jefw.lock.DistributedLock;
import com.jastarwang.jefw.lock.impl.RedissonDistributedLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

/**
 * 为晚于常规锁配置注册的第三方 Redisson 客户端补充默认锁。
 *
 * @author Jastar Wang
 * @since 2.5.1
 */
@AutoConfiguration(after = DistributedLockAutoConfiguration.class)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
@ConditionalOnClass(RedissonClient.class)
@ConditionalOnBean(value = {DistributedLockAutoConfiguration.class, RedissonClient.class}, search = SearchStrategy.CURRENT)
@ConditionalOnMissingBean(value = DistributedLock.class, name = "distributedLock")
public class LateDistributedLockAutoConfiguration {

    @Bean
    RedissonDistributedLock distributedLock(RedissonClient client) {
        return new RedissonDistributedLock(client);
    }

}
