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
package com.jastarwang.jefw.webmvc.log;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义当前 MVC 方法的日志输出。
 *
 * @author Jastar Wang
 * @since 2.6.0
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestLog {

    /**
     * 是否记录该接口的请求日志。
     *
     * @return 默认记录，设为 {@code false} 时跳过该接口
     */
    boolean print() default true;

    /**
     * 额外需要脱敏的入参路径，例如 {@code request.users.name}。
     * 路径经过数组时，匹配数组中的所有元素。
     *
     * @return 从方法参数名开始的脱敏路径数组
     */
    String[] hiddenParams() default {};

}
