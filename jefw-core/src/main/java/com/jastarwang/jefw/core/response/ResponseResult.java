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

import java.io.Serializable;

import static com.jastarwang.jefw.core.response.AbstractResponseCode.*;

/**
 * 异步请求响应包装类
 *
 * @author Jastar Wang
 * @date 2020/11/18
 * @since 1.0
 */
public class ResponseResult<T> implements Serializable {

    private static final long serialVersionUID = -2206058744756418468L;

    /**
     * 业务操作是否成功
     * <p>
     * <b>即业务请求的目的是否达到，该字段是对 <code>code</code> 的简化，减少调用者的判断逻辑</b>
     * </p>
     */
    private boolean success;

    /**
     * 状态码
     * <p>
     * <b>该状态码非HTTP状态码，除非自定义，操作成功默认返回1，操作失败默认返回0</b>
     * </p>
     */
    private String code;

    /**
     * 提示信息
     */
    private String msg;

    /**
     * 业务数据
     */
    private T data;

    public static <T> ResponseResult<T> success() {
        return success(DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MSG);
    }

    public static <T> ResponseResult<T> success(T data) {
        return success(DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MSG, data);
    }

    public static <T> ResponseResult<T> success(AbstractResponseCode code) {
        return success(code, null);
    }

    public static <T> ResponseResult<T> success(AbstractResponseCode code, T data) {
        return success(code.getCode(), code.getMsg(), data);
    }

    /**
     * @since 1.1
     */
    public static <T> ResponseResult<T> success(String code, String msg) {
        return success(code, msg, null);
    }

    /**
     * @since 1.1
     */
    public static <T> ResponseResult<T> success(String code, String msg, T data) {
        return new ResponseResult<>(true, code, msg, data);
    }

    public static <T> ResponseResult<T> fail() {
        return fail(DEFAULT_FAIL_CODE, DEFAULT_FAIL_MSG);
    }

    public static <T> ResponseResult<T> fail(T data) {
        return fail(DEFAULT_FAIL_CODE, DEFAULT_FAIL_MSG, data);
    }

    public static <T> ResponseResult<T> fail(AbstractResponseCode code) {
        return fail(code, null);
    }

    public static <T> ResponseResult<T> fail(AbstractResponseCode code, T data) {
        return fail(code.getCode(), code.getMsg(), data);
    }

    /**
     * @since 1.1
     */
    public static <T> ResponseResult<T> fail(String code, String msg) {
        return fail(code, msg, null);
    }

    /**
     * @since 1.1
     */
    public static <T> ResponseResult<T> fail(String code, String msg, T data) {
        return new ResponseResult<>(false, code, msg, data);
    }

    //=============================== constructor =================================//

    private ResponseResult() {
    }

    private ResponseResult(boolean success, String code, String msg, T data) {
        this.success = success;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    //================================= getter ===================================//

    public boolean isSuccess() {
        return success;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ResponseResult{" +
                "success=" + success +
                ", code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
