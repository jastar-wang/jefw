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
package com.jastarwang.jefw.lock.impl;

import com.jastarwang.jefw.core.function.Execute;
import com.jastarwang.jefw.lock.DistributedLock;
import com.jastarwang.jefw.lock.LockInterruptedException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁之Redisson实现
 *
 * @author Jastar Wang
 * @since 1.0
 */
public class RedissonDistributedLock implements DistributedLock {

    private final RedissonClient redissonClient;

    public RedissonDistributedLock(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public <T> T lock(String resource, Supplier<T> succeed, Supplier<T> failed) {
        RLock lock = redissonClient.getLock(resource);
        try {
            boolean isLocked = lock.tryLock();
            if (isLocked) {
                return succeed.get();
            }
            return failed.get();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public <T> T lock(String resource, long waitSeconds, Supplier<T> succeed, Supplier<T> failed) {
        return lock(resource, waitSeconds, -1, succeed, failed);
    }

    @Override
    public <T> T lock(String resource, long waitSeconds, long leaseSeconds, Supplier<T> succeed, Supplier<T> failed) {
        return lock(resource, waitSeconds, leaseSeconds, TimeUnit.SECONDS, succeed, failed);
    }

    @Override
    public <T> T lock(String resource, long waitTime, long leaseTime, TimeUnit unit, Supplier<T> succeed, Supplier<T> failed) {
        RLock lock = redissonClient.getLock(resource);
        try {
            boolean isLocked = lock.tryLock(waitTime, leaseTime, unit);
            if (isLocked) {
                return succeed.get();
            }
            return failed.get();
        } catch (InterruptedException e) {
            throw new LockInterruptedException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public void lock(String resource, Execute succeed, Execute failed) {
        RLock lock = redissonClient.getLock(resource);
        try {
            boolean isLocked = lock.tryLock();
            if (isLocked) {
                succeed.exec();
            } else if (failed != null) {
                failed.exec();
            }
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public void lock(String resource, long waitSeconds, Execute succeed, Execute failed) {
        lock(resource, waitSeconds, -1, succeed, failed);
    }

    @Override
    public void lock(String resource, long waitSeconds, long leaseSeconds, Execute succeed, Execute failed) {
        lock(resource, waitSeconds, leaseSeconds, TimeUnit.SECONDS, succeed, failed);
    }

    @Override
    public void lock(String resource, long waitTime, long leaseTime, TimeUnit unit, Execute succeed, Execute failed) {
        RLock lock = redissonClient.getLock(resource);
        try {
            boolean isLocked = lock.tryLock(waitTime, leaseTime, unit);
            if (isLocked) {
                succeed.exec();
            } else if (failed != null) {
                failed.exec();
            }
        } catch (InterruptedException e) {
            throw new LockInterruptedException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
