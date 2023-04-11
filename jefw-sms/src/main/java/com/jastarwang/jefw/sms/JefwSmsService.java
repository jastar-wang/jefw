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

import java.util.Map;

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
     *                     <b>注意：腾讯云渠道下请使用有序Map（如：LinkedHashMap），以保证同模板参数的顺序一致</b>
     */
    void send(String phones, String templateOrId, Map<String, Object> params);
}
