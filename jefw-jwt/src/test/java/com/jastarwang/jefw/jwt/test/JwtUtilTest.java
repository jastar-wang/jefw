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
package com.jastarwang.jefw.jwt.test;

import com.jastarwang.jefw.jwt.JwtUtil;
import io.jsonwebtoken.JwtBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * JwtUtil单元测试
 *
 * @author Jastar Wang
 * @date 2020/8/12
 * @since 1.0
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@SpringBootConfiguration
@EnableAutoConfiguration
public class JwtUtilTest {

    @Test
    public void builder() {
        JwtBuilder builder = JwtUtil.builder();
        Assertions.assertNotNull(builder);
    }

    @Test
    public void buildToken() {
        String token = JwtUtil.buildToken("张全蛋");
        Assertions.assertNotNull(token);
    }

    @Test
    public void parserSubject() {
        String token = JwtUtil.buildToken("张全蛋");
        String subject = JwtUtil.parseSubject(token);
        Assertions.assertEquals("张全蛋", subject);
    }

    @Test
    public void isExpired() {
        String token = JwtUtil.buildToken("张全蛋");
        Boolean expired = JwtUtil.isExpired(token);
        Assertions.assertEquals(false, expired);
        Boolean nullExpired = JwtUtil.isExpired(token, "abcdefghijklmn");
        Assertions.assertNull(nullExpired);
    }

}
