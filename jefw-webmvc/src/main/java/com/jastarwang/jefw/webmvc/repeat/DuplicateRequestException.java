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
package com.jastarwang.jefw.webmvc.repeat;

/**
 * 当前窗口内相同的请求已经占位。宿主可以将注解提供的提示映射到自己的响应协议。
 *
 * @author Jastar Wang
 * @since 2.6.0
 */
public class DuplicateRequestException extends RuntimeException {

    public DuplicateRequestException(String message) {
        super(message);
    }

}
