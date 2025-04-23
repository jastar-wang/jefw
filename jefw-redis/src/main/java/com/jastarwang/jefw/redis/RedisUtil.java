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

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
 * 比如：“RedisUtil.set()...”，相关命令请参考 <a href="http://doc.redisfans.com/" target="_blank">Redis命令</a>
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

    public static RedisTemplate<String, Object> template() {
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


    /*==================================== value ======================================*/

    /**
     * 存入值
     *
     * @param key   key，非空
     * @param value value，非空
     */
    public static void set(String key, Object value) {
        v().set(key, value);
    }

    /**
     * 存入值
     *
     * @param key     key，非空
     * @param value   value，非空
     * @param seconds 过期时间（单位：秒）
     */
    public static void set(String key, Object value, long seconds) {
        set(key, value, seconds, TimeUnit.SECONDS);
    }

    /**
     * 存入值
     *
     * @param key      key，非空
     * @param value    value，非空
     * @param timeout  过期时间，非空
     * @param timeUnit 过期时间单位，非空
     */
    public static void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        v().set(key, value, timeout, timeUnit);
    }

    // void set(K key, V value, Duration timeout);

    /**
     * 当值不存在时存入值
     *
     * @param key   key，非空
     * @param value value，非空
     * @return 是否存入成功
     * @since 2.1.1
     */
    public static Boolean setIfAbsent(String key, Object value) {
        return v().setIfAbsent(key, value);
    }

    /**
     * 当值不存在时存入值
     *
     * @param key      key，非空
     * @param value    value，非空
     * @param timeout  过期时间，非空
     * @param timeUnit 过期时间单位，非空
     * @return 是否存入成功
     * @since 2.1.1
     */
    public static Boolean setIfAbsent(String key, Object value, long timeout, TimeUnit timeUnit) {
        return v().setIfAbsent(key, value, timeout, timeUnit);
    }

    /**
     * 当值存在时存入值
     *
     * @param key   key，非空
     * @param value value，非空
     * @return 是否存入成功
     * @since 2.1.1
     */
    public static Boolean setIfPresent(String key, Object value) {
        return v().setIfPresent(key, value);
    }

    /**
     * 当值存在时存入值
     *
     * @param key      key，非空
     * @param value    value，非空
     * @param timeout  过期时间，非空
     * @param timeUnit 过期时间单位，非空
     * @return 是否存入成功
     * @since 2.1.1
     */
    public static Boolean setIfPresent(String key, Object value, long timeout, TimeUnit timeUnit) {
        return v().setIfPresent(key, value, timeout, timeUnit);
    }

    // Boolean setIfAbsent(K key, V value, Duration timeout);

    // Boolean setIfPresent(K key, V value, Duration timeout);

    /**
     * 批量存入
     *
     * @param map 键值map，非空
     */
    public static void multiSet(Map<String, ?> map) {
        v().multiSet(map);
    }

    // Boolean multiSetIfAbsent(Map<? extends K, ? extends V> map);

    /**
     * 获取值
     * <p>注意：在某些序列化方式下，对于结果进行Long类型的强转，会出现异常，编码前请务必了解相关的坑</p>
     *
     * @param key key，非空
     * @return value
     */
    public static Object get(String key) {
        return v().get(key);
    }

    /**
     * 获取值并根据指定类型转换
     *
     * @param key   key，非空
     * @param clazz 类型，非空
     * @param <T>   类型
     * @return value
     */
    public static <T> T get(String key, Class<T> clazz) {
        Object result = get(key);
        return convertToClazz(result, clazz);
    }

    /**
     * 获取指定类型的List值（用于忽略编译告警）
     *
     * @param key   key，非空
     * @param clazz 元素类型，无实际作用，只用于约束编码
     * @param <T>   元素类型
     * @return list
     * @since 1.2.2
     */
    @SuppressWarnings("all")
    public static <T> List<T> getList(String key, Class<T> clazz) {
        return get(key, List.class);
    }

    /**
     * 获取指定类型的Map值（用于忽略编译告警）
     *
     * @param key        key，非空
     * @param keyClazz   键类型，无实际作用，只用于约束编码
     * @param valueClazz 值类型，无实际作用，只用于约束编码
     * @return map
     * @since 1.2.2
     */
    @SuppressWarnings("all")
    public static <K, V> Map<K, V> getMap(String key, Class<K> keyClazz, Class<V> valueClazz) {
        return get(key, Map.class);
    }

    /**
     * 设置新的值并返回旧的值
     *
     * @param key   key，非空
     * @param value value
     * @return 旧值
     */
    public static Object getAndSet(String key, Object value) {
        return v().getAndSet(key, value);
    }

    /**
     * 设置新的值并返回旧的值
     *
     * @param key   key，非空
     * @param value value
     * @param clazz 类型
     * @return 旧值
     */
    public static <T> T getAndSet(String key, Object value, Class<T> clazz) {
        return convertToClazz(v().getAndSet(key, value), clazz);
    }

    /**
     * 批量获取值
     *
     * @param keys key集合，非空
     * @return value集合
     */
    public static List<Object> multiGet(Collection<String> keys) {
        return v().multiGet(keys);
    }

    /**
     * 批量获取值
     * <p>（要求值集合的类型一致）</p>
     *
     * @param keys  key集合，非空
     * @param clazz 类型
     * @return value集合
     */
    public static <T> List<T> multiGet(Collection<String> keys, Class<T> clazz) {
        return convertToClazz(v().multiGet(keys), clazz);
    }

    /**
     * 自增
     * <p>（默认步长：1）</p>
     *
     * @param key key，非空
     * @return 自增后的值
     */
    public static Long increment(String key) {
        return v().increment(key);
    }

    /**
     * 自增
     *
     * @param key   key，非空
     * @param delta 步长
     * @return 自增后的值
     */
    public static Long increment(String key, long delta) {
        return v().increment(key, delta);
    }

    /**
     * 自增
     *
     * @param key   key，非空
     * @param delta 步长
     * @return 自增后的值
     * @since 2.1.1
     */
    public static Double increment(String key, double delta) {
        return v().increment(key, delta);
    }

    /**
     * 自减
     * <p>（默认步长：1）</p>
     *
     * @param key key，非空
     * @return 自减后的值
     */
    public static Long decrement(String key) {
        return v().decrement(key);
    }

    /**
     * 自减
     *
     * @param key   key，非空
     * @param delta 步长
     * @return 自减后的值
     * @since 2.1.1
     */
    public static Long decrement(String key, long delta) {
        return v().decrement(key, delta);
    }

    // Integer append(K key, String value);

    // String get(K key, long start, long end);

    // void set(K key, V value, long offset);

    /**
     * 获取key对应的value的长度
     *
     * @param key key，非空
     * @return value的长度
     */
    public static Long size(String key) {
        return v().size(key);
    }

    // Boolean setBit(K key, long offset, boolean value);

    // Boolean getBit(K key, long offset);

    // List<Long> bitField(K key, BitFieldSubCommands subCommands);


    /*==================================== list ======================================*/

    /**
     * 根据下标范围获取元素列表
     *
     * @param key   key，非空
     * @param start 开始下标（含）
     * @param end   结束下标（含），-1表示最后一个，-2表示倒数第二个，以此类推
     * @return 元素列表
     */
    public static List<Object> lRange(String key, long start, long end) {
        return l().range(key, start, end);
    }

    /**
     * 根据下标范围获取元素列表
     * <b>注意：务必保证预期的结果是指定的Class类型</b>
     *
     * @param key   key，非空
     * @param start 开始下标（含）
     * @param end   结束下标（含），-1表示最后一个，-2表示倒数第二个，以此类推
     * @param clazz 类型
     * @return 元素列表
     */
    public static <T> List<T> lRange(String key, long start, long end, Class<T> clazz) {
        List<Object> list = l().range(key, start, end);
        return convertToClazz(list, clazz);
    }

    /**
     * 对列表进行剪裁（只保留指定区间内的元素）
     *
     * @param key   key，非空
     * @param start 开始下标（含）
     * @param end   结束下标（含），-1表示最后一个，-2表示倒数第二个，以此类推
     */
    public static void lTrim(String key, long start, long end) {
        l().trim(key, start, end);
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
     * 往列表的第一个位置插入一个元素
     *
     * @param key   key，非空
     * @param value value
     * @return 插入完成之后列表的长度
     */
    public static Long lLeftPush(String key, Object value) {
        return l().leftPush(key, value);
    }

    /**
     * 把指定集合中的元素依次添加到列表的首位
     *
     * @param key    key，非空
     * @param values 指定集合
     * @return 插入完成之后列表的长度
     */
    public static Long lLeftPushAll(String key, Object... values) {
        return l().leftPushAll(key, values);
    }

    /**
     * 把指定集合中的元素依次添加到列表的首位
     *
     * @param key    key，非空
     * @param values 指定集合
     * @return 插入完成之后列表的长度
     */
    public static Long lLeftPushAll(String key, Collection<Object> values) {
        return l().leftPushAll(key, values);
    }

    // Long leftPushIfPresent(K key, V value);

    // Long leftPush(K key, V pivot, V value);

    /**
     * 往列表的最后一个位置插入一个元素
     *
     * @param key   key，非空
     * @param value value
     * @return 插入完成之后列表的长度
     */
    public static Long lRightPush(String key, Object value) {
        return l().rightPush(key, value);
    }

    /**
     * 把指定集合中的元素依次添加到列表的末位
     *
     * @param key    key，非空
     * @param values 指定集合
     * @return 插入完成之后列表的长度
     */
    public static Long lRightPushAll(String key, Object... values) {
        return l().rightPushAll(key, values);
    }

    /**
     * 把指定集合中的元素依次添加到列表的末位
     *
     * @param key    key，非空
     * @param values 指定集合
     * @return 插入完成之后列表的长度
     */
    public static Long lRightPushAll(String key, Collection<Object> values) {
        return l().rightPushAll(key, values);
    }

    // Long rightPushIfPresent(K key, V value);

    // Long rightPush(K key, V pivot, V value);

    /**
     * 指定下标设置值
     *
     * @param key   key，非空
     * @param index 下标，注意不能越界，否则抛出异常
     * @param value 值
     */
    public static void lSet(String key, long index, Object value) {
        l().set(key, index, value);
    }

    // Long remove(K key, long count, Object value);

    /**
     * 根据下标获取指定元素
     *
     * @param key   key，非空（可不存在）
     * @param index 下标（可超过实际长度，不会发生越界问题）
     * @return 元素或null
     */
    public static Object lIndex(String key, long index) {
        return l().index(key, index);
    }

    /**
     * 根据下标获取指定元素
     * <b>注意：务必保证预期的结果是指定的Class类型</b>
     *
     * @param key   key，非空（可不存在）
     * @param index 下标（可超过实际长度，不会发生越界问题）
     * @param clazz 类型
     * @return 元素或null
     */
    public static <T> T lIndex(String key, long index, Class<T> clazz) {
        Object object = l().index(key, index);
        return convertToClazz(object, clazz);
    }

    // Long indexOf(K key, V value);

    // Long lastIndexOf(K key, V value);

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
     * @param key   key，非空
     * @param clazz 类型
     * @return 左边第一个元素
     */
    public static <T> T lLeftPop(String key, Class<T> clazz) {
        return convertToClazz(l().leftPop(key), clazz);
    }

    // V leftPop(K key, long timeout, TimeUnit unit);

    // V leftPop(K key, Duration timeout);

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

    // V rightPop(K key, long timeout, TimeUnit unit);

    // V rightPop(K key, Duration timeout);

    // V rightPopAndLeftPush(K sourceKey, K destinationKey);

    // V rightPopAndLeftPush(K sourceKey, K destinationKey, long timeout, TimeUnit unit);

    // V rightPopAndLeftPush(K sourceKey, K destinationKey, Duration timeout)


    /*==================================== set ======================================*/

    /**
     * 将一个或多个元素添加到集合中，已存在的元素将忽略
     *
     * @param key    key，非空
     * @param values 一个或多个元素
     * @return 添加成功的元素数量，不包括被忽略的元素
     */
    public static Long sAdd(String key, Object... values) {
        return s().add(key, values);
    }

    /**
     * 删除集合中的一个或多个元素，不存在的元素将忽略
     *
     * @param key    key，非空
     * @param values 一个或多个元素
     * @return 删除成功的元素数量，不包括被忽略的元素
     */
    public static Long sRemove(String key, Object... values) {
        return s().remove(key, values);
    }

    // V pop(K key);

    // List<V> pop(K key, long count);

    // Boolean move(K key, V value, K destKey);

    /**
     * 获取集合中的元素数量
     *
     * @param key key，非空
     * @return 元素数量，key不存在时返回0
     */
    public static Long sSize(String key) {
        return s().size(key);
    }

    /**
     * 判断指定元素是否存在于集合中
     *
     * @param key key，非空
     * @param o   指定元素
     * @return 是否存在集合中
     */
    public static Boolean sIsMember(String key, Object o) {
        return s().isMember(key, o);
    }

    // Set<V> intersect..(..);

    // Set<V> union..(..);

    // Set<V> difference..(..);

    /**
     * 获取集合中的所有元素，不存在的key视为空集合
     *
     * @param key key，非空
     * @return 集合
     */
    public static Set<Object> sMembers(String key) {
        return s().members(key);
    }

    /**
     * 获取集合中的所有元素，不存在的key视为空集合
     *
     * @param key   key，非空
     * @param clazz 类型
     * @return 集合
     */
    public static <T> Set<T> sMembers(String key, Class<T> clazz) {
        Set<Object> members = s().members(key);
        return convertToClazz(members, clazz);
    }

    // V randomMember(K key);

    // Set<V> distinctRandomMembers(K key, long count);

    // List<V> randomMembers(K key, long count);

    // Cursor<V> scan(K key, ScanOptions options);


    /*==================================== zset ======================================*/

    /**
     * 将元素添加到有序集合中，已存在的元素将覆盖分值并计算新位置
     *
     * @param key   key，非空
     * @param value 元素
     * @param score 元素对应的分值
     * @return 是否添加成功
     */
    public static Boolean zAdd(String key, Object value, double score) {
        return z().add(key, value, score);
    }

    // Boolean addIfAbsent(K key, V value, double score);

    // Long add(K key, Set<TypedTuple<V>> tuples);

    // Long addIfAbsent(K key, Set<TypedTuple<V>> tuples);

    /**
     * 删除有序集合中的一个或多个元素，不存在的元素将忽略
     *
     * @param key    key，非空
     * @param values 一个或多个元素
     * @return 删除成功的元素数量，不包括被忽略的元素
     */
    public static Long zRemove(String key, Object... values) {
        return z().remove(key, values);
    }

    /**
     * 将有序集合中的指定元素对应的分值进行加减计算
     *
     * @param key   key，非空
     * @param value 指定元素
     * @param delta 分值，负数表示减
     * @return 该元素新的分值
     */
    public static Double zIncrement(String key, Object value, double delta) {
        return z().incrementScore(key, value, delta);
    }

    /**
     * 获取指定元素在有序集合中的排名（分值由小到大排序，从0开始）
     *
     * @param key key，非空
     * @param o   指定元素
     * @return 元素下标，key不存在或元素不存在则返回null
     */
    public static Long zRank(String key, Object o) {
        return z().rank(key, o);
    }

    /**
     * 获取指定元素在有序集合中的排名（分值由大到小排序）
     *
     * @param key key，非空
     * @param o   指定元素
     * @return 元素下标，key不存在或元素不存在则返回null
     */
    public static Long zReverseRank(String key, Object o) {
        return z().reverseRank(key, o);
    }

    /**
     * 获取集合中指定下标区间范围内的元素集合
     *
     * @param key   key，非空
     * @param start 开始下标，从0开始，超出不会引起错误
     * @param end   结束下标，-1表示最后一个，-2表示倒数第二个，以此类推
     * @return 元素集合
     */
    public static Set<Object> zRange(String key, long start, long end) {
        return z().range(key, start, end);
    }

    /**
     * 获取集合中指定下标区间范围内的元素集合
     *
     * @param key   key，非空
     * @param start 开始下标，从0开始，超出不会引起错误
     * @param end   结束下标，-1表示最后一个，-2表示倒数第二个，以此类推
     * @param clazz 类型
     * @return 元素集合
     */
    public static <T> Set<T> zRange(String key, long start, long end, Class<T> clazz) {
        Set<Object> set = z().range(key, start, end);
        return convertToClazz(set, clazz);
    }

    // Set<TypedTuple<V>> rangeWithScores(K key, long start, long end);

    // Set<V> rangeByScore(K key, double min, double max);

    // Set<TypedTuple<V>> rangeByScoreWithScores(K key, double min, double max);

    // Set<V> rangeByScore(K key, double min, double max, long offset, long count);

    // Set<TypedTuple<V>> rangeByScoreWithScores(K key, double min, double max, long offset, long count);

    // Set<V> reverseRange(K key, long start, long end);

    // Set<TypedTuple<V>> reverseRangeWithScores(K key, long start, long end);

    // Set<V> reverseRangeByScore(K key, double min, double max);

    // Set<TypedTuple<V>> reverseRangeByScoreWithScores(K key, double min, double max);

    // Set<V> reverseRangeByScore(K key, double min, double max, long offset, long count);

    // Set<TypedTuple<V>> reverseRangeByScoreWithScores(K key, double min, double max, long offset, long count);

    /**
     * 获取集合中分值在[min,max]范围内的元素数量
     *
     * @param key key，非空
     * @param min 最小值（含）
     * @param max 最大值（含）
     * @return 元素数量
     */
    public static Long zCount(String key, double min, double max) {
        return z().count(key, min, max);
    }

    // Long lexCount(K key, RedisZSetCommands.Range range);

    /**
     * 获取有序集合中的元素数量
     *
     * @param key key，非空
     * @return 元素数量，key不存在时返回0
     */
    public static Long zSize(String key) {
        return z().zCard(key);
    }

    // Long zCard(K key);

    /**
     * 获取集合中指定元素对应的分值
     *
     * @param key key，非空
     * @param o   指定元素
     * @return 分值，key不存在或元素不存在则返回null
     */
    public static Double zScore(String key, Object o) {
        return z().score(key, o);
    }

    // ...


    /*==================================== hash ======================================*/

    /**
     * 删除一个或多个filed，不存在的将忽略
     *
     * @param key      key，非空
     * @param hashKeys hashKey集合，非空
     * @return 删除成功的数量，不包括被忽略的
     */
    public static Long hDel(String key, Object... hashKeys) {
        return h().delete(key, hashKeys);
    }

    /**
     * 查询是否存在指定的filed
     *
     * @param key     key，非空
     * @param hashKey hashKey，非空
     * @return 是否存在
     */
    public static Boolean hHasKey(String key, Object hashKey) {
        return h().hasKey(key, hashKey);
    }

    /**
     * 获取指定key的指定field
     *
     * @param key     key，非空
     * @param hashKey hashKey，非空
     * @return 值
     */
    public static Object hGet(String key, Object hashKey) {
        return h().get(key, hashKey);
    }

    /**
     * 获取指定key的指定field
     *
     * @param key     key，非空
     * @param hashKey hashKey，非空
     * @param clazz   类型
     * @return 值
     */
    public static <T> T hGet(String key, Object hashKey, Class<T> clazz) {
        return convertToClazz(h().get(key, hashKey), clazz);
    }

    /**
     * 批量获取
     *
     * @param key      key，非空
     * @param hashKeys hashKey集合，非空
     * @return 值的集合，顺序与key的顺序一致
     */
    public static List<Object> hMultiGet(String key, Collection<Object> hashKeys) {
        return h().multiGet(key, hashKeys);
    }

    /**
     * 将指定field对应的value进行自增
     *
     * @param key     key，非空
     * @param hashKey hashKey，非空
     * @return 自增后的值
     */
    public static Long hIncrement(String key, Object hashKey) {
        return hIncrement(key, hashKey, 1);
    }

    /**
     * 将指定field对应的value进行自增
     *
     * @param key     key，非空
     * @param hashKey hashKey，非空
     * @param delta   步长
     * @return 自增后的值
     */
    public static Long hIncrement(String key, Object hashKey, long delta) {
        return h().increment(key, hashKey, delta);
    }

    /**
     * 将指定field对应的value进行自增
     *
     * @param key     key，非空
     * @param hashKey hashKey，非空
     * @param delta   步长
     * @return 自增后的值
     */
    public static Double hIncrement(String key, Object hashKey, double delta) {
        return h().increment(key, hashKey, delta);
    }

    /**
     * 获取所有的filed集合
     *
     * @param key key，非空
     * @return filed集合
     */
    public static Set<Object> hKeys(String key) {
        return h().keys(key);
    }

    /**
     * 获取value的长度
     *
     * @param key     key，非空
     * @param hashKey hashKey，非空
     * @return 长度
     */
    public static Long hLengthOfValue(String key, Object hashKey) {
        return h().lengthOfValue(key, hashKey);
    }

    /**
     * 获取filed的数量
     *
     * @param key key，非空
     * @return field的数量
     */
    public static Long hSize(String key) {
        return h().size(key);
    }

    /**
     * 批量设置值
     *
     * @param key key，非空
     * @param m   hashKey和值
     */
    public static void hPutAll(String key, Map<?, ?> m) {
        h().putAll(key, m);
    }

    /**
     * 设置值
     *
     * @param key     key，非空
     * @param hashKey hashKey，非空
     * @param value   值
     */
    public static void hPut(String key, Object hashKey, Object value) {
        h().put(key, hashKey, value);
    }

    /**
     * 仅当指定的hashKey不存在的时候设置值
     *
     * @param key     key，非空
     * @param hashKey hashKey，非空
     * @param value   值
     */
    public static void hPutIfAbsent(String key, Object hashKey, Object value) {
        h().putIfAbsent(key, hashKey, value);
    }

    /**
     * 获取所有的value集合
     *
     * @param key key，非空
     * @return value集合
     */
    public static List<Object> hValues(String key) {
        return h().values(key);
    }

    /**
     * 获取所有的field和value的Map
     *
     * @param key key，非空
     * @return map
     */
    public static Map<Object, Object> hEntries(String key) {
        return h().entries(key);
    }


    /*==================================== common ======================================*/

    /**
     * 删除单个key
     *
     * @param key key，非空，不支持通配符
     * @return 是否删除成功
     */
    public static Boolean del(String key) {
        return template().delete(key);
    }

    /**
     * 删除多个key
     *
     * @param keys key集合，非空，不支持通配符
     * @return 删除成功的key个数
     */
    public static Long del(Collection<String> keys) {
        return template().delete(keys);
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

    /**
     * 根据通配符获取key的集合
     *
     * @param pattern 通配符
     * @return key的集合
     */
    public static Set<String> keys(String pattern) {
        return template().keys(pattern);
    }

    /**
     * 查询是否存在对应的key
     *
     * @param key key，非空
     * @return 是否存在
     */
    public static Boolean hasKey(String key) {
        return template().hasKey(key);
    }

    /**
     * 将指定的key设置过期时间
     *
     * @param key     key，非空
     * @param seconds 过期时间（单位：秒）
     * @return 是否设置成功
     */
    public static Boolean expire(String key, long seconds) {
        return expire(key, seconds, TimeUnit.SECONDS);
    }

    /**
     * 将指定的key设置过期时间
     *
     * @param key     key，非空
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return 是否设置成功
     */
    public static Boolean expire(String key, long timeout, TimeUnit unit) {
        return template().expire(key, timeout, unit);
    }

    /**
     * 将指定的key设置到某时间过期
     *
     * @param key  key，非空
     * @param date 过期时间
     * @return 是否设置成功
     */
    public static Boolean expireAt(String key, Date date) {
        return template().expireAt(key, date);
    }

    /**
     * 获取指定key的过期时间
     *
     * @param key key，非空
     * @return 剩余的过期时间（单位：秒）
     */
    public static Long getExpire(String key) {
        return template().getExpire(key);
    }

    /**
     * 获取指定key的过期时间
     *
     * @param key  key，非空
     * @param unit 时间单位
     * @return 剩余的过期时间
     */
    public static Long getExpire(String key, TimeUnit unit) {
        return template().getExpire(key, unit);
    }

    @SuppressWarnings("unchecked")
    private static <T> T convertToClazz(Object o, Class<T> clazz) {
        if (o == null) {
            return null;
        }
        if (clazz.equals(Long.class)) {
            return (T) Long.valueOf(o.toString());
        } else if (clazz.equals(LocalDate.class)) {
            return (T) LocalDate.parse(o.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } else if (clazz.equals(LocalTime.class)) {
            return (T) LocalTime.parse(o.toString(), DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        } else if (clazz.equals(LocalDateTime.class)) {
            return (T) LocalDateTime.parse(o.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        }
        return (T) o;
    }

    private static <T> Set<T> convertToClazz(Set<Object> set, Class<T> clazz) {
        if (set == null || set.isEmpty()) {
            return null;
        }
        Set<T> targetSet = new LinkedHashSet<>(set.size());
        set.forEach(item -> targetSet.add(convertToClazz(item, clazz)));
        return targetSet;
    }

    private static <T> List<T> convertToClazz(List<Object> list, Class<T> clazz) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<T> targetList = new ArrayList<>(list.size());
        list.forEach(item -> targetList.add(convertToClazz(item, clazz)));
        return targetList;
    }

}