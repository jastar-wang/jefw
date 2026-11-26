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
package com.jastarwang.jefw.redis.event;

/**
 * Redis 键过期事件。{@code key} 是已过期的键，{@code channel} 是发布通知的数据库频道，两者均按 UTF-8 解码。
 *
 * @author Jastar Wang
 * @since 2.6.0
 */
public record RedisKeyExpiredEvent(String key, String channel) {
}
