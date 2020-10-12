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
package com.jastarwang.jefw.redis;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类，提供对Redis访问的便捷封装
 * <br>
 * 为了能对应 redis 的数据结构并且调用简洁易懂，对于本工具类的方法前缀使用字母来表示，我们约定：
 * <ul>
 * <li>ValueOperations: 较常用的结构，方法无前缀，用首字母 <b>v</b>表示</li>
 * <li>ListOperations: 用首字母 <b>l</b> 表示</li>
 * <li>SetOperations: 用首字母 <b>s</b> 表示</li>
 * <li>ZSetOperations: 用首字母 <b>z</b> 表示</li>
 * </ul>
 * 比如：“RedisUtil.set()...”
 *
 * @author Jastar Wang
 * @date 2020/10/12
 * @since 1.0
 */
public class RedisUtil {

    private static RedisTemplate<String, Object> template;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    private synchronized void init() {
        RedisUtil.template = redisTemplate;
    }

    public static ValueOperations<String, Object> v() {
        return template.opsForValue();
    }

    public static ListOperations<String, Object> l() {
        return template.opsForList();
    }

    public static SetOperations<String, Object> s() {
        return template.opsForSet();
    }

    public static ZSetOperations<String, Object> z() {
        return template.opsForZSet();
    }


    /*====================================value======================================*/

    public static void set(String key, Object value) {
        v().set(key, value);
    }

    public static void set(String key, Object value, long seconds) {
        set(key, value, seconds, TimeUnit.SECONDS);
    }

    public static void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        v().set(key, value, timeout, timeUnit);
    }

    public static Object get(String key) {
        return v().get(key);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String key, Class<T> clazz) {
        Object result = get(key);
        if (result == null) {
            return null;
        }
        if (clazz.equals(Long.class)) {
            return (T) Long.valueOf(result.toString());
        }
        return (T) result;
    }

    public static Object getAndSet(String key, Object value) {
        return v().getAndSet(key, value);
    }

    public static List<Object> multiGet(Collection<String> keys) {
        return v().multiGet(keys);
    }

    public static void multiSet(Map<? extends String, ?> map) {
        v().multiSet(map);
    }

    /**
     * 自增或自减
     *
     * @param key key，非空
     * @param delta 步长，可空；若为空，则自增1；若为正，则自增delta；若为负，则自减delta
     */
    public static void increment(String key, Long delta) {
        if (delta == null) {
            v().increment(key);
        } else if (delta < 0) {
            v().decrement(key, delta);
        } else if (delta > 0) {
            v().increment(key, delta);
        }
    }

    /**
     * 获取key对应的value的长度
     *
     * @param key key，非空
     * @return value的长度
     */
    public static Long size(String key) {
        return v().size(key);
    }

}