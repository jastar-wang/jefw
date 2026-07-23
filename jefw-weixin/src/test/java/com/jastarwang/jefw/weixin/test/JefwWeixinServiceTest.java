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
