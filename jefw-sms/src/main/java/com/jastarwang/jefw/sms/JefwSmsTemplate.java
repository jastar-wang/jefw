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
package com.jastarwang.jefw.sms;

import com.jastarwang.jefw.sms.config.JefwSmsProperties;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 短信服务操作模板类
 *
 * @author Jastar Wang
 * @date 2023/4/5
 * @since 1.3.0
 */
public class JefwSmsTemplate implements InitializingBean {
    /**
     * 服务顶级容器
     */
    private final Map<SmsChannel, JefwSmsService> serviceRegistry = new HashMap<>();
    /**
     * 短信服务配置
     */
    private final JefwSmsProperties jefwSmsProperties;
    /**
     * 所有短信服务
     */
    private final List<JefwSmsService> jefwSmsServices;
    /**
     * 默认短信服务
     */
    private JefwSmsService defaultService;
    /**
     * 默认短信渠道
     */
    private SmsChannel defaultChannel;

    public JefwSmsTemplate(JefwSmsProperties jefwSmsProperties, List<JefwSmsService> jefwSmsServices) {
        this.jefwSmsProperties = jefwSmsProperties;
        this.jefwSmsServices = jefwSmsServices;
    }

    /**
     * 获取短信配置
     *
     * @return 短信配置信息
     */
    public JefwSmsProperties getProperties() {
        return jefwSmsProperties;
    }

    /**
     * 根据指定渠道获取短信服务
     *
     * @param channel 指定渠道
     * @return 对应渠道的短信服务
     */
    public JefwSmsService getService(SmsChannel channel) {
        return serviceRegistry.get(channel);
    }

    /**
     * 获取默认的短信服务
     *
     * @return 默认的短信服务
     */
    public JefwSmsService getDefaultService() {
        return defaultService;
    }

    /**
     * 获取默认的短信渠道
     *
     * @return 默认的短信渠道
     */
    public SmsChannel getDefaultChannel() {
        return defaultChannel;
    }

    /**
     * 发送短信（使用默认渠道）
     *
     * @param phones       非空，手机号，多个用英文逗号隔开。
     *                     <b>注意：请注意各个平台的最大数量限制</b>
     * @param templateOrId 非空，模板内容或模板ID
     * @param params       可空，参数，要与模板所需的数量一致。
     *                     <b>注意：为了兼容部分渠道(如腾讯云)要求参数顺序一致的问题，此处强制使用有序Map</b>
     */
    public void send(String phones, String templateOrId, LinkedHashMap<String, Object> params) {
        send(defaultService, phones, templateOrId, params);
    }

    /**
     * 发送短信
     *
     * @param service      非空，自主选择短信发送渠道
     * @param phones       非空，手机号，多个用英文逗号隔开。
     *                     <b>注意：请注意各个平台的最大数量限制</b>
     * @param templateOrId 非空，模板内容或模板ID
     * @param params       可空，参数，要与模板所需的数量一致。
     *                     <b>注意：为了兼容部分渠道(如腾讯云)要求参数顺序一致的问题，此处强制使用有序Map</b>
     */
    public void send(JefwSmsService service, String phones, String templateOrId, LinkedHashMap<String, Object> params) {
        service.send(phones, templateOrId, params);
    }

    /**
     * 发送短信
     *
     * @param channel      非空，自主选择短信发送渠道
     * @param phones       非空，手机号，多个用英文逗号隔开。
     *                     <b>注意：请注意各个平台的最大数量限制</b>
     * @param templateOrId 非空，模板内容或模板ID
     * @param params       可空，参数，要与模板所需的数量一致。
     *                     <b>注意：为了兼容部分渠道(如腾讯云)要求参数顺序一致的问题，此处强制使用有序Map</b>
     */
    public void send(SmsChannel channel, String phones, String templateOrId, LinkedHashMap<String, Object> params) {
        serviceRegistry.get(channel).send(phones, templateOrId, params);
    }

    @Override
    public void afterPropertiesSet() {
        if (jefwSmsServices.isEmpty()) {
            throw new IllegalStateException("jefw.sms.enabled=true but no available JefwSmsService implementation was found. Please add a provider dependency or disable the SMS module.");
        }
        for (JefwSmsService jefwSmsService : jefwSmsServices) {
            serviceRegistry.put(jefwSmsService.getChannel(), jefwSmsService);
        }
        if (jefwSmsProperties.getDefaultChannel() != null) {
            defaultService = serviceRegistry.get(jefwSmsProperties.getDefaultChannel());
            if (defaultService == null) {
                throw new IllegalStateException("jefw.sms.default-channel=" + jefwSmsProperties.getDefaultChannel() +
                        " is not available. Current channels: " + serviceRegistry.keySet());
            }
        } else {
            defaultService = jefwSmsServices.get(0);
        }
        defaultChannel = defaultService.getChannel();
    }
}
