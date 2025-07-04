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
package com.jastarwang.jefw.sms.test;

import com.jastarwang.jefw.sms.JefwSmsService;
import com.jastarwang.jefw.sms.JefwSmsTemplate;
import com.jastarwang.jefw.sms.SmsChannel;
import com.jastarwang.jefw.sms.config.JefwSmsProperties;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.LinkedHashMap;

/**
 * 短信服务单元测试
 *
 * @author Jastar Wang
 * @date 2023/4/5
 * @since 1.3.0
 */
@Disabled
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@SpringBootTest
@SpringBootConfiguration
public class JefwSmsServiceTest {

    @Resource
    private JefwSmsProperties properties;

    // =============== 方式1：自定义获取 =============== //
    @Resource
    @Qualifier("tencentJefwSmsService")
    private JefwSmsService tencentJefwSmsService;

    @Resource
    @Qualifier("dh3tongJefwSmsService")
    private JefwSmsService dh3tongJefwSmsService;

    // =============== 方式2：统一模板类 =============== //
    @Resource
    private JefwSmsTemplate smsTemplate;

    @Test
    public void contextLoads() {
        System.out.println(properties);
        Assertions.assertNotNull(properties, "属性配置对象未正常注入");
    }

    @Test
    public void getDefaultService() {
        System.out.println("腾讯云实现类：" + tencentJefwSmsService);
        System.out.println("大汉三通实现类：" + dh3tongJefwSmsService);
        System.out.println("默认的实现类：" + smsTemplate.getDefaultService());
    }

    @Test
    public void send() {
        String phones = "xxx";
        String template = "您的验证码为：{code}，{time}分钟内有效，若非本人操作请忽略！";
        String templateId = "xxx";
        // 为了短信内容直观区分，构建不同的参数
        LinkedHashMap<String, Object> params1 = new LinkedHashMap<>();
        params1.put("code", "123456");
        params1.put("time", 3);
        LinkedHashMap<String, Object> params2 = new LinkedHashMap<>();
        params2.put("code", "789012");
        params2.put("time", 5);
        try {
            // 腾讯云：使用模板id发送
            smsTemplate.send(phones, templateId, params1);

            // 大汉三通：使用模板内容发送；注意：可能会发送失败，因为对方设置了IP鉴权
            smsTemplate.getService(SmsChannel.DH3TONG).send(phones, template, params2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
