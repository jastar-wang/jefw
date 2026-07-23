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

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jastarwang.jefw.weixin.JefwWeixinDevMpService;
import com.jastarwang.jefw.weixin.WeixinDevChannel;
import com.jastarwang.jefw.weixin.model.BaseResponse;
import com.jastarwang.jefw.weixin.model.dev.Code2SessionResponse;
import com.jastarwang.jefw.weixin.model.dev.PhoneNumberResponse;
import com.jastarwang.jefw.weixin.model.dev.SubscribeMsgMpRequest;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 微信开发小程序接口实现
 *
 * @author Jastar Wang
 * @date 2025/4/9
 * @since 2.2.0
 */
public class JefwWeixinDevMpServiceImpl extends JefwWeixinDevServiceImpl implements JefwWeixinDevMpService {

    @Override
    public WeixinDevChannel getChannel() {
        return WeixinDevChannel.MINI_PROGRAM;
    }

    @Override
    public Code2SessionResponse code2Session(String appId, String appSecret, String jsCode) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid={}&secret={}&js_code={}&grant_type=authorization_code";
        url = StrUtil.format(url, appId, appSecret, jsCode);
        try {
            String resBody = HttpUtil.get(url);
            return JSONUtil.toBean(resBody, Code2SessionResponse.class);
        } catch (Throwable e) {
            Code2SessionResponse response = new Code2SessionResponse();
            response.setErrcode(INTERNAL_ERROR_CODE);
            response.setErrmsg("请求获取小程序会话异常：" + e.getMessage());
            return response;
        }
    }

    @Override
    public PhoneNumberResponse getPhoneNumber(String accessToken, String code) {
        String url = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token={}";
        url = StrUtil.format(url, accessToken);
        JSONObject reqBody = new JSONObject();
        reqBody.set("code", code);
        PhoneNumberResponse response = new PhoneNumberResponse();
        try {
            String resBody = HttpUtil.post(url, reqBody.toString());
            JSONObject resJson = JSONUtil.parseObj(resBody);
            if (resJson.containsKey("phone_info")) {
                response = resJson.getBean("phone_info", PhoneNumberResponse.class);
            }
            if (resJson.containsKey("errcode")) {
                response.setErrcode(resJson.getInt("errcode"));
            }
            if (resJson.containsKey("errmsg")) {
                response.setErrmsg(resJson.getStr("errmsg"));
            }
        } catch (Throwable e) {
            response.setErrcode(INTERNAL_ERROR_CODE);
            response.setErrmsg("请求获取用户手机号异常：" + e.getMessage());
        }
        return response;
    }

    @Override
    public BaseResponse sendSubscribeMsg(SubscribeMsgMpRequest request) {
        String url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token={}";
        url = StrUtil.format(url, request.getAccessToken());
        JSONObject reqBody = new JSONObject();
        reqBody.set("touser", request.getToUserOpenid());
        reqBody.set("template_id", request.getTemplateId());
        reqBody.set("lang", StrUtil.blankToDefault(request.getLang(), "zh_CN"));
        reqBody.set("miniprogram_state", StrUtil.blankToDefault(request.getEnvVersion(), "formal"));
        if (StrUtil.isNotBlank(request.getJumpPagePath())) {
            reqBody.set("page", request.getJumpPagePath());
        }
        try {
            JSONObject data = new JSONObject();
            if (request.getData() instanceof Map) {
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) request.getData()).entrySet()) {
                    JSONObject value = new JSONObject();
                    value.set("value", entry.getValue());
                    data.set(entry.getKey().toString(), value);
                }
            } else {
                Map<String, Field> fieldMap = ReflectUtil.getFieldMap(request.getData().getClass());
                fieldMap.forEach((k, v) -> {
                    JSONObject value = new JSONObject();
                    v.setAccessible(true);
                    try {
                        value.set("value", v.get(request.getData()));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    data.set(k, value);
                });
            }
            reqBody.set("data", data);
            String resBody = HttpUtil.post(url, reqBody.toString());
            return JSONUtil.toBean(resBody, BaseResponse.class);
        } catch (Throwable e) {
            BaseResponse response = new BaseResponse();
            response.setErrcode(INTERNAL_ERROR_CODE);
            response.setErrmsg("发送订阅消息时异常：" + e.getMessage());
            return response;
        }
    }

}
