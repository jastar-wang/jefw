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
package com.jastarwang.jefw.util;

import java.awt.Color;
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
