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
