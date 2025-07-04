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
 * @date 2025/4/9
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
