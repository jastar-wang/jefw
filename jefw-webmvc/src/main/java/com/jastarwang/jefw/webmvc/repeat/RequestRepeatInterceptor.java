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
package com.jastarwang.jefw.webmvc.repeat;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;

/**
 * 从选定的参数路径计算防重键，只有 Redis 原子占位成功才执行目标方法。
 *
 * @author Jastar Wang
 * @since 2.6.0
 */
final class RequestRepeatInterceptor implements MethodInterceptor {

    private final ObjectProvider<RequestRepeatAutoConfiguration.RepeatWindow> windows;
    private final ObjectProvider<SessionUserProvider> users;

    RequestRepeatInterceptor(ObjectProvider<RequestRepeatAutoConfiguration.RepeatWindow> windows,
                             ObjectProvider<SessionUserProvider> users) {
        this.windows = windows;
        this.users = users;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = AopUtils.getMostSpecificMethod(invocation.getMethod(), invocation.getThis().getClass());
        RequestRepeatLimit limit = AnnotatedElementUtils.findMergedAnnotation(method, RequestRepeatLimit.class);
        RequestRepeatAutoConfiguration.RepeatWindow window = windows.getIfAvailable();
        if (window == null) {
            throw new IllegalStateException("@RequestRepeatLimit 需要 Spring Data Redis 和 StringRedisTemplate Bean");
        }
        if (limit.timeout() <= 0 || limit.fields().length == 0) {
            throw new IllegalArgumentException("@RequestRepeatLimit 的 timeout 必须为正数，fields 不能为空：" + method);
        }
        StringBuilder key = new StringBuilder();
        appendValue(key, method.toGenericString());
        for (String path : limit.fields()) {
            appendValue(key, fieldValue(method, invocation.getArguments(), path));
        }
        if (limit.appendSessionUserId()) {
            SessionUserProvider provider = users.getIfAvailable();
            if (provider == null) {
                throw new IllegalStateException("@RequestRepeatLimit 的 appendSessionUserId 需要 SessionUserProvider Bean");
            }
            Object user = provider.getSessionUserId();
            if (user == null) {
                throw new IllegalStateException("SessionUserProvider#getSessionUserId() 不能返回 null");
            }
            appendValue(key, user);
        }
        String digest = DigestUtils.md5DigestAsHex(key.toString().getBytes(StandardCharsets.UTF_8));
        Boolean reserved = window.reserve(limit.uniqueKeyPrefix() + digest, limit.timeout());
        if (reserved == null) {
            throw new IllegalStateException("Redis 未返回 @RequestRepeatLimit 占位结果");
        }
        if (!reserved) {
            throw new DuplicateRequestException(limit.tips());
        }
        return invocation.proceed();
    }

    private Object fieldValue(Method method, Object[] arguments, String path) {
        String[] parts = path.split("\\.", -1);
        Parameter[] parameters = method.getParameters();
        for (int index = 0; index < parameters.length; index++) {
            Parameter parameter = parameters[index];
            if (!parameter.isNamePresent()) {
                throw new IllegalStateException("@RequestRepeatLimit 无法读取参数名，请使用 -parameters 编译：" + method);
            }
            if (!parameter.getName().equals(parts[0])) {
                continue;
            }
            Object value = arguments[index];
            Class<?> type = parameter.getType();
            for (int i = 1; i < parts.length; i++) {
                // 值为 null 时按声明类型继续检查路径，避免把拼错的字段当作合法 null。
                Field field = ReflectionUtils.findField(value == null ? type : value.getClass(), parts[i]);
                if (field == null) {
                    throw new IllegalArgumentException("@RequestRepeatLimit 字段路径不存在：" + path + "，方法：" + method);
                }
                type = field.getType();
                if (value != null) {
                    ReflectionUtils.makeAccessible(field);
                    value = ReflectionUtils.getField(field, value);
                }
            }
            return value;
        }
        throw new IllegalArgumentException("@RequestRepeatLimit 参数不存在：" + path + "，方法：" + method);
    }

    private void appendValue(StringBuilder builder, Object value) {
        if (value == null) {
            builder.append("-1:");
            return;
        }
        // 保持原有的类型和长度前缀，避免组合值间的分隔符冲突。
        String text = value.getClass().getName() + ":" + value;
        builder.append(text.length()).append(':').append(text);
    }

}
