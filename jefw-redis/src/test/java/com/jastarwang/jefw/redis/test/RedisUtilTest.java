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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static com.jastarwang.jefw.redis.RedisUtil.*;

/**
 * RedisUtil单元测试
 *
 * @author Jastar Wang
 * @date 2020/09/22
 * @since 1.0
 */
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
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

        set("a", 111);
        set("b", new Student("张三", 28L), 60);
        try {
            // 错误的方式（某些场景下）
            Long a1 = (Long) get("a");
        } catch (ClassCastException e) {
            System.out.println("you see you，出错了吧！");
        }
        // 正确的方式1
        Object a2 = get("a");
        Long a = a2 == null ? null : Long.parseLong(a2.toString());
        // 正确的方式2
        Long a3 = get("a", Long.class);
        Student b = get("b", Student.class);

        Assertions.assertNotNull(a);
        Assertions.assertNotNull(b);
        Assertions.assertNotNull(a3);
        Assertions.assertEquals(111L, a.longValue());
        Assertions.assertEquals(111L, a3.longValue());
        System.out.println(b);
    }

    @Test
    public void valueTest2() {
        reset("a", "m1", "m2", "m3", "m4", "m5");

        set("a", "张三");
        String oldValue = (String) getAndSet("a", "李四");
        Assertions.assertEquals("张三", oldValue);

        Map<String, Student> map = new HashMap<>();
        map.put("m1", new Student("tony", 18L));
        map.put("m2", new Student("liming", 19L));
        multiSet(map);

        List<Object> objects = multiGet(Arrays.asList("m1", "m2"));
        Assertions.assertNotNull(objects);
        System.out.println(objects);

        increment("m3");
        increment("m4", 2L);
        increment("m5", -1L);

        System.out.println(size("m1"));
    }

    @Test
    public void listTest1() {
        reset("l1", "nothiskey");

        lRightPush("l1", zhangsan);
        lRightPushAll("l1", lisi, wangwu);
        Student l1Index0 = lIndex("l1", 0, Student.class);
        Assertions.assertEquals("张三", l1Index0.getName());

        List<Object> list1 = lRange("l1", 1, -1);
        List<Student> list2 = lRange("l1", 1, -1, Student.class);
        System.out.println(list1);
        System.out.println(list2);

        System.out.println("验证key或index不存在的情况下：");
        System.out.println(lIndex("nothiskey", 0));
        System.out.println(lIndex("l1", 10, Student.class));

        Assertions.assertThrows(Exception.class, () -> lSet("l1", 6, zhaoliu));
    }

    @Test
    public void listTest2() {
        reset("l2", "l2-none");

        lRightPushAll("l2", zhangsan, lisi, wangwu);
        // 测试key不存在的情况下返回null
        System.out.println(lLeftPop("l2-none"));
        // 正常场景
        System.out.println(lLeftPop("l2"));
        System.out.println(lLeftPop("l2", Student.class));
    }

    @Test
    public void listTest3() {
        reset("l3");

        Long temp1 = lLeftPush("l3", zhangsan);
        Long temp2 = lLeftPushAll("l3", Arrays.asList(lisi, wangwu));
        Long temp3 = lLeftPushAll("l3", zhaoliu, zhaoliu);
        Assertions.assertEquals(1, temp1.longValue());
        Assertions.assertEquals(3, temp2.longValue());
        Assertions.assertEquals(5, temp3.longValue());

        Long size = lSize("l3");
        Assertions.assertEquals(5, size.longValue());

        lTrim("l3", 1, 3);
    }

    @Test
    public void setTest() {
        reset("s1");

        Long size1 = sAdd("s1", zhangsan, lisi);
        Assertions.assertEquals(2, size1.longValue());

        Long size2 = sAdd("s1", wangwu);
        Assertions.assertEquals(1, size2.longValue());

        Long size3 = sAdd("s1", wangwu);
        Assertions.assertEquals(0, size3.longValue());

        Set<Object> members1 = sMembers("s1");
        System.out.println(members1);
        System.out.println(members1.getClass().getName());

        Set<Student> members2 = sMembers("s1", Student.class);
        System.out.println(members2);
        System.out.println(members2.getClass().getName());

        Long size4 = sRemove("s1", lisi, zhaoliu);
        Assertions.assertEquals(1, size4.longValue());

        Long size5 = sSize("s1");
        Assertions.assertEquals(2, size5.longValue());

        Boolean isMember = sIsMember("s1", wangwu);
        Assertions.assertEquals(true, isMember);
    }

    @Test
    public void zsetTest() {
        reset("z1");

        Boolean bool1 = zAdd("z1", lisi, 11);
        Assertions.assertEquals(true, bool1);

        zAdd("z1", wangwu, 8.0);

        Double score = zScore("z1", zhaoliu);
        System.out.println(score);

        Long delCount = zRemove("z1", lisi);
        System.out.println(delCount);

        zAdd("z1", lisi, 12);
        zAdd("z1", zhaoliu, 6);
        Long count = zCount("z1", 4, 6);
        Assertions.assertEquals(1, count.longValue());

        Set<Object> set1 = zRange("z1", 1, 2);
        System.out.println(set1);

        Set<Student> set2 = zRange("z1", 1, 2, Student.class);
        System.out.println(set2);

        Long index = zRank("z1", wangwu);
        Assertions.assertEquals(1, index.longValue());

        Long size1 = zSize("z1");
        Assertions.assertEquals(3, size1.longValue());
        Long size2 = zSize("z122");
        Assertions.assertEquals(0, size2.longValue());

        Double newScore = zIncrement("z1", wangwu, 20);
        System.out.println(newScore);
    }

    @Test
    public void hTest() {
        reset("h1");

        hPut("h1", "name", "张三");

        Map<String, Object> map = new HashMap<>();
        map.put("age", 20);
        map.put("birthday", "2020-10-15 16:12:39");
        hPutAll("h1", map);

        hPutIfAbsent("h1", "job", "扫地");
        hPutIfAbsent("h1", "name", "新名字");

        Object get1 = hGet("h1", "birthday");
        System.out.println(get1);

        List<Object> valueList = hMultiGet("h1", Arrays.asList("name", "birthday", "job", "province"));
        System.out.println(valueList);

        Long size = hDel("h1", "job");
        Assertions.assertEquals(1, size.longValue());

        Boolean bool = hHasKey("h1", "job");
        Assertions.assertEquals(false, bool);

        Set<Object> fields = hKeys("h1");
        System.out.println(fields);
        System.out.println(fields.getClass().getName());

        List<Object> values = hValues("h1");
        System.out.println(values);
        System.out.println(values.getClass().getName());

        Map<Object, Object> map2 = hEntries("h1");
        System.out.println(map2);

        Long len = hLengthOfValue("h1", "name");
        System.out.println(len);

        Long size2 = hSize("h1");
        System.out.println(size2);

        Long newAge = hIncrement("h1", "age", 1);
        System.out.println(newAge);
    }

    @Test
    public void commonTest() {
        Set<String> keys = keys("m*");
        System.out.println(keys);

        Boolean abcdefg = hasKey("abcdefg");
        Assertions.assertEquals(false, abcdefg);

        set("test-expire", "test");
        Long time1 = getExpire("test-expire");
        Assertions.assertEquals(-1, time1.longValue());

        expire("test-expire", 60);
        Long time2 = getExpire("test-expire");
        System.out.println(time2);


        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        expireAt("test-expire", calendar.getTime());
        Long time3 = getExpire("test-expire");
        System.out.println(time3);

        Boolean del1 = del("m*");
        System.out.println(del1);

        Long delCount = delPattern("m*");
        System.out.println(delCount);
    }

    private void reset(String... keys) {
        del(Arrays.asList(keys));
    }
}
