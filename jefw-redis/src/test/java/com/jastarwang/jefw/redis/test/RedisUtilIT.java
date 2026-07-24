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
package com.jastarwang.jefw.redis.test;

import com.jastarwang.jefw.lock.config.DistributedLockAutoConfiguration;
import com.jastarwang.jefw.redis.RedisUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static com.jastarwang.jefw.redis.RedisUtil.*;

/**
 * RedisUtil集成测试
 *
 * @author Jastar Wang
 * @date 2020/09/22
 * @since 1.0
 */
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration(exclude = DistributedLockAutoConfiguration.class) // 排除该自动配置，因为其依赖需在外部配置的Bean
@SpringBootTest
@SpringBootConfiguration // 必须加此注解单元测试才不报错
public class RedisUtilIT {

    private final Student zhangsan = new Student("张三", 28L);
    private final Student lisi = new Student("李四", 19L);
    private final Student wangwu = new Student("王五", 26L);
    private final Student zhaoliu = new Student("赵六", 22L);

    private final Teacher mrLi = new Teacher("李老师", 45, 1000L, new Date(), LocalDateTime.now());
    private final Teacher mrWang = new Teacher("王老师", 31, 1001L, new Date(), LocalDateTime.now());
    private final Teacher mrZh = new Teacher("张老师", 27, 1002L, new Date(), LocalDateTime.now());

    @Test
    public void contextLoads() {
        // done
    }

    @Test
    public void valueTest1() {
        clearAll();

        String utA = "ut:a";
        String utB = "ut:b";

        set(utA, 111);
        set(utB, new Student("张三", 28L), 60);
        try {
            // 错误的方式（某些场景下）
            Long a1 = (Long) get(utA);
        } catch (ClassCastException e) {
            System.out.println("you see you，出错了吧！");
        }
        // 正确的方式1
        Object a2 = get(utA);
        Long a = a2 == null ? null : Long.parseLong(a2.toString());
        // 正确的方式2
        Long a3 = get(utA, Long.class);
        Student b = get(utB, Student.class);

        Assertions.assertNotNull(a);
        Assertions.assertNotNull(b);
        Assertions.assertNotNull(a3);
        Assertions.assertEquals(111L, a.longValue());
        Assertions.assertEquals(111L, a3.longValue());
        System.out.println(b);
    }

    @Test
    public void valueTest2() {
        clearAll();

        String utA = "ut:a", utB = "ut:b", utC = "ut:c";
        String utM1 = "ut:m1", utM2 = "ut:2", utM3 = "ut:m3", utM4 = "ut:m4", utM5 = "ut:m5";

        // set,getAndSet
        set(utA, "张三");
        String oldValue = (String) getAndSet(utA, "李四");
        Assertions.assertEquals("张三", oldValue);

        // set and get List<Long>
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        set(utC, ids);
        List<Long> getIds = getList(utC, Long.class);
        System.out.println(getIds);

        // multiSet
        Map<String, Student> map = new HashMap<>();
        map.put(utM1, new Student("tony", 18L));
        map.put(utM2, new Student("liming", 19L));
        multiSet(map);

        // multiGet
        List<Object> objects = multiGet(Arrays.asList(utM1, utM2));
        Assertions.assertNotNull(objects);
        System.out.println(objects);

        // getMap
        set(utB, map);
        Map<String, Student> getMap = getMap(utB, String.class, Student.class);
        System.out.println(getMap);

        // increment
        increment(utM3);
        increment(utM4, 2L);
        increment(utM5, -1L);
        Assertions.assertEquals(1L, get(utM3, Long.class));
        Assertions.assertEquals(2L, get(utM4, Long.class));
        Assertions.assertEquals(-1L, get(utM5, Long.class));

        // size
        System.out.println(size(utM1));
    }

    @Test
    public void valueTest3() {
        clearAll();

        // ---------------------- value 场景 ---------------------- //
        String ut1 = "ut:date";
        String ut2 = "ut:local-date";
        String ut3 = "ut:local-time";
        String ut4 = "ut:local-date-time";
        String ut5 = "ut:clazz-with-time";
        String ut6 = "ut:clazzs-with-time";

        set(ut1, new Date());
        Date ut1Value = get(ut1, Date.class);
        System.out.println(ut1Value);

        set(ut2, LocalDate.now());
        LocalDate ut2Value = get(ut2, LocalDate.class);
        System.out.println(ut2Value);

        set(ut3, LocalTime.now());
        LocalTime ut3Value = get(ut3, LocalTime.class);
        System.out.println(ut3Value);

        set(ut4, LocalDateTime.now());
        LocalDateTime ut4Value = get(ut4, LocalDateTime.class);
        System.out.println(ut4Value);

        List<Teacher> teachers = new ArrayList<>(2);
        teachers.add(mrWang);
        teachers.add(mrZh);

        set(ut5, mrWang);
        Teacher ut5Value = get(ut5, Teacher.class);
        System.out.println(ut5Value);

        set(ut6, teachers);
        List<Teacher> list = getList(ut6, Teacher.class);
        System.out.println(list);
    }

