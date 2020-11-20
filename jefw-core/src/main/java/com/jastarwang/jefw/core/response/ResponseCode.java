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
package com.jastarwang.jefw.core.response;

/**
 * 响应状态码接口
 * <p>PS：自定义状态码可使用枚举类，并实现本接口即可</p>
 *
 * @author Jastar Wang
 * @date 2020/11/19
 * @since 1.0
 */
public interface ResponseCode {
    /**
     * 默认操作成功状态码
     */
    String DEFAULT_SUCCESS_CODE = "1";
    /**
     * 默认操作成功提示信息
     */
    String DEFAULT_SUCCESS_MSG = "succeed";
    /**
     * 默认操作失败状态码
     */
    String DEFAULT_FAIL_CODE = "0";
    /**
     * 默认操作失败提示信息
     */
    String DEFAULT_FAIL_MSG = "failed";

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
