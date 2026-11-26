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
package com.jastarwang.jefw.sms;

/**
 * 短信渠道（平台）
 *
 * @author Jastar Wang
 * @since 1.3.0
 */
public enum SmsChannel {
    /**
     * 阿里云
     */
    ALIYUN,
    /**
     * 腾讯云
     */
    TENCENT,
    /**
     * 大汉三通
     */
    DH3TONG
}