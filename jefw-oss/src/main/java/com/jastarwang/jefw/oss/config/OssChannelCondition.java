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
package com.jastarwang.jefw.oss.config;

import com.jastarwang.jefw.oss.OssChannel;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 按应用配置的渠道名单筛选厂商，省略名单时保留原有装配规则。
 *
 * @author Jastar Wang
 * @since 2.5.1
 */
class OssChannelCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        OssChannel channel = metadata.getAnnotations().get(ConditionalOnOssChannel.class)
                .getEnum("value", OssChannel.class);
        return Binder.get(context.getEnvironment()).bind("jefw.oss.enabled-channels", Bindable.setOf(OssChannel.class))
                .map(channels -> channels.contains(channel)).orElse(true);
    }

}
