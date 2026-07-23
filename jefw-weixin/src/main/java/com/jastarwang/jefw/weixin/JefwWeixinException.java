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

import java.io.Serial;

/**
 * 微信平台调用异常
 *
 * @author Jastar Wang
 * @date 2025/4/22
 * @since 2.2.0
 */
public class JefwWeixinException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public JefwWeixinException(Throwable cause) {
        super(cause);
    }

    public JefwWeixinException(String message, Throwable cause) {
        super(message, cause);
    }

}
