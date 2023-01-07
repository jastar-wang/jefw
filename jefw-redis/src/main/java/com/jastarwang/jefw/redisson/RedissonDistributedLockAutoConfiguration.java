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
package com.jastarwang.jefw.redisson;

import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson分布式锁自动配置类
 * <p>
 * 当系统依赖了Redisson的相关类库时，才会启用该配置
 * （这里用检测 {@link RedissonClient} 是否存在来表示）
 * </p>
 *
 * @author Jastar Wang
 * @date 2020/10/21
 * @since 1.0
 */
@Configuration
@ConditionalOnClass(RedissonClient.class)
public class RedissonDistributedLockAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "distributedLock")
    public RedissonDistributedLock distributedLock(RedissonClient redissonClient) {
        return new RedissonDistributedLock(redissonClient);
    }

}
