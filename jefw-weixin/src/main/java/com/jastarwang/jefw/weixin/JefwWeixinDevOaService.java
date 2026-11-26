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
import com.jastarwang.jefw.weixin.model.dev.OAuth2AccessTokenResponse;
import com.jastarwang.jefw.weixin.model.dev.OnceSubscribeMsgOaRequest;
import com.jastarwang.jefw.weixin.model.dev.UserInfoResponse;

/**
 * 微信开发公众平台接口
 * <p>参考：<a href="https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/Wechat_webpage_authorization.html">网页授权</a></p>
 *
 * @author Jastar Wang
 * @since 2.2.0
 */
public interface JefwWeixinDevOaService extends JefwWeixinDevService {
    /**
     * 通过code换取网页授权access_token
     *
     * @param appId     非空，AppID
     * @param appSecret 非空，AppSecret
     * @param code      非空，网页授权后获取的code
     * @return 网页授权接口调用凭证对象（内部异常时错误码为-2，不会抛出异常）
     */
    OAuth2AccessTokenResponse getOAuth2AccessToken(String appId, String appSecret, String code);

    /**
     * 获取用户信息(需scope为 snsapi_userinfo)
     *
     * @param oauth2AccessToken 非空，网页授权接口调用凭证
     * @param openid            非空，用户的唯一标识
     * @param lang              可空，返回国家地区语言版本，zh_CN 简体（默认），zh_TW 繁体，en 英语
     * @return 用户信息对象（内部异常时错误码为-2，不会抛出异常）
     */
    UserInfoResponse getUserInfo(String oauth2AccessToken, String openid, String lang);

    /**
     * 发送一次性订阅消息
     *
     * @param request 非空，请求参数
     * @return 请求响应（内部异常时错误码为-2，不会抛出异常）
     */
    BaseResponse sendOnceSubscribeMsg(OnceSubscribeMsgOaRequest request);
}
