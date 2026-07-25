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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;


/**
 * 分布式锁集成测试
 *
 * @author Jastar Wang
 * @date 2020/10/22
 * @since 1.0
 */
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@SpringBootTest
@SpringBootConfiguration
@Import(DemoConfig.class)
public class DistributedLockIT {

    @Resource
    private DistributedLock distributedLock;

    @Test
    public void contextLoad() {
        Assertions.assertNotNull(distributedLock);
    }

}
