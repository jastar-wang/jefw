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
package com.jastarwang.jefw.lock.test;

import com.jastarwang.jefw.lock.DistributedLock;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 在显式启用的 Redis 实例上验证分布式锁的互斥和释放。
 *
 * @author Jastar Wang
 * @date 2020/10/22
 * @since 1.0
 */
@EnabledIfEnvironmentVariable(named = "JEFW_REDIS_IT", matches = "true")
@SpringBootTest(classes = DistributedLockIT.class)
@SpringBootConfiguration
@EnableAutoConfiguration
@Import(DemoConfig.class)
class DistributedLockIT {

    @Resource
    private DistributedLock distributedLock;

    /**
     * 主线程持锁期间由另一线程竞争，释放后再由同一竞争线程获取。
     */
    @Test
    @Timeout(30)
    void contenderAcquiresLockOnlyAfterRelease() throws Exception {
        String key = "jefw:it:lock:" + UUID.randomUUID();
        ExecutorService contender = Executors.newSingleThreadExecutor();
        try {
            boolean acquiredWhileHeld = distributedLock.lock(key,
                    () -> contenderCanAcquire(contender, key),
                    () -> fail("主线程应能获取本次测试的锁"));
            assertFalse(acquiredWhileHeld, "主线程持锁时，竞争线程应获取失败");
            assertTrue(contenderCanAcquire(contender, key), "主线程释放后，竞争线程应能获取锁");
        } finally {
            contender.shutdownNow();
            assertTrue(contender.awaitTermination(5, TimeUnit.SECONDS));
        }
    }

    private boolean contenderCanAcquire(ExecutorService contender, String key) {
        return assertDoesNotThrow(() -> contender
                .submit(() -> distributedLock.lock(key, () -> true, () -> false))
                .get(5, TimeUnit.SECONDS));
    }
}
