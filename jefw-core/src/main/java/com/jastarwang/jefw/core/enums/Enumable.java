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
 * 枚举能力接口
 * <p>
 * 适用于数据结构等于或包含 <code>key(int、String)-value(String)</code>结构的枚举，实现该接口后可获得增强的功能实现，具体参考 {@link Enums}
 * </p>
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

}
