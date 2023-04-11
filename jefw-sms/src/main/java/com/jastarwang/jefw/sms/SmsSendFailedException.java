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
package com.jastarwang.jefw.sms;

import java.util.List;

/**
 * 短信发送失败异常
 *
 * @author Jastar Wang
 * @date 2023/4/5
 * @since 1.3.0
 */
public class SmsSendFailedException extends RuntimeException {
    /**
     * 原始参数
     */
    private Object originParam;
    /**
     * 原始响应
     */
    private Object originResponse;
    /**
     * 发送失败的手机号
     */
    private List<String> failedPhones;

    public SmsSendFailedException() {
    }

    public SmsSendFailedException(String message, Object originParam, Object originResponse, List<String> failedPhones) {
        super(message);
        this.originParam = originParam;
        this.originResponse = originResponse;
        this.failedPhones = failedPhones;
    }

    public SmsSendFailedException(String message, Throwable cause, Object originParam) {
        super(message, cause);
        this.originParam = originParam;
    }

    public Object getOriginParam() {
        return originParam;
    }

    public void setOriginParam(Object originParam) {
        this.originParam = originParam;
    }

    public Object getOriginResponse() {
        return originResponse;
    }

    public void setOriginResponse(Object originResponse) {
        this.originResponse = originResponse;
    }

    public List<String> getFailedPhones() {
        return failedPhones;
    }

    public void setFailedPhones(List<String> failedPhones) {
        this.failedPhones = failedPhones;
    }

    @Override
    public String toString() {
        return "SmsSendFailedException{" +
                "originParam=" + originParam +
                ", originResponse=" + originResponse +
                ", failedPhones=" + failedPhones +
                '}';
    }
}
