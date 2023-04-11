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
package com.jastarwang.jefw.core.consts;

/**
 * 常用的规则、模式、表达式
 *
 * @author Jastar Wang
 * @date 2023/4/5
 * @since 1.3.0
 */
public class Pattern {
    /**
     * 日期时间
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
