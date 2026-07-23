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

import cn.hutool.core.collection.CollUtil;
import com.jastarwang.jefw.sms.JefwSmsException;
import com.jastarwang.jefw.sms.JefwSmsService;
import com.jastarwang.jefw.sms.SmsChannel;
import com.jastarwang.jefw.sms.config.JefwSmsProperties;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 短信服务实现之腾讯云
 *
 * @author Jastar Wang
 * @date 2023/4/5
 * @since 1.3.0
 */
public class TencentJefwSmsService implements JefwSmsService {

    private final JefwSmsProperties jefwSmsProperties;
    private final SmsClient tencentSmsClient;

    public TencentJefwSmsService(JefwSmsProperties jefwSmsProperties, SmsClient tencentSmsClient) {
        this.jefwSmsProperties = jefwSmsProperties;
        this.tencentSmsClient = tencentSmsClient;
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
    public Object prototype() {
        return tencentSmsClient;
    }

    @Override
    public SmsChannel getChannel() {
        return SmsChannel.TENCENT;
    }

    @Override
    public void send(String phones, String templateOrId, LinkedHashMap<String, Object> params) {
        try {
            SendSmsRequest req = new SendSmsRequest();
            // 单次最多200个
            req.setPhoneNumberSet(phones.split(","));
            req.setSmsSdkAppId(jefwSmsProperties.getTencent().getAppId());
            req.setSignName(jefwSmsProperties.getTencent().getSignName());
            req.setTemplateId(templateOrId);
            if (params != null && !params.isEmpty()) {
                List<String> values = params.values().stream().map(Object::toString).toList();
                req.setTemplateParamSet(values.toArray(new String[]{}));
            }
            // 发送
            SendSmsResponse resp = tencentSmsClient.SendSms(req);
            List<String> failedPhones = new ArrayList<>();
            for (SendStatus status : resp.getSendStatusSet()) {
                if (!"ok".equalsIgnoreCase(status.getCode())) {
                    failedPhones.add(status.getPhoneNumber());
                }
            }
            if (CollUtil.isNotEmpty(failedPhones)) {
                throw new JefwSmsException("发送短信后对方响应异常", params, resp, failedPhones);
            }
        } catch (TencentCloudSDKException e) {
            throw new JefwSmsException("发送短信出错", e, params);
        }
    }
}
