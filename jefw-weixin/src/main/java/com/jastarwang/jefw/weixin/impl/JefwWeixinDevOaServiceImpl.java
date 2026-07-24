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

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jastarwang.jefw.weixin.JefwWeixinDevOaService;
import com.jastarwang.jefw.weixin.WeixinDevChannel;
import com.jastarwang.jefw.weixin.model.BaseResponse;
import com.jastarwang.jefw.weixin.model.dev.OAuth2AccessTokenResponse;
import com.jastarwang.jefw.weixin.model.dev.OnceSubscribeMsgOaRequest;
import com.jastarwang.jefw.weixin.model.dev.UserInfoResponse;

/**
 * 微信开发公众平台接口实现
 *
 * @author Jastar Wang
 * @date 2025/4/9
 * @since 2.2.0
 */
public class JefwWeixinDevOaServiceImpl extends JefwWeixinDevServiceImpl implements JefwWeixinDevOaService {

    @Override
    public WeixinDevChannel getChannel() {
        return WeixinDevChannel.OFFICIAL_ACCOUNT;
    }

    @Override
    public OAuth2AccessTokenResponse getOAuth2AccessToken(String appId, String appSecret, String code) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid={}&secret={}&code={}&grant_type=authorization_code";
        url = StrUtil.format(url, appId, appSecret, code);
        try {
            String resBody = HttpUtil.get(url);
            return JSONUtil.toBean(resBody, OAuth2AccessTokenResponse.class);
        } catch (Exception e) {
            OAuth2AccessTokenResponse response = new OAuth2AccessTokenResponse();
            response.setErrcode(INTERNAL_ERROR_CODE);
            response.setErrmsg("请求获取网页授权access_token异常：" + e.getMessage());
            return response;
        }
    }

    @Override
    public UserInfoResponse getUserInfo(String oauth2AccessToken, String openid, String lang) {
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token={}&openid={}&lang={}";
        url = StrUtil.format(url, oauth2AccessToken, openid, StrUtil.blankToDefault(lang, "zh_CN"));
        try {
            String resBody = HttpUtil.get(url);
            return JSONUtil.toBean(resBody, UserInfoResponse.class);
        } catch (Exception e) {
            UserInfoResponse response = new UserInfoResponse();
            response.setErrcode(INTERNAL_ERROR_CODE);
            response.setErrmsg("请求获取用户信息异常：" + e.getMessage());
            return response;
        }
    }

    @Override
    public BaseResponse sendOnceSubscribeMsg(OnceSubscribeMsgOaRequest request) {
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/subscribe?access_token={}";
        url = StrUtil.format(url, request.getAccessToken());
        JSONObject reqBody = new JSONObject();
        reqBody.set("touser", request.getToUserOpenid());
        reqBody.set("template_id", request.getTemplateId());
        if (StrUtil.isNotBlank(request.getJumpUrl())) {
            reqBody.set("url", request.getJumpUrl());
        }
        if (StrUtil.isAllNotBlank(request.getJumpMpAppId(), request.getJumpMpPagePath())) {
            JSONObject miniprogram = new JSONObject();
            miniprogram.set("appid", request.getJumpMpAppId());
            miniprogram.set("pagepath", request.getJumpMpPagePath());
            reqBody.set("miniprogram", miniprogram);
        }
        reqBody.set("scene", request.getScene());
        reqBody.set("title", request.getTitle());
        JSONObject content = new JSONObject();
        content.set("value", request.getContent());
        content.set("color", request.getContentColor());
        JSONObject data = new JSONObject();
        data.set("content", content);
        reqBody.set("data", data);
        try {
            String resBody = HttpUtil.post(url, reqBody.toString());
            return JSONUtil.toBean(resBody, BaseResponse.class);
        } catch (Exception e) {
            BaseResponse response = new BaseResponse();
            response.setErrcode(INTERNAL_ERROR_CODE);
            response.setErrmsg("发送一次性订阅消息时异常：" + e.getMessage());
            return response;
        }
    }

}
