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
package com.jastarwang.jefw.sms.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teautil.Common;
import com.jastarwang.jefw.sms.JefwSmsException;
import com.jastarwang.jefw.sms.JefwSmsService;
import com.jastarwang.jefw.sms.SmsChannel;
import com.jastarwang.jefw.sms.config.JefwSmsProperties;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 短信服务实现之阿里云
 *
 * @author Jastar Wang
 * @since 2.5.1
 */
public class AliyunJefwSmsService implements JefwSmsService {

    private final JefwSmsProperties.Aliyun config;
    private final Client client;

    public AliyunJefwSmsService(JefwSmsProperties.Aliyun config, Client client) {
        this.config = config;
        this.client = client;
    }

    @Override
    public void init() {
        // do nothing
    }

    @Override
    public void destroy() {
        // do nothing
    }

    @Override
    public Client prototype() {
        return client;
    }

    @Override
    public SmsChannel getChannel() {
        return SmsChannel.ALIYUN;
    }

    @Override
    public void send(String phones, String templateOrId, LinkedHashMap<String, Object> params) {
        SendSmsRequest request = new SendSmsRequest().setPhoneNumbers(phones)
                .setSignName(config.getSignName()).setTemplateCode(templateOrId);
        SendSmsResponse response;
        try {
            if (params != null && !params.isEmpty()) {
                Map<String, String> variables = new LinkedHashMap<>();
                params.forEach((name, value) -> variables.put(name, Objects.toString(value, null)));
                request.setTemplateParam(Common.toJSONString(variables));
            }
            response = client.sendSms(request);
        } catch (Exception e) {
            throw new JefwSmsException("发送短信出错", e, params);
        }
        if (response == null || response.getBody() == null || !"OK".equals(response.getBody().getCode())) {
            throw new JefwSmsException("发送短信后对方响应异常", params, response, Arrays.asList(phones.split(",")));
        }
    }

}
