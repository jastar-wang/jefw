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
import com.jastarwang.jefw.weixin.model.dev.OAuth2AccessTokenResponse;
import com.jastarwang.jefw.weixin.model.dev.OnceSubscribeMsgOaRequest;
import com.jastarwang.jefw.weixin.model.dev.UserInfoResponse;

/**
 * 微信开发公众平台接口
 * <p>参考：<a href="https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/Wechat_webpage_authorization.html">网页授权</a></p>
 *
 * @author Jastar Wang
 * @date 2025/4/9
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
