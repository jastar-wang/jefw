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
package com.jastarwang.jefw.util.test;

import com.jastarwang.jefw.util.ColorUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.Color;

/**
 * ColorUtil单元测试
 *
 * @author Jastar Wang
 * @date 2020/7/23
 * @since 1.0
 */
public class ColorUtilTest {

    @Test
    public void randomHex() {
        String color = ColorUtil.randomHex();
        Assertions.assertNotNull(color);
        System.out.println(color);
    }

    @Test
    public void convertToHex() {
        String hex = ColorUtil.convertToHex(Color.PINK);
        Assertions.assertNotNull(hex);
        System.out.println(hex);
    }

}
