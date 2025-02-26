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
 * 加锁的线程被打断异常
 * <p>当持有锁的线程被打断时，会抛出此异常，但仍然能自动释放锁</p>
 *
 * @author Jastar Wang
 * @date 2023/02/19
 * @since 1.2.4
 */
public class LockInterruptedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public LockInterruptedException() {
    }

    public LockInterruptedException(String message) {
        super(message);
    }

    public LockInterruptedException(Throwable cause) {
        super(cause);
    }

}
