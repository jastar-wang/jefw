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
package com.jastarwang.jefw.core.enums;

/**
 * 枚举工具类
 * <li>拆分出此类参考自 <code>java.util.Collections</code> 等的设计方式</li>
 * <li>设计愿景：抽象出枚举在工作中常用且通用的功能，避免逐个类实现</li>
 * <li>使用须知：使用该工具类的枚举需实现 {@link Enumable} 接口</li>
 *
 * <pre>
 * 示例：
 * // 根据“值（或名key）为1”的条件获得指定枚举对象
 * TestEnum testEnum = Enums.of(TestEnum.class, 1);
 *
 * // 根据“值（或名key）为1”的条件获得指定枚举对象对应的value
 * String value = Enums.valueOf(TestEnum.class, 1);
 * </pre>
 *
 * @author Jastar Wang
 * @date 2021/8/23
 * @since 1.1
 */
public class Enums {

    private Enums() {
    }

    /**
     * 根据指定key获取指定枚举类的对象
     *
     * @param clazz 指定枚举类Class
     * @param key   key
     * @param <K>   一般为 int 或 String
     * @param <T>   指定枚举类
     * @return 枚举对象
     */
    public static <K, T extends Enumable> T of(Class<T> clazz, K key) {
        T[] consts;
        if (key == null || (consts = clazz.getEnumConstants()) == null || consts.length <= 0) {
            return null;
        }
        for (T t : consts) {
            if (key.equals(t.getKey())) {
                return t;
            }
        }
        return null;
    }

    /**
     * 根据指定value获取指定枚举类的对象
     *
     * @param clazz 指定枚举类Class
     * @param value value
     * @param <V>   value的类型
     * @param <T>   指定枚举类
     * @return 枚举对象
     * @since 1.1.3
     */
    public static <V, T extends Enumable> T ofValue(Class<T> clazz, V value) {
        T[] consts;
        if (value == null || (consts = clazz.getEnumConstants()) == null || consts.length <= 0) {
            return null;
        }
        for (T t : consts) {
            if (value.equals(t.getValue())) {
                return t;
            }
        }
        return null;
    }

    /**
     * 根据指定key获取指定枚举类的value
     * <br>
     * <b>
     * 该方法 ≈ <code>Enumable.of(class,key).getValue()</code>
     * 可以解决key为空或没有匹配枚举对象时造成的NPE，减少业务调用处的非空判断
     * </b>
     *
     * @param clazz 指定枚举类Class
     * @param key   key
     * @param <K>   一般为 int 或 String
     * @param <V>   一般为String
     * @param <T>   指定枚举类
     * @return 枚举对象的value
     */
    public static <K, V, T extends Enumable> V valueOf(Class<T> clazz, K key) {
        T t = of(clazz, key);
        return t == null ? null : t.getValue();
    }

    /**
     * 根据指定key获取指定枚举类的value，若为空则返回默认值
     *
     * @param clazz        指定枚举类Class
     * @param key          key
     * @param defaultValue value为空时的默认值
     * @param <K>          一般为 int 或 String
     * @param <V>          一般为String
     * @param <T>          指定枚举类
     * @return 枚举对象的value
     */
    public static <K, V, T extends Enumable> V valueOfOrElse(Class<T> clazz, K key, V defaultValue) {
        T t = of(clazz, key);
        return t == null ? defaultValue : t.getValue();
    }

    /**
     * 根据指定value获取指定枚举类的key
     * <br>
     * <b>
     * 该方法 ≈ <code>Enumable.ofValue(class,value).getKey()</code>
     * 可以解决value为空或没有匹配枚举对象时造成的NPE，减少业务调用处的非空判断
     * </b>
     *
     * @param clazz 指定枚举类Class
     * @param value value
     * @param <K>   一般为 int 或 String
     * @param <V>   一般为String
     * @param <T>   指定枚举类
     * @return 枚举对象的key
     * @since 1.1.3
     */
    public static <K, V, T extends Enumable> K keyOf(Class<T> clazz, V value) {
        T t = ofValue(clazz, value);
        return t == null ? null : t.getKey();
    }

    /**
     * 根据指定value获取指定枚举类的key，若为空则返回默认值
     *
     * @param clazz      指定枚举类Class
     * @param value      value
     * @param defaultKey key为空时的默认值
     * @param <K>        一般为 int 或 String
     * @param <V>        一般为String
     * @param <T>        指定枚举类
     * @return 枚举对象的key
     * @since 1.1.3
     */
    public static <K, V, T extends Enumable> K keyOfOrElse(Class<T> clazz, V value, K defaultKey) {
        T t = ofValue(clazz, value);
        return t == null ? defaultKey : t.getKey();
    }

}
