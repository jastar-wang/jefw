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

import com.jastarwang.jefw.weixin.model.dev.AccessTokenResponse;

/**
 * 微信开发通用接口
 * <p>各个细分渠道通用的接口会定义在此处，细分渠道定制化的接口则定义在对应渠道的接口中</p>
 *
 * @author Jastar Wang
 * @date 2025/4/9
 * @since 2.2.0
 */
public interface JefwWeixinDevService {
    /**
     * 获取实现渠道
     *
     * @return 实现渠道
     */
    WeixinDevChannel getChannel();

    /**
     * 获取微信接口调用凭证
     *
     * @param appId     非空，AppID
     * @param appSecret 非空，AppSecret
     * @return 接口调用凭证对象
     */
    AccessTokenResponse getAccessToken(String appId, String appSecret);

    /**
     * 获取微信接口调用凭证
     *
     * @param appId        非空，AppID
     * @param appSecret    非空，AppSecret
     * @param forceRefresh 可空，false: 缺省，普通调用模式，access_token 有效期内重复调用该接口不会更新 access_token；
     *                     true: 强制刷新模式，会导致上次获取的 access_token 失效，并返回新的 access_token
     * @return 接口调用凭证对象
     */
    AccessTokenResponse getStableAccessToken(String appId, String appSecret, Boolean forceRefresh);
}
