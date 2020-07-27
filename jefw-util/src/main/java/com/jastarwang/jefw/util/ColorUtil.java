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
package com.jastarwang.jefw.util;

import java.awt.*;
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
     * 随机生成十六进制颜色
     *
     * @return 十六进制颜色代码
     */
    public static String randomHex() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return buildHex(r, g, b);
    }

    /**
     * 将{@link java.awt.Color}转换为十六进制代码
     *
     * @param color 颜色
     * @return 十六进制颜色代码，或空
     */
    public static String convertToHex(Color color) {
        if (color == null) {
            return null;
        }
        return buildHex(color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * 指定R,G,B来构建十六进制颜色
     *
     * @param red 红色
     * @param green 绿色
     * @param blue 蓝色
     * @return 十六进制颜色代码
     */
    private static String buildHex(int red, int green, int blue) {
        String r = Integer.toHexString(red);
        String g = Integer.toHexString(green);
        String b = Integer.toHexString(blue);
        r = r.length() == 1 ? "0" + r : r;
        g = g.length() == 1 ? "0" + g : g;
        b = b.length() == 1 ? "0" + b : b;
        return "#" + (r + g + b).toUpperCase();
    }
}
