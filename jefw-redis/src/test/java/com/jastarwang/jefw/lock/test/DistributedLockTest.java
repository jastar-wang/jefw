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
 * 分布式锁单元测试
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
public class DistributedLockTest {

    @Resource
    private DistributedLock distributedLock;

    @Test
    public void contextLoad() {
        Assertions.assertNotNull(distributedLock);
    }

}
