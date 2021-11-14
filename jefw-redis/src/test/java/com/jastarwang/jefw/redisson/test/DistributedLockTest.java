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
package com.jastarwang.jefw.redisson.test;

import com.jastarwang.jefw.redisson.DistributedLock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

/**
 * Redisson分布式锁单元测试
 *
 * @author Jastar Wang
 * @date 2020/10/22
 * @since 1.0
 */
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@SpringBootTest
@SpringBootConfiguration
public class DistributedLockTest {

    @Resource
    private DistributedLock distributedLock;

    @Test
    public void contextLoad() {
        Assertions.assertNotNull(distributedLock);
    }

    // ====以下为高并发场景下测试分布式锁是否正常的代码，在使用本类库的客户端调用即可==== //
    // @RestController
    // @RequestMapping("/")
    public class DemoController {

        // @Resource
        private DistributedLock lock;

        private int stock = 10;

        private String productId = "666";

        // @GetMapping("seckill")
        public String seckill() {
            String result;
            if (stock > 0) {
                stock = stock - 1;
                result = Thread.currentThread().getId() + "抢购成功！剩余库存：" + stock;
            } else {
                result = Thread.currentThread().getId() + "抢购失败，库存不足：" + stock;
            }
            // 会出现超卖的问题
            System.out.println(result);
            return result;
        }

        // @GetMapping("seckillByLock")
        public String seckillByLock() {
            String result = lock.lock(productId, 1, () -> {
                if (stock > 0) {
                    stock = stock - 1;
                    return Thread.currentThread().getId() + "抢购成功！剩余库存：" + stock;
                } else {
                    return Thread.currentThread().getId() + "抢购失败，库存不足：" + stock;
                }
            }, e -> Thread.currentThread().getId() + "获取锁失败：" + e.getMessage());
            // 不会超卖
            System.out.println(result);
            return result;
        }

        // @GetMapping("resetStock")
        public String resetStock() {
            stock = 10;
            return "重置成功：" + stock + "！";
        }
    }

}
