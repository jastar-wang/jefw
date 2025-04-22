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
package com.jastarwang.jefw.oss;

import java.io.Serial;

/**
 * 对象存储调用异常
 *
 * @author Jastar Wang
 * @date 2025/4/22
 * @since 2.1.0
 */
public class JefwOssException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public JefwOssException(Throwable cause) {
        super(cause);
    }

    public JefwOssException(String message, Throwable cause) {
        super(message, cause);
    }

}
