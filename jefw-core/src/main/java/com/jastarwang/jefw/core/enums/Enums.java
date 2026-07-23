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
package com.jastarwang.jefw.core.enums;

/**
 * 枚举工具类
 * <li>设计愿景：参考 {@link java.util.Collections} 等的设计方式，抽象出枚举在工作中常用且通用的功能，避免逐个类实现</li>
 * <li>使用须知：使用该工具类的枚举需实现 {@link Enumable} 接口</li>
 *
 * <pre>
 * 示例：
 * public enum Gender implements Enumable &lt; Integer, String &gt; {
 *     FEMALE(0, "女"),
 *     MALE(1, "男");
 *
 *     private final Integer code;
 *     private final String value;
 *
 *     Gender(Integer code, String value) {
 *         this.code = code;
 *         this.value = value;
 *     }
 *     // 省略 Getter...
 * }
 *
 * // MALE
 * Gender demo1 = Enums.byCode(Gender.class, 1);
 * // FEMALE
 * Gender demo2 = Enums.byValue(Gender.class, "女");
 * // 1
 * Integer demo3 = Enums.codeByValue(Gender.class, "男");
 * // 保密
 * String demo4 = Enums.valueByCodeOrElse(Gender.class, 2, "保密");
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
     * 根据code获取枚举类对象
     *
     * @param clazz 枚举类
     * @param code  nullable, code
     * @param <T>   枚举类的类型
     * @param <C>   枚举类的Code类型
     * @param <V>   枚举类的Value类型
     * @return 枚举对象, nullable
     */
    public static <T extends Enumable<C, V>, C, V> T byCode(Class<T> clazz, C code) {
        T[] consts = getConsts(clazz, code);
        if (consts == null) {
            return null;
        }
        for (T t : consts) {
            if (code.equals(t.getCode())) {
                return t;
            }
        }
        return null;
    }

    /**
     * 根据code获取枚举类对象，若为空返回默认值
     *
     * @param clazz       枚举类
     * @param code        nullable, code
     * @param defaultEnum nullable,默认对象
     * @param <T>         枚举类的类型
     * @param <C>         枚举类的Code类型
     * @param <V>         枚举类的Value类型
     * @return 枚举对象, nullable
     * @since 1.2.2
     */
    public static <T extends Enumable<C, V>, C, V> T byCodeOrElse(Class<T> clazz, C code, T defaultEnum) {
        T t = byCode(clazz, code);
        return t == null ? defaultEnum : t;
    }

    /**
     * 根据value获取枚举类对象
     *
     * @param clazz 枚举类
     * @param value nullable, value
     * @param <T>   枚举类的类型
     * @param <C>   枚举类的Code类型
     * @param <V>   枚举类的Value类型
     * @return 枚举对象, nullable
     * @since 1.1.3
     */
    public static <T extends Enumable<C, V>, C, V> T byValue(Class<T> clazz, V value) {
        T[] consts = getConsts(clazz, value);
        if (consts == null) {
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
     * 根据value获取枚举类对象，若为空返回默认值
     *
     * @param clazz       枚举类
     * @param value       nullable, value
     * @param defaultEnum nullable,默认对象
     * @param <T>         枚举类的类型
     * @param <C>         枚举类的Code类型
     * @param <V>         枚举类的Value类型
     * @return 枚举对象, nullable
     * @since 1.2.2
     */
    public static <T extends Enumable<C, V>, C, V> T byValueOrElse(Class<T> clazz, V value, T defaultEnum) {
        T t = byValue(clazz, value);
        return t == null ? defaultEnum : t;
    }

    /**
     * 根据code获取枚举类对象的value
     * <p>
     * 该方法 ≈ <code>Enums.byCode(class,code).getValue()</code>
     * 可以解决返回值为空时造成的NPE，减少业务调用处的非空判断
     * </p>
     *
     * @param clazz 枚举类
     * @param code  nullable, code
     * @param <T>   枚举类的类型
     * @param <C>   枚举类的Code类型
     * @param <V>   枚举类的Value类型
     * @return 枚举对象的value, nullable
     */
    public static <T extends Enumable<C, V>, C, V> V valueByCode(Class<T> clazz, C code) {
        T t = byCode(clazz, code);
        return t == null ? null : t.getValue();
    }

    /**
     * 根据code获取枚举类对象的value，若为空返回默认值
     *
     * @param clazz        枚举类
     * @param code         nullable, code
     * @param defaultValue nullable, 默认value
     * @param <T>          枚举类的类型
     * @param <C>          枚举类的Code类型
     * @param <V>          枚举类的Value类型
     * @return 枚举对象的value, nullable
     */
    public static <T extends Enumable<C, V>, C, V> V valueByCodeOrElse(Class<T> clazz, C code, V defaultValue) {
        T t = byCode(clazz, code);
        return t == null ? defaultValue : t.getValue();
    }

    /**
     * 根据value获取枚举类对象的code
     * <p>
     * 该方法 ≈ <code>Enums.byValue(class,value).getCode()</code>
     * 可以解决返回值为空时造成的NPE，减少业务调用处的非空判断
     * </p>
     *
     * @param clazz 枚举类
     * @param value nullable, value
     * @param <T>   枚举类的类型
     * @param <C>   枚举类的Code类型
     * @param <V>   枚举类的Value类型
     * @return 枚举对象的code, nullable
     * @since 1.1.3
     */
    public static <T extends Enumable<C, V>, C, V> C codeByValue(Class<T> clazz, V value) {
        T t = byValue(clazz, value);
        return t == null ? null : t.getCode();
    }

    /**
     * 根据value获取枚举类对象的code，若为空返回默认值
     *
     * @param clazz       枚举类
     * @param value       nullable, value
     * @param defaultCode nullable, 默认code
     * @param <T>         枚举类的类型
     * @param <C>         枚举类的Code类型
     * @param <V>         枚举类的Value类型
     * @return 枚举对象的code, nullable
     * @since 1.1.3
     */
    public static <T extends Enumable<C, V>, C, V> C codeByValueOrElse(Class<T> clazz, V value, C defaultCode) {
        T t = byValue(clazz, value);
        return t == null ? defaultCode : t.getCode();
    }

    private static <T> T[] getConsts(Class<T> clazz, Object arg) {
        T[] consts;
        if (arg == null || (consts = clazz.getEnumConstants()) == null || consts.length <= 0) {
            return null;
        }
        return consts;
    }

}
