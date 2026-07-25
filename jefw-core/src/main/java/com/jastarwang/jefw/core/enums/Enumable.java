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
 * 枚举能力接口
 * <p>
 * 适用于数据结构等于或包含 <code>Code - Value</code> 结构的枚举，实现该接口后可获得增强的功能，详见 {@link com.jastarwang.jefw.core.enums.Enums}
 * </p>
 *
 * @param <C> <code>Code</code>字段的类型，该字段值不可重复
 * @param <V> <code>Value</code>字段的类型，该字段值不可重复
 * @author Jastar Wang
 * @date 2021/7/27
 * @since 1.1
 */
public interface Enumable<C, V> {

    /**
     * 获取枚举中定义的code值
     *
     * @return code
     */
    C getCode();

    /**
     * 获取枚举中定义的value值
     *
     * @return value
     */
    V getValue();

}
