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

/**
 * 演示并发扣减库存时分布式锁的使用方式。运行示例时由调用方配置控制器及锁实例。
 *
 * @author Jastar Wang
 * @since 1.2.3
 */
public class DemoController {

    // @Resource
    private DistributedLock lock;

    private int stock = 10;

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
        String productId = "666";
        String result = lock.lock(productId, 1, () -> {
            if (stock > 0) {
                stock = stock - 1;
                return Thread.currentThread().getId() + "抢购成功！剩余库存：" + stock;
            } else {
                return Thread.currentThread().getId() + "抢购失败，库存不足：" + stock;
            }
        }, () -> Thread.currentThread().getId() + "获取锁失败");
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
