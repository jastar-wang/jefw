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
 * Redis 报告的过期键及其通知频道（UTF-8）。
 *
 * @param key     已过期的键
 * @param channel 发布该通知的实际数据库频道
 * @author Jastar Wang
 * @date 2026/11/25
 * @since 2.6.0
 */
public record RedisKeyExpiredEvent(String key, String channel) {
}
