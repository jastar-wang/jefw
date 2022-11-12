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
package com.jastarwang.jefw.util.test;

import com.jastarwang.jefw.util.SerializeUtil;
import com.jastarwang.jefw.util.test.bean.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

/**
 * 工具单元测试类
 *
 * @author Jastar Wang
 * @date 2020/7/18
 * @since 1.0
 */
public class SerializeUtilTest {

    @Test
    public void serialize() {
        User tom = new User(28, "狗蛋");
        byte[] bytes = SerializeUtil.serialize(tom);
        Assertions.assertNotNull(bytes);
        // deserialize
        User backTom = SerializeUtil.deserialize(bytes);
        Assertions.assertNotNull(backTom);
        Assertions.assertEquals(backTom.getAge(), tom.getAge());
        Assertions.assertEquals(backTom.getName(), tom.getName());
        Assertions.assertNotEquals(backTom, tom);
    }

    @Test
    public void serializeList() {
        ArrayList<User> list = new ArrayList<>();
        list.add(new User(28, "铁柱"));
        byte[] bytes = SerializeUtil.serialize(list);
        Assertions.assertNotNull(bytes);
        // deserialize
        ArrayList<User> backList = SerializeUtil.deserialize(bytes);
        Assertions.assertNotNull(backList);
        Assertions.assertEquals(backList.get(0).getAge(), list.get(0).getAge());
        Assertions.assertEquals(backList.get(0).getName(), list.get(0).getName());
        Assertions.assertNotEquals(backList.get(0), list.get(0));
    }

}
