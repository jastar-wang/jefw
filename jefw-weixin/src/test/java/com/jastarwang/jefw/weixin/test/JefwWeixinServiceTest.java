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
package com.jastarwang.jefw.weixin.test;

import com.jastarwang.jefw.weixin.JefwWeixinDevMpService;
import com.jastarwang.jefw.weixin.JefwWeixinDevOaService;
import com.jastarwang.jefw.weixin.JefwWeixinPayJsapiService;
import com.jastarwang.jefw.weixin.JefwWeixinPayMpService;
import com.jastarwang.jefw.weixin.config.JefwWeixinProperties;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * 微信服务单元测试
 *
 * @author Jastar Wang
 * @date 2025/7/4
 * @since 2.2.0
 */
@Disabled
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@SpringBootTest
@SpringBootConfiguration
public class JefwWeixinServiceTest {

    @Resource
    private JefwWeixinProperties properties;
    @Resource
    private JefwWeixinPayJsapiService jefwWeixinPayJsapiService;
    @Resource
    private JefwWeixinPayMpService jefwWeixinPayMpService;
    @Resource
    private JefwWeixinDevOaService jefwWeixinDevOaService;
    @Resource
    private JefwWeixinDevMpService jefwWeixinDevMpService;

    // 注意：测试前需要在 resource 目录下放置 pem 支付证书文件，并在 yml 中配置正确的信息

    @Test
    public void contextLoads() {
        System.out.println(properties);
        Assertions.assertNotNull(properties, "属性配置对象未正常注入");
    }

}
