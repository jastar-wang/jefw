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
 * 微信支付细分渠道
 * <p>参考：<a href="https://pay.weixin.qq.com/doc/v3/merchant/4012062524">官方文档</a></p>
 *
 * @author Jastar Wang
 * @date 2025/4/9
 * @since 2.2.0
 */
public enum WeixinPayChannel {
    /**
     * JSAPI（与小程序共用接口）
     */
    JSAPI,
    /**
     * 小程序（与JSAPI共用接口）
     */
    MINI_PROGRAM,
    /**
     * APP
     */
    APP,
    /**
     * H5
     */
    H5,
    /**
     * NATIVE
     */
    NATIVE
}