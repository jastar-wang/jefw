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
package com.jastarwang.jefw.weixin.impl;

import com.jastarwang.jefw.weixin.JefwWeixinPayMpService;
import com.jastarwang.jefw.weixin.WeixinPayChannel;
import com.jastarwang.jefw.weixin.config.JefwWeixinProperties;
import com.wechat.pay.java.core.Config;

/**
 * 微信支付JSAPI接口实现类
 *
 * @author Jastar Wang
 * @date 2025/4/9
 * @since 2.2.0
 */
public class JefwWeixinPayMpServiceImpl extends JefwWeixinPayJsapiServiceImpl implements JefwWeixinPayMpService {

    public JefwWeixinPayMpServiceImpl(Config config, JefwWeixinProperties jefwWeixinProperties) {
        super(config, jefwWeixinProperties);
    }

    @Override
    public WeixinPayChannel getChannel() {
        return WeixinPayChannel.MINI_PROGRAM;
    }

    @Override
    protected String getAppId() {
        return jefwWeixinProperties.getMp().getAppId();
    }

}
