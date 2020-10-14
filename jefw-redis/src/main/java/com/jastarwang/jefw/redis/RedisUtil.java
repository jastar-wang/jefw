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

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
 * <li>HashOperations: 用首字母 <b>h</b> 表示</li>
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

    public static RedisTemplate<String, Object> original() {
        return RedisUtil.template;
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

    public static HashOperations<String, Object, Object> h() {
        return template.opsForHash();
    }


    /*====================================value======================================*/

    /**
     * 存入值
     *
     * @param key key，非空
     * @param value value，非空
     */
    public static void set(String key, Object value) {
        v().set(key, value);
    }

    /**
     * 存入值
     *
     * @param key key，非空
     * @param value value，非空
     * @param seconds 过期时间（单位：秒）
     */
    public static void set(String key, Object value, long seconds) {
        set(key, value, seconds, TimeUnit.SECONDS);
    }

    /**
     * 存入值
     *
     * @param key key，非空
     * @param value value，非空
     * @param timeout 过期时间，非空
     * @param timeUnit 过期时间单位，非空
     */
    public static void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        v().set(key, value, timeout, timeUnit);
    }

    /**
     * 获取值
     * <p>注意：在某些序列化方式下，对于结果进行Long类型的强转，会出现异常，编码前请务必了解相关的坑</p>
     *
     * @param key key，非空
     * @return 值
     */
    public static Object get(String key) {
        return v().get(key);
    }

    /**
     * 获取值并根据指定类型转换
     *
     * @param key key，非空
     * @param clazz 模板类型，非空
     * @param <T> 类型
     * @return 值
     */
    public static <T> T get(String key, Class<T> clazz) {
        Object result = get(key);
        return convertToClazz(result, clazz);
    }

    /**
     * 设置新的值并返回旧的值
     *
     * @param key key，非空
     * @param value value
     * @return 旧值
     */
    public static Object getAndSet(String key, Object value) {
        return v().getAndSet(key, value);
    }

    /**
     * 批量获取值
     *
     * @param keys key集合，非空
     * @return 值集合
     */
    public static List<Object> multiGet(Collection<String> keys) {
        return v().multiGet(keys);
    }

    /**
     * 批量存入值
     *
     * @param map 键值map
     */
    public static void multiSet(Map<? extends String, ?> map) {
        v().multiSet(map);
    }

    /**
     * 自增或自减（值为空时默认从0开始）
     *
     * @param key key，非空
     * @param delta 步长，可空；若为空，则自增1；若为正，则自增delta；若为负，则自减delta
     */
    public static void increment(String key, Long delta) {
        if (delta == null) {
            v().increment(key);
        } else if (delta < 0) {
            v().decrement(key, -delta);
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

    /*====================================list======================================*/

    /**
     * 根据下标获取指定元素
     *
     * @param key key，非空（可不存在）
     * @param index 下标（可超过实际长度，不会发生越界问题）
     * @return 元素或null
     */
    public static Object lGet(String key, long index) {
        return l().index(key, index);
    }

    /**
     * 根据下标获取指定元素
     * <b>注意：务必保证预期的结果是指定的Class类型</b>
     *
     * @param key key，非空（可不存在）
     * @param index 下标（可超过实际长度，不会发生越界问题）
     * @param clazz 类型
     * @return 元素或null
     */
    public static <T> T lGet(String key, long index, Class<T> clazz) {
        Object object = l().index(key, index);
        return convertToClazz(object, clazz);
    }

    /**
     * 根据下标范围获取元素列表
     *
     * @param key key，非空
     * @param start 开始下标（含）
     * @param end 结束下标（含），-1表示最后一个，-2表示倒数第二个，以此类推
     * @return 元素列表
     */
    public static List<Object> lGet(String key, long start, long end) {
        return l().range(key, start, end);
    }

    /**
     * 根据下标范围获取元素列表
     * <b>注意：务必保证预期的结果是指定的Class类型</b>
     *
     * @param key key，非空
     * @param start 开始下标（含）
     * @param end 结束下标（含），-1表示最后一个，-2表示倒数第二个，以此类推
     * @param clazz 类型
     * @return 元素列表
     */
    public static <T> List<T> lGet(String key, long start, long end, Class<T> clazz) {
        List<Object> list = l().range(key, start, end);
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<T> resultList = new ArrayList<>(list.size());
        list.forEach(item -> resultList.add(convertToClazz(item, clazz)));
        return resultList;
    }

    /**
     * 指定下标设置值
     *
     * @param key key，非空
     * @param index 下标，注意不能越界，否则抛出异常
     * @param value 值
     */
    public static void lSet(String key, long index, Object value) {
        l().set(key, index, value);
    }

    /**
     * 移除并返回列表第一个元素
     *
     * @param key key，非空
     * @return 左边第一个元素
     */
    public static Object lLeftPop(String key) {
        return l().leftPop(key);
    }

    /**
     * 移除并返回列表第一个元素
     * <b>注意：务必保证预期的结果是指定的Class类型</b>
     *
     * @param key key，非空
     * @param clazz 类型
     * @return 左边第一个元素
     */
    public static <T> T lLeftPop(String key, Class<T> clazz) {
        return convertToClazz(l().leftPop(key), clazz);
    }

    /**
     * 往列表的第一个位置插入一个元素
     *
     * @param key key，非空
     * @param value value
     * @return 插入完成之后列表的长度
     */
    public static Long lLeftPush(String key, Object value) {
        return l().leftPush(key, value);
    }

    /**
     * 把指定集合中的元素依次添加到列表的首位
     *
     * @param key key，非空
     * @param values 指定集合
     * @return 插入完成之后列表的长度
     */
    public static Long lLeftPushAll(String key, Collection<Object> values) {
        return l().leftPushAll(key, values);
    }

    /**
     * 把指定集合中的元素依次添加到列表的首位
     *
     * @param key key，非空
     * @param values 指定集合
     * @return 插入完成之后列表的长度
     */
    public static Long lLeftPushAll(String key, Object... values) {
        return l().leftPushAll(key, values);
    }

    /**
     * 移除并返回列表最后一个元素
     *
     * @param key key，非空
     * @return 右边最后一个元素
     */
    public static Object lRightPop(String key) {
        return l().rightPop(key);
    }

    /**
     * 移除并返回列表最后一个元素
     * <b>注意：务必保证预期的结果是指定的Class类型</b>
     *
     * @param key key，非空
     * @return 右边最后一个元素
     */
    public static <T> T lRightPop(String key, Class<T> clazz) {
        return convertToClazz(l().rightPop(key), clazz);
    }

    /**
     * 往列表的最后一个位置插入一个元素
     *
     * @param key key，非空
     * @param value value
     * @return 插入完成之后列表的长度
     */
    public static Long lRightPush(String key, Object value) {
        return l().rightPush(key, value);
    }

    /**
     * 把指定集合中的元素依次添加到列表的末位
     *
     * @param key key，非空
     * @param values 指定集合
     * @return 插入完成之后列表的长度
     */
    public static Long lRightPushAll(String key, Collection<Object> values) {
        return l().rightPushAll(key, values);
    }

    /**
     * 把指定集合中的元素依次添加到列表的末位
     *
     * @param key key，非空
     * @param values 指定集合
     * @return 插入完成之后列表的长度
     */
    public static Long lRightPushAll(String key, Object... values) {
        return l().rightPushAll(key, values);
    }

    /**
     * 返回列表中的元素个数
     *
     * @param key key，非空
     * @return 元素个数（非占用空间大小）
     */
    public static Long lSize(String key) {
        return l().size(key);
    }

    /**
     * 对列表进行剪裁（只保留指定区间内的元素）
     *
     * @param key key，非空
     * @param start 开始下标（含）
     * @param end 结束下标（含），-1表示最后一个，-2表示倒数第二个，以此类推
     */
    public static void lTrim(String key, long start, long end) {
        l().trim(key, start, end);
    }

    /*====================================set======================================*/

    public static Long sAdd(String key, Object... values) {
        return s().add(key, values);
    }

    public static Set<Object> sGet(String key) {
        return s().members(key);
    }

    public static Long sDel(String key, Object... values) {
        return s().remove(key, values);
    }

    public static Long sSize(String key) {
        return s().size(key);
    }

    public static Boolean sIsMember(String key, Object o) {
        return s().isMember(key, o);
    }

    /*====================================zset======================================*/

    public static Boolean zAdd(String key, Object value, double score) {
        return z().add(key, value, score);
    }

    public static Long zDel(String key, Object... values) {
        return z().remove(key, values);
    }

    public static Double zScore(String key, Object o) {
        return z().score(key, o);
    }

    public static Long zCount(String key, double min, double max) {
        return z().count(key, min, max);
    }

    public static Set<Object> zRange(String key, long start, long end) {
        return z().range(key, start, end);
    }

    public static Long zRank(String key, Object o) {
        return z().rank(key, o);
    }

    public static Long zSize(String key) {
        return z().zCard(key);
    }

    public static Double zIncrement(String key, Object value, double delta) {
        return z().incrementScore(key, value, delta);
    }

    /*====================================hash======================================*/

    public static void hPut(String key, Object hashKey, Object value) {
        h().put(key, hashKey, value);
    }

    public static void hPutAll(String key, Map<?, ?> m) {
        h().putAll(key, m);
    }

    public static void hPutIfAbsent(String key, Object hashKey, Object value) {
        h().putIfAbsent(key, hashKey, value);
    }

    public static Object hGet(String key, Object hashKey) {
        return h().get(key, hashKey);
    }

    public static List<Object> hMultiGet(String key, Collection<Object> hashKeys) {
        return h().multiGet(key, hashKeys);
    }

    public static Long hDel(String key, Object... hashKeys) {
        return h().delete(key, hashKeys);
    }

    public static Boolean hHasKey(String key, Object hashKey) {
        return h().hasKey(key, hashKey);
    }

    public static Set<Object> hKeys(String key) {
        return h().keys(key);
    }

    public static List<Object> hValues(String key) {
        return h().values(key);
    }

    public static Map<Object, Object> hEntries(String key) {
        return h().entries(key);
    }

    public static Long hLengthOfValue(String key, Object hashKey) {
        return h().lengthOfValue(key, hashKey);
    }

    public static Long hSize(String key) {
        return h().size(key);
    }

    public static Long hIncrement(String key, Object hashKey, long delta) {
        return h().increment(key, hashKey, delta);
    }

    /*====================================common======================================*/

    /**
     * 删除单个key
     *
     * @param key key，非空，不支持通配符
     * @return 是否删除成功
     */
    public static Boolean del(String key) {
        return original().delete(key);
    }

    /**
     * 删除多个key
     *
     * @param keys key集合，非空，不支持通配符
     * @return 删除成功的key个数
     */
    public static Long del(Collection<String> keys) {
        return original().delete(keys);
    }

    /**
     * 使用通配符删除key
     * <b>本方法比前者<code>del(String key)</code>多了查询的操作，删除单个请使用前者</b>
     *
     * @param pattern 支持通配符的key
     * @return 删除成功的key个数
     */
    public static Long delPattern(String pattern) {
        Set<String> keys = keys(pattern);
        if (keys == null || keys.isEmpty()) {
            return 0L;
        }
        return del(keys);
    }

    public static Set<String> keys(String pattern) {
        return original().keys(pattern);
    }

    public static Boolean hasKey(String key) {
        return original().hasKey(key);
    }

    public static Boolean expire(String key, long seconds) {
        return expire(key, seconds, TimeUnit.SECONDS);
    }

    public static Boolean expire(String key, long timeout, TimeUnit unit) {
        return original().expire(key, timeout, unit);
    }

    public static Boolean expireAt(String key, Date date) {
        return original().expireAt(key, date);
    }

    public static Long getExpire(String key) {
        return original().getExpire(key);
    }

    public static Long getExpire(String key, TimeUnit unit) {
        return original().getExpire(key, unit);
    }

    @SuppressWarnings("unchecked")
    private static <T> T convertToClazz(Object o, Class<T> clazz) {
        if (o == null) {
            return null;
        }
        return (T) (clazz.equals(Long.class) ? Long.valueOf(o.toString()) : o);
    }

}