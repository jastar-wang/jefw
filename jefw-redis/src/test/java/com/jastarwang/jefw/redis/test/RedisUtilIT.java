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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static com.jastarwang.jefw.redis.RedisUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 使用真实 Redis 验证缓存读写、类型还原和键的生命周期。
 * 设置环境变量 {@code JEFW_REDIS_IT=true} 后才启动测试上下文。
 *
 * @author Jastar Wang
 * @date 2020/09/22
 * @since 1.0
 */
@EnabledIfEnvironmentVariable(named = "JEFW_REDIS_IT", matches = "true")
@SpringBootTest(classes = RedisUtilIT.class)
@SpringBootConfiguration
@EnableAutoConfiguration(exclude = DistributedLockAutoConfiguration.class)
class RedisUtilIT {

    private final String prefix = "jefw:it:" + UUID.randomUUID() + ":";
    private final List<String> testKeys = new ArrayList<>();

    private String key(String name) {
        String key = prefix + name;
        testKeys.add(key);
        return key;
    }

    /**
     * 仅删除本用例登记的键，断言失败时也会执行。
     */
    @AfterEach
    void deleteTestKeys() {
        del(testKeys);
    }

    /**
     * 验证数值转换、原子替换和多键读取的顺序。
     */
    @Test
    void readsTypedValuesAndPreservesMultiGetOrder() {
        String first = key("first");
        String second = key("second");
        set(first, 111);
        assertThat(get(first, Long.class)).isEqualTo(111L);
        assertThat(getAndSet(first, "new")).isEqualTo(111);
        assertThat(get(first)).isEqualTo("new");
        Student student = new Student("张三", 28L);
        set(second, student, 60);
        assertThat(get(second, Student.class)).usingRecursiveComparison().isEqualTo(student);
        assertThat(getExpire(second)).isBetween(1L, 60L);

        multiSet(Map.of(first, "甲", second, "乙"));
        assertThat(multiGet(List.of(second, first))).containsExactly("乙", "甲");
        assertThat(size(first)).isPositive();
        List<Long> ids = new ArrayList<>(List.of(1L, 2L));
        set(first, ids);
        assertThat(getList(first, Long.class)).containsExactlyElementsOf(ids);
        Map<String, Student> students = new HashMap<>();
        students.put("班长", student);
        set(second, students);
        assertThat(getMap(second, String.class, Student.class).get("班长"))
                .usingRecursiveComparison().isEqualTo(student);
    }

    /**
     * 使用固定的毫秒精度时间，验证时间值以及嵌套对象的序列化。
     */
    @Test
    void roundTripsTemporalValuesAndObjects() {
        Date hired = Date.from(Instant.parse("2020-10-01T09:10:11Z"));
        LocalDateTime birth = LocalDateTime.parse("1990-01-02T03:04:05.678");
        Teacher teacher = new Teacher("李老师", 30, 1001L, hired, birth);
        String value = key("value");
        set(value, hired);
        assertThat(get(value, Date.class)).isEqualTo(hired);
        set(value, birth.toLocalDate());
        assertThat(get(value, LocalDate.class)).isEqualTo(birth.toLocalDate());
        set(value, birth.toLocalTime());
        assertThat(get(value, LocalTime.class)).isEqualTo(birth.toLocalTime());
        set(value, birth);
        assertThat(get(value, LocalDateTime.class)).isEqualTo(birth);
        set(value, teacher);
        assertThat(get(value, Teacher.class)).usingRecursiveComparison().isEqualTo(teacher);
        set(value, new ArrayList<>(List.of(teacher)));
        assertThat(getList(value, Teacher.class)).singleElement().usingRecursiveComparison().isEqualTo(teacher);

        String hash = key("hash");
        hPut(hash, "teacher", teacher);
        assertThat(hGet(hash, "teacher", Teacher.class)).usingRecursiveComparison().isEqualTo(teacher);
        String members = key("set");
        sAdd(members, teacher);
        assertThat(sMembers(members, Teacher.class)).singleElement().usingRecursiveComparison().isEqualTo(teacher);
        String list = key("list");
        lRightPush(list, teacher);
        assertThat(lRange(list, 0, -1, Teacher.class)).singleElement().usingRecursiveComparison().isEqualTo(teacher);
        String ranking = key("ranking");
        zAdd(ranking, teacher, 9);
        assertThat(zRange(ranking, 0, -1, Teacher.class)).singleElement().usingRecursiveComparison().isEqualTo(teacher);
    }

