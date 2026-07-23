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
