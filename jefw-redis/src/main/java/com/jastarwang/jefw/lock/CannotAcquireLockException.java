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
 * 加锁失败异常
 * <p>当锁已被占用获取不到时，会抛出此异常</p>
 *
 * @author Jastar Wang
 * @date 2020/10/21
 * @since 1.0
 */
public class CannotAcquireLockException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CannotAcquireLockException() {
    }

    public CannotAcquireLockException(String message) {
        super(message);
    }

}
