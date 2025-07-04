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

/**
 * 微信开发细分渠道
 * <p>参考：<a href="https://developers.weixin.qq.com/doc/">官方文档</a></p>
 *
 * @author Jastar Wang
 * @date 2025/4/9
 * @since 2.2.0
 */
public enum WeixinDevChannel {
    /**
     * 小程序
     */
    MINI_PROGRAM,
    /**
     * 公众平台（公众号+服务号）
     * <p>实际上微信官方已将公众号和服务号拆分为独立渠道</p>
     */
    OFFICIAL_ACCOUNT,
    /**
     * 公众号（原订阅号）
     */
    OA_SUBSCRIPTION,
    /**
     * 服务号
     */
    OA_SERVICE,
}