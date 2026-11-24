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
package com.jastarwang.jefw.sms;

import com.jastarwang.jefw.sms.config.JefwSmsProperties;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private Map<SmsChannel, JefwSmsService> serviceRegistry = new HashMap<>();
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
        JefwSmsService service = serviceRegistry.get(channel);
        if (service == null) {
            throw new IllegalArgumentException("SMS channel " + channel + " is not available. Available channels: " +
                    serviceRegistry.keySet());
        }
        service.send(phones, templateOrId, params);
    }

    @Override
    public void afterPropertiesSet() {
        Set<SmsChannel> enabledChannels = jefwSmsProperties.getEnabledChannels();
        List<JefwSmsService> services = enabledChannels == null ? jefwSmsServices : jefwSmsServices.stream()
                .filter(service -> enabledChannels.contains(service.getChannel())).toList();
        Map<SmsChannel, JefwSmsService> servicesByChannel = new HashMap<>();
        for (JefwSmsService service : services) {
            SmsChannel channel = service.getChannel();
            if (servicesByChannel.putIfAbsent(channel, service) != null) {
                throw new IllegalStateException("Multiple JefwSmsService beans for channel " + channel +
                        ". Register only one service per channel.");
            }
        }
        if (services.isEmpty()) {
            if (enabledChannels != null) {
                throw new IllegalStateException("No available JefwSmsService for jefw.sms.enabled-channels=" +
                        enabledChannels + ". Check the Tencent SMS SDK or jefw.sms.dh3tong.api-url, or supply a service for the selected channels.");
            }
            throw new IllegalStateException("jefw.sms.enabled=true but no available JefwSmsService implementation was found. Please add a provider dependency or disable the SMS module.");
        }
        SmsChannel configuredDefault = jefwSmsProperties.getDefaultChannel();
        JefwSmsService selected = configuredDefault == null ? services.get(0) : servicesByChannel.get(configuredDefault);
        if (selected == null) {
            throw new IllegalStateException("jefw.sms.default-channel=" + configuredDefault +
                    " is not available. Current channels: " + servicesByChannel.keySet());
        }
        defaultChannel = selected.getChannel();
        defaultService = selected;
        serviceRegistry = servicesByChannel;
    }
}
