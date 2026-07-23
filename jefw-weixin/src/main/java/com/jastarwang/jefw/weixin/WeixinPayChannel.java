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