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

import java.io.Serial;
import java.io.Serializable;

/**
 * 异步请求响应包装类
 * <p>建议使用语义更为简洁的 {@link R}，本类在未来可能会被删除</p>
 *
 * @author Jastar Wang
 * @date 2020/11/18
 * @since 1.0
 */
public class ResponseResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 默认操作成功状态码
     */
    public static String DEFAULT_SUCCESS_CODE = "0";
    /**
     * 默认操作成功提示信息
     */
    public static String DEFAULT_SUCCESS_MSG = "ok";
    /**
     * 默认操作失败状态码
     */
    public static String DEFAULT_FAIL_CODE = "-1";
    /**
     * 默认操作失败提示信息
     */
    public static String DEFAULT_FAIL_MSG = "failed";

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
     * <b>该状态码非HTTP状态码，默认成功返回{@link ResponseResult#DEFAULT_SUCCESS_CODE}，失败默认返回{@link ResponseResult#DEFAULT_FAIL_CODE}</b>
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
     * @since 1.1.1
     */
    public static <T> ResponseResult<T> success(String code, String msg) {
        return success(code, msg, null);
    }

    /**
     * @since 1.1.1
     */
    public static <T> ResponseResult<T> success(String code, String msg, T data) {
        return new ResponseResult<>(true, code, msg, data);
    }

    /**
     * 格式化信息并返回
     *
     * @param code   非空，响应码对象，其提示信息可使用“{}”占位格式化
     * @param params 可空，参数列表，要与占位符的个数一致
     * @since 1.3.0
     */
    public static <T> ResponseResult<T> successf(AbstractResponseCode code, Object... params) {
        String finalMsg = (params != null && params.length > 0) ? String.format(code.getMsg().replaceAll("\\{}", "%s"), params) : code.getMsg();
        return success(code.getCode(), finalMsg, null);
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
     * @since 1.1.1
     */
    public static <T> ResponseResult<T> fail(String code, String msg) {
        return fail(code, msg, null);
    }

    /**
     * @since 1.1.1
     */
    public static <T> ResponseResult<T> fail(String code, String msg, T data) {
        return new ResponseResult<>(false, code, msg, data);
    }

    /**
     * 格式化信息并返回
     *
     * @param code   非空，响应码对象，其提示信息可使用“{}”占位格式化
     * @param params 可空，参数列表，要与占位符的个数一致
     * @since 1.3.0
     */
    public static <T> ResponseResult<T> failf(AbstractResponseCode code, Object... params) {
        String finalMsg = (params != null && params.length > 0) ? String.format(code.getMsg().replaceAll("\\{}", "%s"), params) : code.getMsg();
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
     * @since 1.3.0
     */
    public static synchronized void setDefaultCodeAndMsg(String successCode, String successMsg, String failCode, String failMsg) {
        if (successCode != null && !"".equals(successCode.trim())) {
            DEFAULT_SUCCESS_CODE = successCode;
        }
        if (successMsg != null && !"".equals(successMsg.trim())) {
            DEFAULT_SUCCESS_MSG = successMsg;
        }
        if (failCode != null && !"".equals(failCode.trim())) {
            DEFAULT_FAIL_CODE = failCode;
        }
        if (failMsg != null && !"".equals(failMsg.trim())) {
            DEFAULT_FAIL_MSG = failMsg;
        }
    }

    /**
     * 恢复默认的状态码与提示信息
     *
     * @since 1.3.0
     */
    public static void resetDefaultCodeAndMsg() {
        setDefaultCodeAndMsg("0", "ok", "-1", "failed");
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
