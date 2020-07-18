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
package com.jastarwang.jefw.utils;

import java.util.Random;

/**
 * 颜色工具类
 *
 * @author Jastar Wang
 * @date 2020/07/18
 * @since 1.0
 */
public class ColorUtil {

    private ColorUtil() {
    }

    /**
     * 随机生成颜色（十六进制）
     *
     * @return 十六进制颜色代码，如：#8BDF31
     */
    public static String randomHex() {
        Random random = new Random();
        String red = Integer.toHexString(random.nextInt(256)).toUpperCase();
        String green = Integer.toHexString(random.nextInt(256)).toUpperCase();
        String blue = Integer.toHexString(random.nextInt(256)).toUpperCase();
        red = red.length() == 1 ? "0" + red : red;
        green = green.length() == 1 ? "0" + green : green;
        blue = blue.length() == 1 ? "0" + blue : blue;
        return "#" + red + green + blue;
    }

}
