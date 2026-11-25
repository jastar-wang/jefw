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
package com.jastarwang.jefw.webmvc.log;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotatedMethod;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.stream.BaseStream;

/**
 * 记录 MVC 控制器映射方法的实参、返回值和调用耗时。上传、流及异步返回值仅记录占位符。
 * 参数绑定或校验在方法调用前失败时，不会产生请求日志。
 *
 * @author Jastar Wang
 * @date 2026/11/25
 * @since 2.6.0
 */
public class RequestLogAspect extends StaticMethodMatcherPointcutAdvisor {

    private static final Logger log = LoggerFactory.getLogger(RequestLogAspect.class);
    private static final String SWAGGER_HIDDEN = "io.swagger.v3.oas.annotations.Hidden";
    private static final String MASK = "******";
    private static final Set<String> HIDDEN_FIELDS = Set.of("password", "newpassword", "confirmpassword", "secretkey", "accesskey", "token");

    private final ObjectMapper mapper;

    public RequestLogAspect(ObjectMapper objectMapper) {
        SimpleModule module = new SimpleModule("jefw-request-log");
        JsonSerializer<Object> skipped = new JsonSerializer<>() {
            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString("skipped");
            }
        };
        for (Class<?> type : List.of(MultipartFile.class, Part.class, ServletRequest.class, ServletResponse.class,
                BindingResult.class, Resource.class, InputStream.class, OutputStream.class, Reader.class, Writer.class,
                BaseStream.class, ResponseBodyEmitter.class, StreamingResponseBody.class, Callable.class,
                DeferredResult.class, CompletionStage.class, WebAsyncTask.class)) {
            module.addSerializer(type, skipped);
        }
        mapper = objectMapper.copy().registerModule(module);
        setOrder(100);
        setClassFilter(type -> AnnotatedElementUtils.hasAnnotation(type, Controller.class)
                && !AnnotatedElementUtils.isAnnotated(type, SWAGGER_HIDDEN));
        setAdvice((MethodInterceptor) this::around);
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        Method handler = AopUtils.getMostSpecificMethod(method, targetClass);
        return AnnotatedElementUtils.hasAnnotation(handler, RequestMapping.class)
                && !AnnotatedElementUtils.isAnnotated(handler, SWAGGER_HIDDEN);
    }

    private Object around(MethodInvocation invocation) throws Throwable {
        long start = System.nanoTime();
        Object result = null;
        Throwable failure = null;
        try {
            result = invocation.proceed();
            return result;
        } catch (Throwable e) {
            failure = e;
            throw e;
        } finally {
            try {
                long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                logRequest(invocation, result, failure, elapsed);
            } catch (Throwable e) {
                // 宿主序列化器的异常消息可能包含尚未脱敏的参数。
                log.error("打印请求日志时异常：{}", e.getClass().getName());
            }
        }
    }

    private void logRequest(MethodInvocation invocation, Object result, Throwable failure, long elapsed) {
        if (!log.isInfoEnabled()
                || !(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        String uri = request.getRequestURI();
        String path = uri.substring(request.getContextPath().length());
        Method method = AopUtils.getMostSpecificMethod(invocation.getMethod(), invocation.getThis().getClass());
        RequestLog options = AnnotatedElementUtils.findMergedAnnotation(method, RequestLog.class);
        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") || (options != null && !options.print())) {
            return;
        }
        JsonNode params = requestParams(method, invocation.getArguments(), options);
        Object body = result instanceof HttpEntity<?> entity ? entity.getBody() : result;
        JsonNode response = maskedTree(failure == null ? body : "[异常]" + failure.getClass().getName());
        Object route = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        // 自定义 HandlerMapping 可能没有路由模板，此时使用方法名，避免回退到含敏感路径参数的 URI。
        log.info("[{}]参数：{}, 响应: {}, 耗时: {}ms", route != null ? route : method.getName(), params, response, elapsed);
    }

    private JsonNode requestParams(Method method, Object[] values, RequestLog options) {
        Map<String, Object> params = new LinkedHashMap<>();
        // MVC 也会从控制器接口读取参数绑定注解。
        MethodParameter[] parameters = new AnnotatedMethod(method).getMethodParameters();
        for (int i = 0; i < values.length; i++) {
            MethodParameter parameter = parameters[i];
            String name = parameter.getParameter().getName();
            if (isSensitiveBinding(parameter)) {
                params.put(name, MASK);
                continue;
            }
            Object value = values[i];
            params.put(name, value instanceof HttpEntity<?> entity ? entity.getBody() : value);
        }
        JsonNode tree = null;
        if (options != null) {
            for (String path : options.hiddenParams()) {
                if (path.isBlank()) {
                    continue;
                }
                String[] parts = path.split("\\.", -1);
                if (!params.containsKey(parts[0])) {
                    return unresolvedPath(method, path);
                }
                if (tree == null) {
                    tree = mapper.valueToTree(params);
                }
                if (!maskPath(tree, parts, 0)) {
                    return unresolvedPath(method, path);
                }
            }
        }
        if (tree == null) {
            return maskedTree(params);
        }
        maskSensitiveFields(tree);
        return tree;
    }

    private JsonNode unresolvedPath(Method method, String path) {
        log.warn("跳过 {} 的请求参数：无法匹配脱敏路径 {}", method, path);
        return mapper.getNodeFactory().textNode("skipped");
    }

    private JsonNode maskedTree(Object value) {
        JsonNode tree = mapper.valueToTree(value);
        maskSensitiveFields(tree);
        return tree;
    }

    private void maskSensitiveFields(JsonNode node) {
        if (node instanceof ObjectNode object) {
            object.properties().forEach(entry -> {
                if (isSensitiveField(entry.getKey())) {
                    object.put(entry.getKey(), MASK);
                } else {
                    maskSensitiveFields(entry.getValue());
                }
            });
        } else if (node instanceof ArrayNode array) {
            array.forEach(this::maskSensitiveFields);
        }
    }

    private boolean isSensitiveBinding(MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(RequestHeader.class) || parameter.hasParameterAnnotation(CookieValue.class)) {
            return true;
        }
        PathVariable path = parameter.getParameterAnnotation(PathVariable.class);
        RequestParam param = parameter.getParameterAnnotation(RequestParam.class);
        RequestPart part = parameter.getParameterAnnotation(RequestPart.class);
        MatrixVariable matrix = parameter.getParameterAnnotation(MatrixVariable.class);
        return (path != null && isSensitiveField(path.name()))
                || (param != null && isSensitiveField(param.name()))
                || (part != null && isSensitiveField(part.name()))
                || (matrix != null && isSensitiveField(matrix.name()));
    }

    private boolean isSensitiveField(String name) {
        return HIDDEN_FIELDS.contains(name.replace("_", "").replace("-", "").toLowerCase(Locale.ROOT));
    }

    private boolean maskPath(JsonNode node, String[] path, int index) {
        if (node.isNull()) {
            return true;
        }
        if (node instanceof ArrayNode array) {
            for (JsonNode item : array) {
                if (!maskPath(item, path, index)) {
                    return false;
                }
            }
            return true;
        }
        if (node instanceof ObjectNode object && object.has(path[index])) {
            if (index == path.length - 1) {
                object.put(path[index], MASK);
                return true;
            }
            return maskPath(object.get(path[index]), path, index + 1);
        }
        return false;
    }
}
