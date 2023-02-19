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
package com.jastarwang.jefw.lock;

/**
 * 加锁失败异常
 * <p>当锁已被占用获取不到时，会抛出此异常</p>
 *
 * @author Jastar Wang
 * @date 2020/10/21
 * @since 1.0
 */
public class CannotAcquireLockException extends RuntimeException {

    public CannotAcquireLockException() {
    }

    public CannotAcquireLockException(String message) {
        super(message);
    }

}
