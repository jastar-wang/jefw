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

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 分布式锁接口
 *
 * @author Jastar Wang
 * @date 2020/10/20
 * @since 1.0
 */
public interface DistributedLock {

    /**
     * 普通分布式锁
     * <br><b>Redisson的实现方式下，使用“看门狗”机制</b>
     *
     * @param resource 资源名称
     * @param waitTime 尝试加锁的最大等待时间（单位：秒）
     * @param succeed 加锁成功要执行的业务逻辑
     * @param failed 加锁失败要执行的业务逻辑
     * @param <T> 结果类型
     * @return 业务结果
     */
    <T> T lock(String resource, long waitTime, Supplier<T> succeed, Function<Exception, T> failed);

    /**
     * 普通分布式锁
     *
     * @param resource 资源名称
     * @param waitTime 尝试加锁的最大等待时间（单位：秒）
     * @param leaseTime 加锁后锁的失效时间（单位：秒）
     * @param succeed 加锁成功要执行的业务逻辑
     * @param failed 加锁失败要执行的业务逻辑
     * @param <T> 结果类型
     * @return 业务结果
     */
    <T> T lock(String resource, long waitTime, long leaseTime, Supplier<T> succeed, Function<Exception, T> failed);

    /**
     * 普通分布式锁
     *
     * @param resource 资源名称
     * @param waitTime 尝试加锁的最大等待时间
     * @param leaseTime 加锁后锁的失效时间
     * @param unit 时间单位
     * @param succeed 加锁成功要执行的业务逻辑
     * @param failed 加锁失败要执行的业务逻辑
     * @param <T> 结果类型
     * @return 业务结果
     */
    <T> T lock(String resource, long waitTime, long leaseTime, TimeUnit unit, Supplier<T> succeed, Function<Exception, T> failed);

}
