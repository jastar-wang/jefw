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

import java.lang.annotation.*;

/**
 * 限制代理方法在指定时间窗口内重复提交。按方法和指定参数生成 Redis 键。使用方须以 {@code -parameters} 编译该方法。
 *
 * @author Jastar Wang
 * @date 2025/3/13
 * @since 2.6.0
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestRepeatLimit {
    /**
     * 用来生成请求唯一标识的参数字段
     *
     * @return 参数字段数组，支持复杂类型，如：order.orderNo
     */
    String[] fields();

    /**
     * 计算唯一标识时是否需要带上当前会话用户ID
     * <p><b>注意：如果设置为true，则需要实现 {@link SessionUserProvider} 接口</b></p>
     *
     * @return 是否需要带上当前会话用户ID
     */
    boolean appendSessionUserId() default false;

    /**
     * 防重时间窗口（单位：秒）
     *
     * @return 防重时间窗口（单位：秒）
     */
    int timeout() default 5;

    /**
     * 错误时的提示信息
     *
     * @return 错误时的提示信息
     */
    String tips() default "啊哦，手快了~";

    /**
     * 唯一标识存放缓存时的前缀
     *
     * @return 唯一标识存放缓存时的前缀
     */
    String uniqueKeyPrefix() default "unique:";
}
