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

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 通过真实 Spring 代理验证窗口防重行为。
 *
 * @author Jastar Wang
 * @since 2.6.0
 */
class RequestRepeatLimitTest {

    private final AtomicInteger calls = new AtomicInteger();
    private final WebApplicationContextRunner runner = new WebApplicationContextRunner()
            .withUserConfiguration(App.class)
            .withBean("controller", Controller.class, () -> new Controller(calls));

    /**
     * 从宿主的 Boot 入口发现模块自动配置。
     *
     * @author Jastar Wang
     * @since 2.6.0
     */
    @EnableAutoConfiguration
    static class App {
    }

    /**
     * 统计宿主方法经过代理的次数，验证应用的 AOP 开关。
     *
     * @author Jastar Wang
     * @since 2.6.0
     */
    @Aspect
    static class HostAspect {
        private final AtomicInteger calls = new AtomicInteger();

        @AfterReturning("execution(* plain()) && bean(controller)")
        public void afterPlain() {
            calls.incrementAndGet();
        }
    }

    @Test
    void hostControlsAutomaticProxying() {
        var host = runner.withBean(HostAspect.class);
        host.run(context -> {
            context.getBean(Controller.class).plain();
            assertThat(context.getBean(HostAspect.class).calls).hasValue(1);
        });
        host.withPropertyValues("spring.aop.auto=false").run(context -> {
            Controller controller = context.getBean(Controller.class);
            controller.plain();
            assertThat(context.getBean(HostAspect.class).calls).hasValue(0);
            assertThat(controller.save("a", "b")).isEqualTo(1);
        });
    }

    @Test
    void missingRedisNeverLetsAnAnnotatedCallThrough() {
        runner.withClassLoader(new FilteredClassLoader("org.springframework.data.redis"))
                .run(context -> {
                    Controller controller = context.getBean(Controller.class);
                    assertThat(controller.plain()).isEqualTo("ok");
                    assertThatIllegalStateException().isThrownBy(() -> controller.save("a", "b"))
                            .withMessageContaining("StringRedisTemplate");
                    assertThat(calls).hasValue(0);
                });
        runner.run(context -> assertThatIllegalStateException()
                .isThrownBy(() -> context.getBean(Controller.class).save("a", "b"))
                .withMessageContaining("StringRedisTemplate"));
    }

    @Test
    void duplicateFailsBeforeBusinessAndKeysDistinguishMethodsValuesAndUsers() {
        StringRedisTemplate template = mock(StringRedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> values = mock(ValueOperations.class);
        Set<String> seen = new HashSet<>();
        when(template.opsForValue()).thenReturn(values);
        when(values.setIfAbsent(startsWith("test:"), eq("true"), eq(9L), eq(TimeUnit.SECONDS)))
                .thenAnswer(call -> seen.add(call.getArgument(0)));
        AtomicInteger user = new AtomicInteger(1);

        runner.withBean(StringRedisTemplate.class, () -> template)
                .withBean(SessionUserProvider.class, () -> user::get)
                .run(context -> {
                    assertThat(context).doesNotHaveBean("requestLogAspect");
                    Controller controller = context.getBean(Controller.class);
                    assertThat(controller.save("a_b", "c")).isEqualTo(1);
                    assertThatThrownBy(() -> controller.save("a_b", "c"))
                            .isInstanceOf(DuplicateRequestException.class).hasMessage("重复");
                    assertThat(controller.save("a", "b_c")).isEqualTo(2);
                    assertThat(controller.save(null, "")).isEqualTo(3);
                    assertThat(controller.save("null", "")).isEqualTo(4);
                    assertThat(controller.update("a_b", "c")).isEqualTo(5);
                    controller.nested(new Input(new Details("1")));
                    controller.nested(new Input(new Details("2")));
                    controller.nested(new Input(null));
                    assertThatThrownBy(() -> controller.nested(null)).isInstanceOf(DuplicateRequestException.class);
                    user.incrementAndGet();
                    assertThatCode(() -> controller.nested(null)).doesNotThrowAnyException();
                });
    }

    @Test
    void invalidConfigurationIsVisibleEvenWhenArgumentIsNull() {
        runner.withBean(StringRedisTemplate.class, () -> mock(StringRedisTemplate.class))
                .run(context -> {
                    Controller controller = context.getBean(Controller.class);
                    assertThatIllegalArgumentException().isThrownBy(() -> controller.badRoot(null))
                            .withMessageContaining("missing");
                    assertThatIllegalArgumentException().isThrownBy(() -> controller.badMember(null))
                            .withMessageContaining("input.details.missing");
                    assertThatIllegalArgumentException().isThrownBy(controller::noFields)
                            .withMessageContaining("fields");
                    assertThatIllegalStateException().isThrownBy(() -> controller.nested(null))
                            .withMessageContaining("SessionUserProvider");
                });
        runner.withBean(StringRedisTemplate.class, () -> mock(StringRedisTemplate.class))
                .withBean(SessionUserProvider.class, () -> () -> null)
                .run(context -> assertThatIllegalStateException()
                        .isThrownBy(() -> context.getBean(Controller.class).nested(null))
                        .withMessageContaining("null"));
    }

    @Test
    void indeterminateRedisResultStopsBusiness() {
        StringRedisTemplate template = mock(StringRedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> values = mock(ValueOperations.class);
        when(template.opsForValue()).thenReturn(values);
        when(values.setIfAbsent(anyString(), anyString(), anyLong(), any())).thenReturn(null);
        runner.withBean(StringRedisTemplate.class, () -> template).run(context -> {
            assertThatIllegalStateException().isThrownBy(() -> context.getBean(Controller.class).save("a", "b"))
                    .withMessageContaining("Redis");
            assertThat(calls).hasValue(0);
        });
    }

    /**
     * 携带嵌套标识的防重复提交测试请求。
     *
     * @author Jastar Wang
     * @since 2.6.0
     */
    public record Input(Details details) {
    }

    /**
     * 提供生成防重键时读取的标识。
     *
     * @author Jastar Wang
     * @since 2.6.0
     */
    public record Details(String id) {
    }

    /**
     * 记录实际执行次数，供测试区分放行和拦截的请求。
     *
     * @author Jastar Wang
     * @since 2.6.0
     */
    public static class Controller {
        private final AtomicInteger calls;

        public Controller(AtomicInteger calls) {
            this.calls = calls;
        }

        public String plain() {
            return "ok";
        }

        @RequestRepeatLimit(fields = {"first", "second"}, timeout = 9, tips = "重复", uniqueKeyPrefix = "test:")
        public int save(String first, String second) {
            return calls.incrementAndGet();
        }

        @RequestRepeatLimit(fields = {"first", "second"}, timeout = 9, uniqueKeyPrefix = "test:")
        public int update(String first, String second) {
            return calls.incrementAndGet();
        }

        @RequestRepeatLimit(fields = "input.details.id", timeout = 9, uniqueKeyPrefix = "test:", appendSessionUserId = true)
        public void nested(Input input) {
        }

        @RequestRepeatLimit(fields = "missing")
        public void badRoot(Input input) {
        }

        @RequestRepeatLimit(fields = "input.details.missing")
        public void badMember(Input input) {
        }

        @RequestRepeatLimit(fields = {})
        public void noFields() {
        }
    }

}
