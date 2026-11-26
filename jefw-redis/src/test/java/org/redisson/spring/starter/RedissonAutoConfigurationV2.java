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
package org.redisson.spring.starter;

import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

/**
 * 使用 Redisson starter 的自动配置类名验证生产代码声明的排序关系。
 *
 * @author Jastar Wang
 * @since 2.5.1
 */
@AutoConfiguration
public class RedissonAutoConfigurationV2 {
    @Bean
    RedissonClient redissonClient() {
        return mock(RedissonClient.class);
    }
}
