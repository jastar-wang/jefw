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
package com.jastarwang.jefw.weixin;

import com.jastarwang.jefw.weixin.model.BaseResponse;
import com.jastarwang.jefw.weixin.model.dev.Code2SessionResponse;
import com.jastarwang.jefw.weixin.model.dev.PhoneNumberResponse;
import com.jastarwang.jefw.weixin.model.dev.SubscribeMsgMpRequest;

/**
 * 微信开发小程序接口
 * <p>参考：<a href="https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/">服务端接口</a></p>
 *
 * @author Jastar Wang
 * @since 2.2.0
 */
public interface JefwWeixinDevMpService extends JefwWeixinDevService {
    /**
     * 获得临时登录凭证 code 后调用此接口完成登录
     *
     * @param appId     非空，AppID
     * @param appSecret 非空，AppSecret
     * @param jsCode    非空，登录时获取的 code，可通过wx.login获取
     * @return 会话对象
     */
    Code2SessionResponse code2Session(String appId, String appSecret, String jsCode);

    /**
     * 通过手机号获取凭证 code 换取用户手机号
     *
     * @param accessToken 非空，接口调用凭证
     * @param code        非空，手机号获取凭证
     * @return 手机号信息对象
     */
    PhoneNumberResponse getPhoneNumber(String accessToken, String code);

    /**
     * 发送一次性或长期订阅消息
     *
     * @param request 非空，请求参数
     * @return 请求响应
     */
    BaseResponse sendSubscribeMsg(SubscribeMsgMpRequest request);
}
