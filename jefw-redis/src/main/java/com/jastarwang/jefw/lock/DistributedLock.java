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
package com.jastarwang.jefw.lock;

import com.jastarwang.jefw.core.function.Execute;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁接口
 *
 * @author Jastar Wang
 * @since 1.0
 */
public interface DistributedLock {

    /**
     * 普通分布式锁（不等待）
     *
     * @param resource 非空，要加锁的资源
     * @param succeed  非空，加锁成功要执行的业务逻辑
     * @param failed   非空，加锁失败要执行的业务逻辑（不捕获异常）
     * @param <T>      返回的结果类型
     * @return 返回的结果
     * @since 1.2.3
     */
    <T> T lock(String resource, Supplier<T> succeed, Supplier<T> failed);

    /**
     * 普通分布式锁（阻塞等待）
     * <p>Redisson的实现方式下，使用“看门狗”机制，即业务未执行完时，锁的有效期会自动延长</p>
     *
     * @param resource    非空，要加锁的资源
     * @param waitSeconds 非空，尝试加锁的最大阻塞等待时间（单位：秒）
     * @param succeed     非空，加锁成功要执行的业务逻辑（有返回值）
     * @param failed      非空，加锁失败要执行的业务逻辑（不捕获异常）
     * @param <T>         返回结果的类型
     * @return 返回的结果
     * @since 1.2.4
     */
    <T> T lock(String resource, long waitSeconds, Supplier<T> succeed, Supplier<T> failed);

    /**
     * 普通分布式锁（阻塞等待）
     *
     * @param resource     非空，要加锁的资源
     * @param waitSeconds  非空，尝试加锁的最大阻塞等待时间（单位：秒）
     * @param leaseSeconds 非空，加锁成功后自动释放的时间（单位：秒）
     * @param succeed      非空，加锁成功要执行的业务逻辑（有返回值）
     * @param failed       非空，加锁失败要执行的业务逻辑（不捕获异常）
     * @param <T>          返回结果的类型
     * @return 返回的结果
     * @since 1.2.4
     */
    <T> T lock(String resource, long waitSeconds, long leaseSeconds, Supplier<T> succeed, Supplier<T> failed);

    /**
     * 普通分布式锁（阻塞等待）
     *
     * @param resource  非空，要加锁的资源
     * @param waitTime  非空，尝试加锁的最大阻塞等待时间
     * @param leaseTime 非空，加锁成功后自动释放的时间
     * @param unit      非空，时间单位
     * @param succeed   非空，加锁成功要执行的业务逻辑（有返回值）
     * @param failed    非空，加锁失败要执行的业务逻辑（不捕获异常）
     * @param <T>       返回结果的类型
     * @return 返回的结果
     * @since 1.2.4
     */
    <T> T lock(String resource, long waitTime, long leaseTime, TimeUnit unit, Supplier<T> succeed, Supplier<T> failed);

    // ================================================== 无返回值 ================================================== //

    /**
     * 普通分布式锁（不等待）
     *
     * @param resource 非空，要加锁的资源
     * @param succeed  非空，加锁成功要执行的业务逻辑
     * @param failed   可空，加锁失败要执行的业务逻辑（不捕获异常）
     * @since 1.2.4
     */
    void lock(String resource, Execute succeed, Execute failed);

    /**
     * 普通分布式锁（阻塞等待）
     * <p>Redisson的实现方式下，使用“看门狗”机制，即业务未执行完时，锁的有效期会自动延长</p>
     *
     * @param resource    非空，要加锁的资源
     * @param waitSeconds 非空，尝试加锁的最大阻塞等待时间（单位：秒）
     * @param succeed     非空，加锁成功要执行的业务逻辑（有返回值）
     * @param failed      可空，加锁失败要执行的业务逻辑（不捕获异常）
     * @since 1.2.4
     */
    void lock(String resource, long waitSeconds, Execute succeed, Execute failed);

    /**
     * 普通分布式锁（阻塞等待）
     *
     * @param resource     非空，要加锁的资源
     * @param waitSeconds  非空，尝试加锁的最大阻塞等待时间（单位：秒）
     * @param leaseSeconds 非空，加锁成功后自动释放的时间（单位：秒）
     * @param succeed      非空，加锁成功要执行的业务逻辑（有返回值）
     * @param failed       可空，加锁失败要执行的业务逻辑（不捕获异常）
     * @since 1.2.4
     */
    void lock(String resource, long waitSeconds, long leaseSeconds, Execute succeed, Execute failed);

    /**
     * 普通分布式锁（阻塞等待）
     *
     * @param resource  非空，要加锁的资源
     * @param waitTime  非空，尝试加锁的最大阻塞等待时间
     * @param leaseTime 非空，加锁成功后自动释放的时间
     * @param unit      非空，时间单位
     * @param succeed   非空，加锁成功要执行的业务逻辑（有返回值）
     * @param failed    可空，加锁失败要执行的业务逻辑（不捕获异常）
     * @since 1.2.4
     */
    void lock(String resource, long waitTime, long leaseTime, TimeUnit unit, Execute succeed, Execute failed);
}
