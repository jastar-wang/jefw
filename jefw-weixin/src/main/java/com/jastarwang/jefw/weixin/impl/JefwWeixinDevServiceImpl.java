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

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jastarwang.jefw.weixin.JefwWeixinDevService;
import com.jastarwang.jefw.weixin.model.dev.AccessTokenResponse;

/**
 * 微信开发通用接口实现基类
 *
 * @author Jastar Wang
 * @date 2025/4/9
 * @since 2.2.0
 */
public abstract class JefwWeixinDevServiceImpl implements JefwWeixinDevService {

    protected static final int INTERNAL_ERROR_CODE = -2;

    @Override
    public AccessTokenResponse getAccessToken(String appId, String appSecret) {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={}&secret={}";
        url = StrUtil.format(url, appId, appSecret);
        try {
            String resBody = HttpUtil.get(url);
            return JSONUtil.toBean(resBody, AccessTokenResponse.class);
        } catch (Throwable e) {
            AccessTokenResponse response = new AccessTokenResponse();
            response.setErrcode(INTERNAL_ERROR_CODE);
            response.setErrmsg("请求获取access_token异常：" + e.getMessage());
            return response;
        }
    }

    @Override
    public AccessTokenResponse getStableAccessToken(String appId, String appSecret, Boolean forceRefresh) {
        String url = "https://api.weixin.qq.com/cgi-bin/stable_token";
        JSONObject reqBody = new JSONObject();
        reqBody.set("appid", appId);
        reqBody.set("secret", appSecret);
        reqBody.set("grant_type", "client_credential");
        if (forceRefresh != null) {
            reqBody.set("force_refresh", forceRefresh);
        }
        try {
            String resBody = HttpUtil.post(url, reqBody.toString());
            return JSONUtil.toBean(resBody, AccessTokenResponse.class);
        } catch (Throwable e) {
            AccessTokenResponse response = new AccessTokenResponse();
            response.setErrcode(INTERNAL_ERROR_CODE);
            response.setErrmsg("请求获取稳定access_token异常：" + e.getMessage());
            return response;
        }
    }

}
