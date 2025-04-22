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
package com.jastarwang.jefw.sms.impl;

import cn.hutool.core.collection.CollUtil;
import com.jastarwang.jefw.sms.JefwSmsService;
import com.jastarwang.jefw.sms.SmsChannel;
import com.jastarwang.jefw.sms.JefwSmsException;
import com.jastarwang.jefw.sms.config.JefwSmsProperties;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public void send(String phones, String templateOrId, Map<String, Object> params) {
        try {
            SendSmsRequest req = new SendSmsRequest();
            // 单次最多200个
            req.setPhoneNumberSet(phones.split(","));
            req.setSmsSdkAppId(jefwSmsProperties.getTencent().getAppId());
            req.setSignName(jefwSmsProperties.getTencent().getSignName());
            req.setTemplateId(templateOrId);
            if (params != null && !params.isEmpty()) {
                List<String> values = params.values().stream().map(Object::toString).collect(Collectors.toList());
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
