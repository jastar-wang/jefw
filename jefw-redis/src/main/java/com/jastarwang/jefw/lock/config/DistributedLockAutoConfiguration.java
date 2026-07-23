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

import com.jastarwang.jefw.lock.impl.RedissonDistributedLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 分布式锁自动配置类
 * <p>
 * 默认使用 Redisson 实现，且当系统依赖了 Redisson 的相关类库时，才会启用该配置
 * （这里用检测 {@link RedissonClient} 类来判断）
 * </p>
 *
 * @author Jastar Wang
 * @date 2020/10/21
 * @since 1.0
 */
@Configuration
@ConditionalOnClass(RedissonClient.class)
public class DistributedLockAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "distributedLock")
    public RedissonDistributedLock distributedLock(RedissonClient redissonClient) {
        return new RedissonDistributedLock(redissonClient);
    }

}
