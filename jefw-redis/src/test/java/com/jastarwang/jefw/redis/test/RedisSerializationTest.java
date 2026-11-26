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

import com.jastarwang.jefw.redis.config.RedisAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Redis 序列化测试
 *
 * @author Jastar Wang
 * @since 2.5.1
 */
class RedisSerializationTest {
    @Test
    void readsExistingCacheValueWithTypeMetadata() {
        String json = """
                {"@class":"com.jastarwang.jefw.redis.test.Student","name":"Alice","age":18}
                """;
        var serializer = new GenericJackson2JsonRedisSerializer(new RedisAutoConfiguration().objectMapper());
        Student student = assertInstanceOf(Student.class, serializer.deserialize(json.getBytes(StandardCharsets.UTF_8)));
        assertEquals("Alice", student.getName());
        assertEquals(18L, student.getAge());
    }
}
