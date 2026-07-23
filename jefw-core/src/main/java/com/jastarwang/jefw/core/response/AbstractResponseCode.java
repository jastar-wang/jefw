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
package com.jastarwang.jefw.core.response;

/**
 * 响应状态码接口
 * <p>PS：自定义状态码可使用枚举类，并实现本接口即可</p>
 *
 * @author Jastar Wang
 * @date 2020/11/19
 * @since 1.0
 */
public interface AbstractResponseCode {
    /**
     * 获取状态码
     *
     * @return 状态码
     */
    String getCode();

    /**
     * 获取提示信息
     *
     * @return 提示信息
     */
    String getMsg();
}
