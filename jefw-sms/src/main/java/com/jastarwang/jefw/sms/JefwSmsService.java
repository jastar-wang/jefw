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

import java.util.LinkedHashMap;

/**
 * 短信服务接口
 *
 * @author Jastar Wang
 * @date 2023/4/5
 * @since 1.3.0
 */
public interface JefwSmsService {
    /**
     * 自定义初始化操作
     */
    void init();

    /**
     * 自定义销毁操作
     */
    void destroy();

    /**
     * 获取原始客户端
     *
     * @return 原始客户端对象，调用方自行转换类型，可能为空
     */
    Object prototype();

    /**
     * 获取短信渠道
     *
     * @return 实现类对应的短信渠道
     */
    SmsChannel getChannel();

    /**
     * 发送短信
     *
     * @param phones       非空，手机号，多个用英文逗号隔开。
     *                     <b>注意：请注意各个平台的最大数量限制</b>
     * @param templateOrId 非空，模板内容或模板ID
     * @param params       可空，参数，要与模板所需的数量一致。
     *                     <b>注意：为了兼容部分渠道(如腾讯云)要求参数顺序一致的问题，此处强制使用有序Map</b>
     */
    void send(String phones, String templateOrId, LinkedHashMap<String, Object> params);
}
