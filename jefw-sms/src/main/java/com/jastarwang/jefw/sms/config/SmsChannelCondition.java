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
package com.jastarwang.jefw.sms.config;

import com.jastarwang.jefw.sms.SmsChannel;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * 按应用配置的渠道名单筛选厂商，省略名单时保留原有装配规则。
 *
 * @author Jastar Wang
 * @date 2026/11/23
 * @since 2.5.1
 */
class SmsChannelCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        Set<SmsChannel> selected = Binder.get(environment)
                .bind("jefw.sms.enabled-channels", Bindable.setOf(SmsChannel.class)).orElse(null);
        MergedAnnotation<ConditionalOnSmsChannel> condition = metadata.getAnnotations().get(ConditionalOnSmsChannel.class);
        SmsChannel channel = condition.getEnum("value", SmsChannel.class);
        if (selected != null && !selected.contains(channel)) {
            return false;
        }
        String requiredProperty = condition.getString("requiredProperty");
        if (requiredProperty.isEmpty()) {
            return true;
        }
        String value = environment.getProperty(requiredProperty);
        // 大汉三通省略名单时，保留旧版按属性存在性装配的行为。
        boolean requireText = selected != null || channel == SmsChannel.ALIYUN;
        return value != null && !"false".equalsIgnoreCase(value) && (!requireText || StringUtils.hasText(value));
    }

}
