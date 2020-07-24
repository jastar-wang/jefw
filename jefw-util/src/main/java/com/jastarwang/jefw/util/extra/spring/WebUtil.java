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

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes attr = RequestContextHolder.getRequestAttributes();
        return ((ServletRequestAttributes) attr).getRequest();
    }

    /**
     * 获取当前线程的Response对象
     *
     * @return response
     */
    public static HttpServletResponse getResponse() {
        RequestAttributes attr = RequestContextHolder.getRequestAttributes();
        return ((ServletRequestAttributes) attr).getResponse();
    }

    /**
     * 获取当前线程的Session对象
     *
     * @return session
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * 判断当前请求是否是Ajax请求
     *
     * @return true表示是，反之亦然
     */
    public static boolean isAjax() {
        String header = getRequest().getHeader("X-Requested-With");
        if ("XMLHttpRequest".equalsIgnoreCase(header)) {
            return true;
        }
        return false;
    }
}
