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
package com.jastarwang.jefw.redis.test;

import com.jastarwang.jefw.redis.RedisUtil;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RedisUtil单元测试
 *
 * @author Jastar Wang
 * @date 2020/09/22
 * @since 1.0
 */
@EnableAutoConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@SpringBootConfiguration // 必须加此注解单元测试才不报错
public class RedisUtilTest {

    private final Student zhangsan = new Student("张三", 28L);
    private final Student lisi = new Student("李四", 19L);
    private final Student wangwu = new Student("王五", 26L);
    private final Student zhaoliu = new Student("赵六", 22L);

    @Test
    public void contextLoads() {
        // done
    }

    @Test
    public void valueTest1() {
        reset("a", "b");

        RedisUtil.set("a", 111);
        RedisUtil.set("b", new Student("张三", 28L), 60);
        try {
            // 错误的方式（某些场景下）
            Long a1 = (Long) RedisUtil.get("a");
        } catch (ClassCastException e) {
            System.out.println("you see you，出错了吧！");
        }
        // 正确的方式1
        Object a2 = RedisUtil.get("a");
        Long a = a2 == null ? null : Long.parseLong(a2.toString());
        // 正确的方式2
        Long a3 = RedisUtil.get("a", Long.class);
        Student b = RedisUtil.get("b", Student.class);

        Assert.assertNotNull(a);
        Assert.assertNotNull(b);
        Assert.assertNotNull(a3);
        Assert.assertEquals(111L, a.longValue());
        Assert.assertEquals(111L, a3.longValue());
        System.out.println(b);
    }

    @Test
    public void valueTest2() {
        reset("a", "m1", "m2", "m3", "m4", "m5");

        RedisUtil.set("a", "张三");
        String oldValue = (String) RedisUtil.getAndSet("a", "李四");
        Assert.assertEquals("张三", oldValue);

        Map<String, Student> map = new HashMap<>();
        map.put("m1", new Student("tony", 18L));
        map.put("m2", new Student("liming", 19L));
        RedisUtil.multiSet(map);

        List<Object> objects = RedisUtil.multiGet(Arrays.asList("m1", "m2"));
        Assert.assertNotNull(objects);
        System.out.println(objects);

        RedisUtil.increment("m3", null);
        RedisUtil.increment("m4", 2L);
        RedisUtil.increment("m5", -1L);

        System.out.println(RedisUtil.size("m1"));
    }

    @Test
    public void listTest1() {
        reset("l1", "nothiskey");

        RedisUtil.lRightPush("l1", zhangsan);
        RedisUtil.lRightPushAll("l1", lisi, wangwu);
        Student l1Index0 = RedisUtil.lGet("l1", 0, Student.class);
        Assert.assertEquals("张三", l1Index0.getName());

        List<Object> list1 = RedisUtil.lGet("l1", 1, -1);
        List<Student> list2 = RedisUtil.lGet("l1", 1, -1, Student.class);
        System.out.println(list1);
        System.out.println(list2);

        System.out.println("验证key或index不存在的情况下：");
        System.out.println(RedisUtil.lGet("nothiskey", 0));
        System.out.println(RedisUtil.lGet("l1", 10, Student.class));

        Assert.assertThrows(Exception.class, () -> RedisUtil.lSet("l1", 6, zhaoliu));
    }

    @Test
    public void listTest2() {
        reset("l2", "l2-none");

        RedisUtil.lRightPushAll("l2", zhangsan, lisi, wangwu);
        // 测试key不存在的情况下返回null
        System.out.println(RedisUtil.lLeftPop("l2-none"));
        // 正常场景
        System.out.println(RedisUtil.lLeftPop("l2"));
        System.out.println(RedisUtil.lLeftPop("l2", Student.class));
    }

    @Test
    public void listTest3() {
        reset("l3");

        Long temp1 = RedisUtil.lLeftPush("l3", zhangsan);
        Long temp2 = RedisUtil.lLeftPushAll("l3", Arrays.asList(lisi, wangwu));
        Long temp3 = RedisUtil.lLeftPushAll("l3", zhaoliu, zhaoliu);
        Assert.assertEquals(1, temp1.longValue());
        Assert.assertEquals(3, temp2.longValue());
        Assert.assertEquals(5, temp3.longValue());

        Long size = RedisUtil.lSize("l3");
        Assert.assertEquals(5, size.longValue());

        RedisUtil.lTrim("l3", 1, 3);
    }

    private void reset(String... keys) {
        RedisUtil.del(Arrays.asList(keys));
    }
}
