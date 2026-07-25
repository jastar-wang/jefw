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
package com.jastarwang.jefw.sms;

import java.io.Serial;
import java.util.List;

/**
 * 发送短信调用异常
 *
 * @author Jastar Wang
 * @date 2023/4/5
 * @since 1.3.0
 */
public class JefwSmsException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
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

    public JefwSmsException() {
    }

    public JefwSmsException(String message, Object originParam, Object originResponse, List<String> failedPhones) {
        super(message);
        this.originParam = originParam;
        this.originResponse = originResponse;
        this.failedPhones = failedPhones;
    }

    public JefwSmsException(String message, Throwable cause, Object originParam) {
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
        return "JefwSmsException{" +
                "originParam=" + originParam +
                ", originResponse=" + originResponse +
                ", failedPhones=" + failedPhones +
                '}';
    }
    
}
