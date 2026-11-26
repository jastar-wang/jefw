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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockPart;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 通过 MVC 分派验证日志切面与宿主控制器的交互。
 *
 * @author Jastar Wang
 * @since 2.6.0
 */
@ExtendWith(OutputCaptureExtension.class)
class RequestLogMvcTest {

    private final WebApplicationContextRunner runner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AopAutoConfiguration.class, RequestLogAutoConfiguration.class))
            .withUserConfiguration(App.class);

    @Test
    void disabledByDefault(CapturedOutput output) {
        runner.run(context -> {
            assertThat(context).doesNotHaveBean(RequestLogAspect.class);
            mvc(context).perform(get("/ping")).andExpect(status().isOk());
            assertThat(output.getOut()).doesNotContain("[/ping]");
        });
    }

    @Test
    void masksCopiesAndPreservesControllerResponse(CapturedOutput output) {
        runner.withPropertyValues("jefw.webmvc.request-log.enabled=true").run(context -> {
            String body = mvc(context).perform(post("/users").contentType(APPLICATION_JSON)
                            .content("""
                                    {"password":"original-secret","name":"private-name","accessToken":"access-marker",
                                     "refresh_token":"refresh-marker","Session-Key":"session-marker"}
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.SET_COOKIE, "session=private-cookie"))
                    .andReturn().getResponse().getContentAsString();
            assertThat(body).contains("original-secret", "private-name", "access-marker", "refresh-marker", "session-marker");
            assertThat(output.getOut()).contains("[/users]", "\"password\":\"******\"",
                            "\"name\":\"******\"", "\"access_key\":\"******\"", "\"accessToken\":\"******\"",
                            "\"refresh_token\":\"******\"", "\"Session-Key\":\"******\"", "耗时")
                    .doesNotContain("original-secret", "response-secret", "private-cookie",
                            "access-marker", "refresh-marker", "session-marker");
            mvc(context).perform(get("/quiet")).andExpect(status().isOk());
            assertThat(output.getOut()).doesNotContain("[/quiet]");
            assertThat(context.getBean(Api.class).ping()).isEqualTo("ok");
        });
    }

    private MockMvc mvc(WebApplicationContext context) {
        return MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void logsRequestEntityBodyWithoutCredentials(CapturedOutput output) {
        runner.withPropertyValues("jefw.webmvc.request-log.enabled=true").run(context -> {
            MockMvc mvc = mvc(context);
            for (String path : List.of("/orders", "/request-orders")) {
                mvc.perform(post(path).contentType(APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer private-auth")
                                .header(HttpHeaders.COOKIE, "session=private-session")
                                .content("{\"order\":\"order-42\",\"token\":\"private-body-token\"}"))
                        .andExpect(status().isOk())
                        .andExpect(content().string("ok"));
            }
            assertThat(output.getOut())
                    .contains("[/orders]", "[/request-orders]", "\"order\":\"order-42\"", "\"token\":\"******\"")
                    .doesNotContain("private-auth", "private-session", "private-body-token");
        });
    }

    @Test
    void masksBoundHeadersAndCookiesIncludingInterfaceBindings(CapturedOutput output) {
        runner.withPropertyValues("jefw.webmvc.request-log.enabled=true").run(context -> {
            mvc(context).perform(get("/bindings").header(HttpHeaders.AUTHORIZATION, "private-auth")
                            .header("X-Api-Key", "private-key").cookie(new Cookie("SESSION", "private-session")))
                    .andExpect(status().isOk()).andExpect(content().string("ok"));
            assertThat(output.getOut()).contains("[/bindings]", "\"credential\":\"******\"",
                            "\"session\":\"******\"", "\"headers\":\"******\"")
                    .doesNotContain("private-auth", "private-key", "private-session");
        });
    }

    @Test
    void masksSensitiveRequestParamByBindingName(CapturedOutput output) {
        runner.withPropertyValues("jefw.webmvc.request-log.enabled=true").run(context -> {
            mvc(context).perform(post("/login").param("password", "private-password").param("client", "web"))
                    .andExpect(status().isOk()).andExpect(content().string("ok"));
            assertThat(output.getOut()).contains("[/login]", "\"credential\":\"******\"", "\"client\":\"web\"")
                    .doesNotContain("private-password");
        });
    }

    @Test
    void masksTextPartByBindingName(CapturedOutput output) {
        runner.withPropertyValues("jefw.webmvc.request-log.enabled=true").run(context -> {
            mvc(context).perform(multipart("/parts")
                            .part(new MockPart("token", "part-marker".getBytes(StandardCharsets.UTF_8)),
                                    new MockPart("client", "web".getBytes(StandardCharsets.UTF_8))))
                    .andExpect(status().isOk()).andExpect(content().string("ok"));
            assertThat(output.getOut()).contains("[/parts]", "\"credential\":\"******\"", "\"client\":\"web\"")
                    .doesNotContain("part-marker");
        });
    }

    @Test
    void masksMatrixVariableByBindingName(CapturedOutput output) {
        runner.withPropertyValues("jefw.webmvc.request-log.enabled=true").run(context -> {
            mvc(context).perform(get("/matrix/item;secret-key=matrix-marker;client=web"))
                    .andExpect(status().isOk()).andExpect(content().string("ok"));
            assertThat(output.getOut()).contains("[/matrix/{item}]", "\"credential\":\"******\"", "\"client\":\"web\"")
                    .doesNotContain("matrix-marker");
        });
    }

    @Test
    void logsRouteTemplateAndMasksSensitivePathArguments(CapturedOutput output) {
        runner.withPropertyValues("jefw.webmvc.request-log.enabled=true").run(context -> {
            mvc(context).perform(get("/reset/private-token/private-account"))
                    .andExpect(status().isOk()).andExpect(content().string("ok"));
            assertThat(output.getOut()).contains("[/reset/{token}/{account}]", "\"code\":\"******\"",
                            "\"account\":\"******\"")
                    .doesNotContain("private-token", "private-account");
        });
    }

    @Test
    void loggingDoesNotReadUploadOrDownload(CapturedOutput output) {
        runner.withPropertyValues("jefw.webmvc.request-log.enabled=true").run(context -> {
            MockMvc mvc = mvc(context);
            MockMultipartFile file = new MockMultipartFile("file", new byte[]{1}) {
                @Override
                public byte[] getBytes() {
                    throw new AssertionError("日志读取了上传内容");
                }

                @Override
                public InputStream getInputStream() {
                    throw new AssertionError("日志打开了上传文件");
                }
            };
            mvc.perform(multipart("/upload").file(file)).andExpect(content().string("ok"));
            mvc.perform(get("/download")).andExpect(content().bytes(new byte[]{65}));
            assertThat(context.getBean(Api.class).downloadReads()).isEqualTo(1);
            assertThat(output.getOut()).contains("\"file\":\"skipped\"", "[/download]", "响应: \"skipped\"");
        });
    }

    @Test
    void hostHandlesControllerErrorsAndLogSerializationErrorsLeaveResponseIntact(CapturedOutput output) {
        runner.withPropertyValues("jefw.webmvc.request-log.enabled=true").run(context -> {
            MockMvc mvc = mvc(context);
            mvc.perform(get("/fail")).andExpect(status().isBadRequest()).andExpect(content().string("host error"));
            mvc.perform(get("/broken").param("label", "ok")).andExpect(content().string("ok"));
            mvc.perform(get("/v3/api-docs/test")).andExpect(content().string("ok"));
            assertThat(output.getOut()).contains("[/fail]", "[异常]java.lang.IllegalStateException", "打印请求日志时异常")
                    .doesNotContain("[/v3/api-docs/test]", "仅日志序列化失败", "private-exception-value");
        });
    }

    @Test
    void brokenRequestMetadataCannotReplaceBusinessResultOrException(CapturedOutput output) {
        runner.withPropertyValues("jefw.webmvc.request-log.enabled=true").run(context -> {
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest() {
                @Override
                public String getRequestURI() {
                    throw new IllegalArgumentException("private request data");
                }
            }));
            try {
                Api api = context.getBean(Api.class);
                assertThat(api.ping()).isEqualTo("ok");
                assertThatThrownBy(api::fail).isInstanceOf(IllegalStateException.class).hasMessage("invalid token private-exception-value");
                assertThat(output.getOut()).doesNotContain("private request data");
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        });
    }

    @Test
    void unresolvedMaskPathsOmitArguments(CapturedOutput output) {
        runner.withPropertyValues("jefw.webmvc.request-log.enabled=true").run(context -> {
            MockMvc mvc = mvc(context);
            for (String uri : List.of("/missing-root", "/missing-field")) {
                mvc.perform(post(uri).contentType(APPLICATION_JSON).content("{\"phone_number\":\"private-phone\"}"))
                        .andExpect(content().string("ok"));
            }
            assertThat(output.getOut()).contains("[/missing-root]", "[/missing-field]", "skipped")
                    .doesNotContain("private-phone");
        });
    }

    /**
     * 加载 MVC 请求日志配置的测试应用。
     *
     * @author Jastar Wang
     * @since 2.6.0
     */
    @Configuration(proxyBeanMethods = false)
    @EnableWebMvc
    static class App {
        @Bean
        Api api() {
            return new Api();
        }

        @Bean
        ObjectMapper objectMapper() {
            return JsonMapper.builder().propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE).build();
        }
    }

    /**
     * 在接口上声明请求头和 Cookie 绑定，验证实现方法沿用 MVC 的参数注解。
     *
     * @author Jastar Wang
     * @since 2.6.0
     */
    interface HeaderBindings {
        @GetMapping("/bindings")
        String bindings(@RequestHeader(HttpHeaders.AUTHORIZATION) String credential,
                        @CookieValue("SESSION") String session, @RequestHeader HttpHeaders headers);
    }

    /**
     * 提供请求日志测试所需的 MVC 接口。
     *
     * @author Jastar Wang
     * @since 2.6.0
     */
    @RestController
    static class Api implements HeaderBindings {
        private final AtomicInteger reads = new AtomicInteger();

        int downloadReads() {
            return reads.get();
        }

        @Override
        public String bindings(String credential, String session, HttpHeaders headers) {
            assertThat(credential).isEqualTo("private-auth");
            assertThat(session).isEqualTo("private-session");
            assertThat(headers.getFirst("X-Api-Key")).isEqualTo("private-key");
            return "ok";
        }

        @PostMapping("/login")
        String login(@RequestParam("password") String credential, @RequestParam String client) {
            assertThat(credential).isEqualTo("private-password");
            return "ok";
        }

        @GetMapping("/reset/{token}/{account}")
        @RequestLog(hiddenParams = "account")
        String reset(@PathVariable("token") String code, @PathVariable String account) {
            assertThat(code).isEqualTo("private-token");
            assertThat(account).isEqualTo("private-account");
            return "ok";
        }

        @PostMapping("/parts")
        String parts(@RequestPart("token") String credential, @RequestPart("client") String client) {
            assertThat(credential).isEqualTo("part-marker");
            return "ok";
        }

        @GetMapping("/matrix/{item}")
        String matrix(@MatrixVariable(name = "secret-key") String credential, @MatrixVariable("client") String client) {
            assertThat(credential).isEqualTo("matrix-marker");
            return "ok";
        }

        @GetMapping({"/ping", "/v3/api-docs/test"})
        String ping() {
            return "ok";
        }

        @GetMapping("/quiet")
        @RequestLog(print = false)
        String quiet() {
            return "quiet";
        }

        @PostMapping("/users")
        @RequestLog(hiddenParams = "user.name")
        ResponseEntity<List<Map<String, Object>>> echo(@RequestBody Map<String, String> user) {
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, "session=private-cookie").body(List.of(Map.of("user", user, "key", new Keys("response-secret"))));
        }

        @PostMapping("/upload")
        String upload(@RequestParam("file") MultipartFile file) {
            return "ok";
        }

        @PostMapping("/orders")
        String createOrder(HttpEntity<Map<String, String>> request) {
            assertThat(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer private-auth");
            assertThat(request.getHeaders().getFirst(HttpHeaders.COOKIE)).isEqualTo("session=private-session");
            assertThat(request.getBody()).containsEntry("token", "private-body-token");
            return "ok";
        }

        @PostMapping("/request-orders")
        String createRequestOrder(RequestEntity<Map<String, String>> request) {
            return createOrder(request);
        }

        @GetMapping("/download")
        ResponseEntity<ByteArrayResource> download() {
            return ResponseEntity.ok(new ByteArrayResource(new byte[]{65}) {
                @Override
                public InputStream getInputStream() throws IOException {
                    reads.incrementAndGet();
                    return super.getInputStream();
                }
            });
        }

        @GetMapping("/fail")
        String fail() {
            throw new IllegalStateException("invalid token private-exception-value");
        }

        @PostMapping("/missing-root")
        @RequestLog(hiddenParams = "other.phone_number")
        String missingRoot(@RequestBody Map<String, Object> user) {
            return "ok";
        }

        @PostMapping("/missing-field")
        @RequestLog(hiddenParams = "user.phoneNumber")
        String missingField(@RequestBody Map<String, Object> user) {
            return "ok";
        }

        @ExceptionHandler(IllegalStateException.class)
        ResponseEntity<String> handle() {
            return ResponseEntity.badRequest().body("host error");
        }

        @GetMapping("/broken")
        String broken(BadLogArgument arg) {
            return arg.label();
        }
    }

    /**
     * 读取属性时抛出异常的参数，用于模拟日志序列化失败。
     *
     * @author Jastar Wang
     * @since 2.6.0
     */
    record BadLogArgument(String label) {
        public String getBroken() {
            throw new AssertionError("仅日志序列化失败");
        }
    }

    /**
     * 携带访问密钥的测试参数，用于验证日志脱敏。
     *
     * @author Jastar Wang
     * @since 2.6.0
     */
    record Keys(String accessKey) {
    }

}
