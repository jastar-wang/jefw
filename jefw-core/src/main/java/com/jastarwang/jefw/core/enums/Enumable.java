package com.jastarwang.jefw.core.enums;

/**
 * 枚举接口
 * <li>设计愿景：抽象出在工作中常用且通用的功能，减少每个枚举类的实现</li>
 * <li>该基类适用于数据结构等于或包含 <code>key(int、String)-value(String)</code>结构的枚举</li>
 *
 * <pre>
 * 例如：
 * // 根据“值（或名key）为1”的条件获得指定枚举对象
 * TestEnum testEnum = Enumable.of(TestEnum.class, 1);
 *
 * // 根据“值（或名key）为1”的条件获得指定枚举对象对应的value
 * String value = Enumable.valueOf(TestEnum.class, 1);
 * </pre>
 *
 * @author Jastar Wang
 * @date 2021/7/27
 * @since 1.1
 */
public interface Enumable {

    /**
     * 获取枚举中定义的key值
     *
     * @param <K> 一般为 int 或 String
     * @return key
     */
    <K> K getKey();

    /**
     * 获取枚举中定义的value值
     *
     * @param <V> 一般为String
     * @return value
     */
    <V> V getValue();

    /**
     * 根据指定key获取指定枚举类的对象
     *
     * @param clazz 指定枚举类Class
     * @param key   key
     * @param <K>   一般为 int 或 String
     * @param <T>   指定枚举类
     * @return 枚举对象
     */
    static <K, T extends Enumable> T of(Class<T> clazz, K key) {
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
    static <K, V, T extends Enumable> V valueOf(Class<T> clazz, K key) {
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
    static <K, V, T extends Enumable> V valueOfOrElse(Class<T> clazz, K key, V defaultValue) {
        T t = of(clazz, key);
        return t == null ? defaultValue : t.getValue();
    }

}
