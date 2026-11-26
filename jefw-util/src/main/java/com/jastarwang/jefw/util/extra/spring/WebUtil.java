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
package com.jastarwang.jefw.util.extra.spring;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * Web工具类
 *
 * @author Jastar Wang
 * @since 1.0
 */
public class WebUtil {

    private WebUtil() {
    }

    /**
     * 获取当前线程的Request对象
     *
     * @return request
     * @throws IllegalStateException 当前线程未绑定请求时抛出
     */
    public static HttpServletRequest getRequest() {
        return getServletRequestAttributes().getRequest();
    }

    /**
     * 获取当前线程的Response对象
     *
     * @return response
     * @throws IllegalStateException 当前线程未绑定请求时抛出
     */
    public static HttpServletResponse getResponse() {
        return getServletRequestAttributes().getResponse();
    }

    /**
     * 获取当前线程的Session对象
     *
     * @return session
     * @throws IllegalStateException 当前线程未绑定请求时抛出
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * 判断当前请求是否是Ajax请求
     *
     * @return true or false
     * @throws IllegalStateException 当前线程未绑定请求时抛出
     */
    public static boolean isAjax() {
        return isAjax(getRequest());
    }

    /**
     * 判断指定请求是否是Ajax请求
     *
     * @return true or false
     * @since 1.2.2
     */
    public static boolean isAjax(HttpServletRequest request) {
        String header = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equalsIgnoreCase(header);
    }

    private static ServletRequestAttributes getServletRequestAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    }

}
