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
package com.jastarwang.jefw.util.test.jwt;

import com.jastarwang.jefw.util.extra.jwt.JwtUtil;
import io.jsonwebtoken.JwtBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JwtUtil单元测试
 *
 * @author Jastar Wang
 * @date 2020/8/12
 * @since 1.0
 */
@EnableAutoConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
// @EnableAutoConfiguration可以代替@SpringBootTest以提供bean映射的支持
// @SpringBootTest(classes = {PropertyAutoConfiguration.class, JwtAutoConfiguration.class})
public class JwtUtilTest {

    @Test
    public void builder() {
        JwtBuilder builder = JwtUtil.builder();
        Assert.assertNotNull(builder);
    }

    @Test
    public void buildToken() {
        String token = JwtUtil.buildToken("张全蛋");
        Assert.assertNotNull(token);
    }

    @Test
    public void parserSubject() {
        String token = JwtUtil.buildToken("张全蛋");
        String subject = JwtUtil.parseSubject(token);
        Assert.assertEquals("张全蛋", subject);
    }

    @Test
    public void isExpired() {
        String token = JwtUtil.buildToken("张全蛋");
        Boolean expired = JwtUtil.isExpired(token);
        Assert.assertEquals(false, expired);
        Boolean nullExpired = JwtUtil.isExpired(token, "abcdefghijklmn");
        Assert.assertNull(nullExpired);
    }

}
