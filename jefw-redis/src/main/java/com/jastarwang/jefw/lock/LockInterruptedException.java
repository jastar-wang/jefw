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
package com.jastarwang.jefw.lock;

/**
 * 加锁的线程被打断异常
 * <p>当持有锁的线程被打断时，会抛出此异常，但仍然能自动释放锁</p>
 *
 * @author Jastar Wang
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
