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
 * @date 2020/7/23
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
