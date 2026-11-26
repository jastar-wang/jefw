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
package com.jastarwang.jefw.core.consts;

/**
 * 常用的规则、模式、表达式
 *
 * @author Jastar Wang
 * @since 1.3.0
 */
public class Pattern {
    /**
     * 日期时间
     *
     * @author Jastar Wang
     * @since 1.3.0
     */
    public interface Date {
        /**
         * 标准的日期格式
         */
        String NORMAL_DATE = "yyyy-MM-dd";
        /**
         * 标准的时间格式
         */
        String NORMAL_TIME = "HH:mm:ss";
        /**
         * 标准的时间带毫秒格式
         */
        String NORMAL_TIME_MS = "HH:mm:ss.SSS";
        /**
         * 标准的日期时间格式
         */
        String NORMAL_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
        /**
         * 标准的日期时间带毫秒格式
         */
        String NORMAL_DATE_TIME_MS = "yyyy-MM-dd HH:mm:ss.SSS";
    }
}