    @Test
    public void valueTest4() {
        clearAll();

        // ---------------------- hash 场景 ---------------------- //
        String ut1 = "ut:h1";
        hPut(ut1, "master", mrLi);
        Teacher master = hGet(ut1, "master", Teacher.class);
        System.out.println(master);

        // ---------------------- set 场景 ---------------------- //
        String ut2 = "ut:s1";
        sAdd(ut2, mrLi);
        sAdd(ut2, mrWang);
        Set<Teacher> teacherSet = sMembers(ut2, Teacher.class);
        System.out.println(teacherSet);

        // ---------------------- list 场景 ---------------------- //
        String ut3 = "ut:l1";
        lRightPush(ut3, mrLi);
        lRightPush(ut3, mrZh);
        List<Teacher> teacherList = lRange(ut3, 0, -1, Teacher.class);
        System.out.println(teacherList);

        // ---------------------- zset 场景 ---------------------- //
        String ut4 = "ut:z1";
        zAdd(ut4, mrWang, 9);
        zAdd(ut4, mrZh, 1);
        Set<Teacher> teacherZSet = zRange(ut4, 0, -1, Teacher.class);
        System.out.println(teacherZSet);
        System.out.println("MrWang排名：" + zRank(ut4, mrWang));
        System.out.println("MrZh排名：" + zRank(ut4, mrZh));
    }

    @Test
    public void listTest1() {
        clearAll();

        String utL1 = "ut:l1", utUnExistKey = "ut:no-this-key";

        lRightPush(utL1, zhangsan);
        lRightPushAll(utL1, lisi, wangwu);
        Student l1Index0 = lIndex(utL1, 0, Student.class);
        Assertions.assertEquals("张三", l1Index0.getName());

        List<Object> list1 = lRange(utL1, 1, -1);
        List<Student> list2 = lRange(utL1, 1, -1, Student.class);
        System.out.println(list1);
        System.out.println(list2);

        System.out.println("验证key或index不存在的情况下：");
        System.out.println(lIndex(utUnExistKey, 0));
        System.out.println(lIndex(utL1, 10, Student.class));

        Assertions.assertThrows(Exception.class, () -> lSet(utL1, 6, zhaoliu));
    }

    @Test
    public void listTest2() {
        clearAll();

        String utL2 = "ut:l2", utUnExistKey = "ut:no-this-key";

        lRightPushAll(utL2, zhangsan, lisi, wangwu);
        // 测试key不存在的情况下返回null
        System.out.println(lLeftPop(utUnExistKey));
        // 正常场景
        System.out.println(lLeftPop(utL2));
        System.out.println(lLeftPop(utL2, Student.class));
    }

    @Test
    public void listTest3() {
        clearAll();

        String utL3 = "ut:l3";

        Long temp1 = lLeftPush(utL3, zhangsan);
        Long temp2 = lLeftPushAll(utL3, Arrays.asList(lisi, wangwu));
        Long temp3 = lLeftPushAll(utL3, zhaoliu, zhaoliu);
        Assertions.assertEquals(1, temp1.longValue());
        Assertions.assertEquals(3, temp2.longValue());
        Assertions.assertEquals(5, temp3.longValue());

        Long size = lSize(utL3);
        Assertions.assertEquals(5, size.longValue());

        lTrim(utL3, 1, 3);
    }

    @Test
    public void setTest() {
        clearAll();

        String utS1 = "ut:s1";

        Long size1 = sAdd(utS1, zhangsan, lisi);
        Assertions.assertEquals(2, size1.longValue());

        Long size2 = sAdd(utS1, wangwu);
        Assertions.assertEquals(1, size2.longValue());

        Long size3 = sAdd(utS1, wangwu);
        Assertions.assertEquals(0, size3.longValue());

        Set<Object> members1 = sMembers(utS1);
        System.out.println(members1);
        System.out.println(members1.getClass().getName());

        Set<Student> members2 = sMembers(utS1, Student.class);
        System.out.println(members2);
        System.out.println(members2.getClass().getName());

        Long size4 = sRemove(utS1, lisi, zhaoliu);
        Assertions.assertEquals(1, size4.longValue());

        Long size5 = sSize(utS1);
        Assertions.assertEquals(2, size5.longValue());

        Boolean isMember = sIsMember(utS1, wangwu);
        Assertions.assertEquals(true, isMember);
    }

