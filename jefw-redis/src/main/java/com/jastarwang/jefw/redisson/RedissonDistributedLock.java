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

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 分布式锁之Redisson锁
 *
 * @author Jastar Wang
 * @date 2020/10/21
 * @since 1.0
 */
public class RedissonDistributedLock implements DistributedLock {

    private final RedissonClient redissonClient;

    public RedissonDistributedLock(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public <T> T lock(String resource, long waitSeconds, Supplier<T> succeed, Function<Exception, T> failed) {
        return lock(resource, waitSeconds, -1, succeed, failed);
    }

    @Override
    public <T> T lock(String resource, long waitSeconds, long leaseSeconds, Supplier<T> succeed, Function<Exception, T> failed) {
        return lock(resource, waitSeconds, leaseSeconds, TimeUnit.SECONDS, succeed, failed);
    }

    @Override
    public <T> T lock(String resource, long waitTime, long leaseTime, TimeUnit unit, Supplier<T> succeed, Function<Exception, T> failed) {
        RLock lock = redissonClient.getLock(resource);
        try {
            boolean isLocked = lock.tryLock(waitTime, leaseTime, unit);
            if (isLocked) {
                return succeed.get();
            }
            return failed.apply(new CannotAcquireLockException("Cannot to acquire lock, because redisson returned false!"));
        } catch (InterruptedException e) {
            return failed.apply(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public <T> T lock(String resource, Supplier<T> succeed, Supplier<T> failed) {
        RLock lock = redissonClient.getLock(resource);
        try {
            boolean isLocked = lock.tryLock();
            if (isLocked) {
                return succeed.get();
            }
            return failed == null ? null : failed.get();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