    /**
     * 验证列表两端入队、按索引访问、裁剪和越界处理。
     */
    @Test
    void preservesListOrderAcrossPushTrimAndPop() {
        String list = key("list");
        assertThat(lLeftPop(list)).isNull();
        assertThat(lIndex(list, 0)).isNull();
        assertThat(lRightPush(list, "甲")).isEqualTo(1L);
        assertThat(lRightPushAll(list, "乙", "丙")).isEqualTo(3L);
        assertThat(lLeftPush(list, "丁")).isEqualTo(4L);
        assertThat(lLeftPushAll(list, List.<Object>of("戊", "己"))).isEqualTo(6L);
        assertThat(lLeftPushAll(list, "庚", "辛")).isEqualTo(8L);
        assertThat(lSize(list)).isEqualTo(8L);
        assertThat(lIndex(list, 2, String.class)).isEqualTo("己");
        assertThat(lIndex(list, 8, String.class)).isNull();
        assertThatThrownBy(() -> lSet(list, 8, "壬")).isInstanceOf(DataAccessException.class);
        lTrim(list, 4, 6);
        assertThat(lRange(list, 0, -1)).containsExactly("丁", "甲", "乙");
        assertThat(lLeftPop(list, String.class)).isEqualTo("丁");
        assertThat(lLeftPop(list)).isEqualTo("甲");
    }

    /**
     * 验证集合去重和删除时返回的实际成员数。
     */
    @Test
    void countsOnlyChangedSetMembers() {
        String set = key("set");
        assertThat(sAdd(set, "甲", "乙")).isEqualTo(2L);
        assertThat(sAdd(set, "甲")).isZero();
        assertThat(sMembers(set)).containsExactlyInAnyOrder("甲", "乙");
        assertThat(sRemove(set, "乙", "丙")).isEqualTo(1L);
        assertThat(sSize(set)).isEqualTo(1L);
        assertThat(sIsMember(set, "甲")).isTrue();
        assertThat(sIsMember(set, "乙")).isFalse();
    }

    /**
     * 验证有序集合的分值区间、排名和更新后的顺序。
     */
    @Test
    void updatesSortedSetScoresAndRanks() {
        String ranking = key("ranking");
        assertThat(zSize(ranking)).isZero();
        assertThat(zScore(ranking, "甲")).isNull();
        assertThat(zAdd(ranking, "甲", 11)).isTrue();
        zAdd(ranking, "乙", 8);
        zAdd(ranking, "丙", 6);
        assertThat(zCount(ranking, 6, 8)).isEqualTo(2L);
        assertThat(zRange(ranking, 1, 2)).containsExactly("乙", "甲");
        assertThat(zRank(ranking, "乙")).isEqualTo(1L);
        assertThat(zIncrement(ranking, "乙", 20)).isEqualTo(28.0);
        assertThat(zRange(ranking, 0, -1, String.class)).containsExactly("丙", "甲", "乙");
        assertThat(zRemove(ranking, "甲")).isEqualTo(1L);
        assertThat(zSize(ranking)).isEqualTo(2L);
    }

    /**
     * 验证哈希字段的条件写入、批量读取、递增和删除。
     */
    @Test
    void maintainsHashFieldsAcrossUpdates() {
        String hash = key("hash");
        hPut(hash, "name", "张三");
        hPutAll(hash, Map.of("age", 20));
        hPutIfAbsent(hash, "job", "教师");
        hPutIfAbsent(hash, "name", "新名字");
        assertThat(hGet(hash, "name")).isEqualTo("张三");
        assertThat(hMultiGet(hash, List.of("job", "name", "missing"))).containsExactly("教师", "张三", null);
        assertThat(hDel(hash, "job")).isEqualTo(1L);
        assertThat(hHasKey(hash, "job")).isFalse();
        assertThat(hLengthOfValue(hash, "name")).isPositive();
        assertThat(hSize(hash)).isEqualTo(2L);
        assertThat(hIncrement(hash, "age", 1)).isEqualTo(21L);
        assertThat(hKeys(hash)).containsExactlyInAnyOrder("age", "name");
        assertThat(hValues(hash)).containsExactlyInAnyOrder("张三", 21);
        assertThat(hEntries(hash)).containsEntry("name", "张三").containsEntry("age", 21);
    }

    /**
     * 验证相对过期、绝对过期时间和单键删除。
     */
    @Test
    void updatesExpiryAndDeletesExactKey() {
        String value = key("value");
        assertThat(hasKey(value)).isFalse();
        set(value, "测试");
        assertThat(keys(value)).containsExactly(value);
        assertThat(getExpire(value)).isEqualTo(-1L);
        assertThat(expire(value, 120)).isTrue();
        assertThat(getExpire(value)).isBetween(1L, 120L);
        assertThat(expireAt(value, Date.from(Instant.now().plusSeconds(600)))).isTrue();
        assertThat(getExpire(value)).isBetween(121L, 601L);
        assertThat(del(value)).isTrue();
        assertThat(hasKey(value)).isFalse();
    }

    /**
     * 使用不同的过期参数再次自增，确认只有首次写入设置过期时间。
     */
    @Test
    void keepsInitialExpiryWhenIncrementingCounter() {
        String counter = key("counter");
        assertThat(incrementAndExpire(counter, 120)).isEqualTo(1L);
        assertThat(incrementAndExpire(counter, 3600)).isEqualTo(2L);
        assertThat(getExpire(counter)).isBetween(1L, 120L);
        assertThat(increment(counter)).isEqualTo(3L);
        assertThat(increment(counter, -5L)).isEqualTo(-2L);
        assertThat(get(counter, Long.class)).isEqualTo(-2L);
    }
}