    @Test
    public void zsetTest() {
        clearAll();

        String utZ1 = "ut:z1";

        Boolean bool1 = zAdd(utZ1, lisi, 11);
        Assertions.assertEquals(true, bool1);

        zAdd(utZ1, wangwu, 8.0);

        Double score = zScore(utZ1, zhaoliu);
        System.out.println(score);

        Long delCount = zRemove(utZ1, lisi);
        System.out.println(delCount);

        zAdd(utZ1, lisi, 12);
        zAdd(utZ1, zhaoliu, 6);
        Long count = zCount(utZ1, 4, 6);
        Assertions.assertEquals(1, count.longValue());

        Set<Object> set1 = zRange(utZ1, 1, 2);
        System.out.println(set1);

        Set<Student> set2 = zRange(utZ1, 1, 2, Student.class);
        System.out.println(set2);

        Long index = zRank(utZ1, wangwu);
        Assertions.assertEquals(1, index.longValue());

        Long size1 = zSize(utZ1);
        Assertions.assertEquals(3, size1.longValue());
        Long size2 = zSize("z122");
        Assertions.assertEquals(0, size2.longValue());

        Double newScore = zIncrement(utZ1, wangwu, 20);
        System.out.println(newScore);
    }

    @Test
    public void hTest() {
        clearAll();

        String utH1 = "ut:h1";

        hPut(utH1, "name", "张三");

        Map<String, Object> map = new HashMap<>();
        map.put("age", 20);
        map.put("birthday", "2020-10-15 16:12:39");
        hPutAll(utH1, map);

        hPutIfAbsent(utH1, "job", "扫地");
        hPutIfAbsent(utH1, "name", "新名字");

        Object get1 = hGet(utH1, "birthday");
        System.out.println(get1);

        List<Object> valueList = hMultiGet(utH1, Arrays.asList("name", "birthday", "job", "province"));
        System.out.println(valueList);

        Long size = hDel(utH1, "job");
        Assertions.assertEquals(1, size.longValue());

        Boolean bool = hHasKey(utH1, "job");
        Assertions.assertEquals(false, bool);

        Set<Object> fields = hKeys(utH1);
        System.out.println(fields);
        System.out.println(fields.getClass().getName());

        List<Object> values = hValues(utH1);
        System.out.println(values);
        System.out.println(values.getClass().getName());

        Map<Object, Object> map2 = hEntries(utH1);
        System.out.println(map2);

        // HSTRLEN since redis 3.0.6
        Long len = hLengthOfValue(utH1, "name");
        System.out.println(len);

        Long size2 = hSize(utH1);
        System.out.println(size2);

        Long newAge = hIncrement(utH1, "age", 1);
        System.out.println(newAge);
    }

    @Test
    public void commonTest1() {
        String ut1 = "ut*", ut2 = "ut:abcdefg", ut3 = "ut:test-expire";

        Set<String> keys = keys(ut1);
        System.out.println(keys);

        Boolean abcdefg = hasKey(ut2);
        Assertions.assertEquals(false, abcdefg);

        set(ut3, "test");
        Long time1 = getExpire(ut3);
        Assertions.assertEquals(-1, time1.longValue());

        expire(ut3, 60);
        Long time2 = getExpire(ut3);
        System.out.println(time2);


        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        expireAt(ut3, calendar.getTime());
        Long time3 = getExpire(ut3);
        System.out.println(time3);

        Boolean del1 = del(ut1);
        System.out.println(del1);

        Long delCount = delPattern(ut1);
        System.out.println(delCount);
    }

    @Test
    public void commonTest2() throws InterruptedException {
        clearAll();

        Long value = incrementAndExpire("test", 30);
        System.out.println("第1次自增：" + value);

        Thread.sleep(5000);

        value = RedisUtil.incrementAndExpire("test", 30);
        System.out.println("第2次自增：" + value);
    }

    @Test
    public void clearOnAllFinished() {
        clearAll();
    }

    private void clearAll() {
        delPattern("ut:*");
    }

}
