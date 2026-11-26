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

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * 异步请求响应包装类
 *
 * @author Jastar Wang
 * @since 2.3.0
 */
public class R<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String ORIGINAL_SUCCESS_CODE = "0";
    private static final String ORIGINAL_SUCCESS_MSG = "ok";
    private static final String ORIGINAL_FAIL_CODE = "-1";
    private static final String ORIGINAL_FAIL_MSG = "failed";

    /**
     * 默认操作成功状态码
     */
    public static String DEFAULT_SUCCESS_CODE = ORIGINAL_SUCCESS_CODE;
    /**
     * 默认操作成功提示信息
     */
    public static String DEFAULT_SUCCESS_MSG = ORIGINAL_SUCCESS_MSG;
    /**
     * 默认操作失败状态码
     */
    public static String DEFAULT_FAIL_CODE = ORIGINAL_FAIL_CODE;
    /**
     * 默认操作失败提示信息
     */
    public static String DEFAULT_FAIL_MSG = ORIGINAL_FAIL_MSG;

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
     * <b>该状态码非HTTP状态码，默认成功返回{@link R#DEFAULT_SUCCESS_CODE}，失败默认返回{@link R#DEFAULT_FAIL_CODE}</b>
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

    public static <T> R<T> ok() {
        return ok(DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MSG);
    }

    public static <T> R<T> ok(T data) {
        return ok(DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MSG, data);
    }

    public static <T> R<T> ok(AbstractResponseCode code) {
        return ok(code, null);
    }

    public static <T> R<T> ok(AbstractResponseCode code, T data) {
        return ok(code.getCode(), code.getMsg(), data);
    }

    public static <T> R<T> ok(String code, String msg) {
        return ok(code, msg, null);
    }

    public static <T> R<T> ok(String code, String msg, T data) {
        return new R<>(true, code, msg, data);
    }

    /**
     * 格式化信息并返回
     *
     * @param code   非空，响应码对象，其提示信息可使用“{}”占位格式化
     * @param params 可空，参数列表，要与占位符的个数一致
     */
    public static <T> R<T> okf(AbstractResponseCode code, Object... params) {
        String finalMsg = formatMsg(code, params);
        return ok(code.getCode(), finalMsg, null);
    }

    public static <T> R<T> fail() {
        return fail(DEFAULT_FAIL_CODE, DEFAULT_FAIL_MSG);
    }

    public static <T> R<T> fail(T data) {
        return fail(DEFAULT_FAIL_CODE, DEFAULT_FAIL_MSG, data);
    }

    public static <T> R<T> fail(AbstractResponseCode code) {
        return fail(code, null);
    }

    public static <T> R<T> fail(AbstractResponseCode code, T data) {
        return fail(code.getCode(), code.getMsg(), data);
    }

    public static <T> R<T> fail(String code, String msg) {
        return fail(code, msg, null);
    }

    public static <T> R<T> fail(String code, String msg, T data) {
        return new R<>(false, code, msg, data);
    }

    /**
     * 格式化信息并返回
     *
     * @param code   非空，响应码对象，其提示信息可使用“{}”占位格式化
     * @param params 可空，参数列表，要与占位符的个数一致
     */
    public static <T> R<T> failf(AbstractResponseCode code, Object... params) {
        String finalMsg = formatMsg(code, params);
        return fail(code.getCode(), finalMsg, null);
    }

    /**
     * 设置默认的状态码与提示信息
     * <p>适用场景：需自定义默认信息，可在适当位置（如：main方法）调用该方法设置一次即可。</p>
     *
     * @param successCode 可空，成功状态码
     * @param successMsg  可空，成功提示信息
     * @param failCode    可空，失败状态码
     * @param failMsg     可空，失败提示信息
     */
    public static synchronized void setDefaultCodeAndMsg(String successCode, String successMsg, String failCode, String failMsg) {
        if (successCode != null && !successCode.isBlank()) {
            DEFAULT_SUCCESS_CODE = successCode;
        }
        if (successMsg != null && !successMsg.isBlank()) {
            DEFAULT_SUCCESS_MSG = successMsg;
        }
        if (failCode != null && !failCode.isBlank()) {
            DEFAULT_FAIL_CODE = failCode;
        }
        if (failMsg != null && !failMsg.isBlank()) {
            DEFAULT_FAIL_MSG = failMsg;
        }
    }

    /**
     * 恢复默认的状态码与提示信息
     */
    public static void resetDefaultCodeAndMsg() {
        setDefaultCodeAndMsg(ORIGINAL_SUCCESS_CODE, ORIGINAL_SUCCESS_MSG, ORIGINAL_FAIL_CODE, ORIGINAL_FAIL_MSG);
    }

    private static String formatMsg(AbstractResponseCode code, Object... params) {
        Objects.requireNonNull(code, "code must not be null");
        String msg = code.getMsg();
        return (params != null && params.length > 0) ? String.format(msg.replace("{}", "%s"), params) : msg;
    }

    //=============================== constructor =================================//

    private R() {
    }

    private R(boolean success, String code, String msg, T data) {
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
        return "R{" +
                "success=" + success +
                ", code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

}
