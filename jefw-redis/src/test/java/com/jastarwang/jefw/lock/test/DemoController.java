package com.jastarwang.jefw.lock.test;

import com.jastarwang.jefw.lock.DistributedLock;

// ====以下为高并发场景下测试分布式锁是否正常的代码，在使用本类库的客户端调用即可==== //

// @RestController
// @RequestMapping("/")
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