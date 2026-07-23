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

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents the owner of a bucket.
 *
 * @author Jastar Wang
 * @date 2023/5/9
 * @since 1.3.1
 */
public class Owner implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String displayName;
    private String id;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Owner{" +
                "displayName='" + displayName + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

}
