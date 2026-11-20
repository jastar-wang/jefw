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
package com.jastarwang.jefw.oss.model;

import java.util.List;

/**
 * 对象分页包装类
 *
 * @param items                 本页对象，过滤后可能为空
 * @param nextContinuationToken 下一页令牌，null 表示结束
 * @author Jastar Wang
 * @date 2026/11/20
 * @since 2.5.1
 */
public record ObjectPage(List<ObjectSummary> items, String nextContinuationToken) {
    public ObjectPage {
        items = List.copyOf(items);
    }

    public static void validateMaxKeys(int maxKeys) {
        if (maxKeys < 1 || maxKeys > 1000) {
            throw new IllegalArgumentException("maxKeys 必须在 1 到 1000 之间");
        }
    }
}
