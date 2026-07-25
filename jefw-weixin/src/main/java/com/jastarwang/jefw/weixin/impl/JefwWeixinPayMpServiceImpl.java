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
