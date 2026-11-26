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
package com.jastarwang.jefw.mail;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * 验证独立邮件构件的自动装配、消息内容和调用失败行为。
 *
 * @author Jastar Wang
 * @since 2.6.0
 */
class MailServiceTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner().withUserConfiguration(App.class);
    private final ApplicationContextRunner configured = runner.withPropertyValues(
            "spring.mail.host=smtp.example.com", "spring.mail.username=from@example.com", "spring.mail.password=test");

    /**
     * 加载邮件服务自动配置的测试应用。
     *
     * @author Jastar Wang
     * @since 2.6.0
     */
    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    static class App {
    }

    @Test
    void reportsMissingConfigurationOnlyWhenSending() {
        runner.run(context -> {
            assertThat(context).doesNotHaveBean(JavaMailSender.class);
            assertThatThrownBy(() -> context.getBean(MailService.class).sendText("to@example.com", "主题", "正文"))
                    .isInstanceOf(MailPreparationException.class).hasMessageContaining("spring.mail.host");
        });
    }

    @Test
    void onlyJefwTransportRequiresVerifiedTlsByDefault() {
        assertIsolatedSmtpTransport(configured);
    }

    @Test
    void smtpFallbackRequiresVerifiedTlsWhenJndiIsUnavailable() {
        try (var jndi = mockStatic(JndiLocatorDelegate.class)) {
            jndi.when(JndiLocatorDelegate::isDefaultJndiEnvironmentAvailable).thenReturn(false);
            assertIsolatedSmtpTransport(configured.withPropertyValues("spring.mail.jndi-name=mail/session"));
        }
    }

    private void assertIsolatedSmtpTransport(ApplicationContextRunner smtp) {
        smtp.withPropertyValues("spring.mail.properties[mail.smtp.timeout]=8000").run(context -> {
            var host = context.getBean(JavaMailSenderImpl.class);
            var transport = ArgumentCaptor.forClass(Properties.class);
            try (var sessions = mockStatic(Session.class)) {
                // 在创建传输会话时捕获实际发信参数，避免连接 SMTP 服务。
                var stopped = new MailPreparationException("captured session");
                sessions.when(() -> Session.getInstance(any(Properties.class))).thenThrow(stopped);
                assertThatThrownBy(() -> context.getBean(MailService.class).sendText("to@example.com", "主题", "正文"))
                        .isSameAs(stopped);
                sessions.verify(() -> Session.getInstance(transport.capture()));
            }
            assertThat(host.getJavaMailProperties()).containsEntry("mail.smtp.timeout", "8000")
                    .doesNotContainKeys("mail.smtp.auth", "mail.smtp.starttls.required",
                            "mail.smtp.starttls.enable", "mail.smtp.ssl.checkserveridentity", "mail.smtps.auth",
                            "mail.smtps.ssl.checkserveridentity");
            assertThat(transport.getValue()).containsEntry("mail.smtp.timeout", "8000")
                    .containsEntry("mail.smtp.starttls.required", "true")
                    .containsEntry("mail.smtp.starttls.enable", "true")
                    .containsEntry("mail.smtp.ssl.checkserveridentity", "true")
                    .containsEntry("mail.smtps.ssl.checkserveridentity", "true")
                    .doesNotContainKey("mail.smtp.ssl.trust").doesNotContainKey("mail.smtps.ssl.trust");
        });
    }

    @Test
    void coexistsWithHostMailService() {
        runner.withAllowBeanDefinitionOverriding(false).withBean("mailService", String.class, () -> "host")
                .run(context -> {
                    assertThat(context.getBean("mailService")).isEqualTo("host");
                    assertThat(context).hasSingleBean(MailService.class);
                });
    }

    @Test
    void usesHostSenderWithOnlyAFromAddress() {
        RecordingSender sender = new RecordingSender();
        runner.withPropertyValues("jefw.mail.from=notify@example.com")
                .withBean(JavaMailSender.class, () -> sender).run(context -> {
                    context.getBean(MailService.class).sendText("to@example.com", "通知", "正文");
                    assertThat(sender.message.getFrom()[0].toString()).isEqualTo("notify@example.com");
                });
    }

    @Test
    void usesJndiSessionTransportWithoutBootCredentials() {
        Properties settings = new Properties();
        settings.setProperty("mail.transport.protocol", "test-transport");
        Session session = spy(Session.getInstance(settings));
        try (var jndi = mockStatic(JndiLocatorDelegate.class)) {
            jndi.when(JndiLocatorDelegate::isDefaultJndiEnvironmentAvailable).thenReturn(true);
            runner.withPropertyValues("spring.mail.jndi-name=mail/session", "spring.mail.host=smtp.example.com",
                            "jefw.mail.from=notify@example.com")
                    .withBean(Session.class, () -> session).run(context -> {
                        assertThatThrownBy(() -> context.getBean(MailService.class).sendText("to@example.com", "通知", "正文"))
                                .isInstanceOf(MailSendException.class)
                                .hasRootCauseInstanceOf(jakarta.mail.NoSuchProviderException.class);
                        verify(session).getTransport("test-transport");
                    });
        }
    }

    @Test
    void hostSenderRequiresAFromAddress() {
        RecordingSender sender = new RecordingSender();
        runner.withBean(JavaMailSender.class, () -> sender).run(context -> {
            assertThatThrownBy(() -> context.getBean(MailService.class).sendText("to@example.com", "通知", "正文"))
                    .isInstanceOf(MailPreparationException.class).hasMessageContaining("jefw.mail.from");
            assertThat(sender.message).isNull();
        });
    }

    @Test
    void bootSmtpSenderStillRequiresCredentials() {
        runner.withPropertyValues("spring.mail.host=smtp.example.com", "jefw.mail.from=notify@example.com")
                .run(context -> assertThatThrownBy(() -> context.getBean(MailService.class)
                        .sendText("to@example.com", "通知", "正文"))
                        .isInstanceOf(MailPreparationException.class).hasMessageContaining("spring.mail.password"));
    }

    @Test
    void sendsFromMailboxWhenLoginIsAnAccountId() {
        RecordingSender sender = new RecordingSender();
        configured.withPropertyValues("spring.mail.username=account-id", "jefw.mail.from=notify@example.com")
                .withBean(JavaMailSender.class, () -> sender).run(context -> {
                    context.getBean(MailService.class).sendText("to@example.com", "通知", "正文");
                    assertThat(sender.message.getFrom()[0].toString()).isEqualTo("notify@example.com");
                });
    }

    @Test
    void sendsTextAndHtmlWithoutTemplateEngine() {
        RecordingSender sender = new RecordingSender();
        configured.withBean(JavaMailSender.class, () -> sender).run(context -> {
            MailService service = context.getBean(MailService.class);
            service.sendText("to@example.com", "主题", "正文");
            sender.message.saveChanges();
            assertThat(sender.message.getFrom()[0].toString()).isEqualTo("from@example.com");
            assertThat(sender.message.getAllRecipients()[0].toString()).isEqualTo("to@example.com");
            assertThat(sender.message.getSubject()).isEqualTo("主题");
            assertThat(sender.message.isMimeType("text/plain")).isTrue();
            assertThat(sender.message.getContent()).isEqualTo("正文");
            service.sendHtml("to@example.com", "通知", "<b>正文</b>");
            sender.message.saveChanges();
            assertThat(sender.message.isMimeType("text/html")).isTrue();
            assertThat(sender.message.getContent()).isEqualTo("<b>正文</b>");
        });
    }

    @Test
    void propagatesDeliveryFailure() {
        JavaMailSender sender = mock(JavaMailSender.class);
        MailSendException failure = new MailSendException("SMTP unavailable");
        doThrow(failure).when(sender).send(any(MimeMessagePreparator.class));
        configured.withBean(JavaMailSender.class, () -> sender).run(context ->
                assertThatThrownBy(() -> context.getBean(MailService.class).sendText("to@example.com", "主题", "正文"))
                        .isSameAs(failure));
    }

    /**
     * 保留生成的 MIME 邮件以便检查，替代 SMTP 传输。
     *
     * @author Jastar Wang
     * @since 2.6.0
     */
    static class RecordingSender extends JavaMailSenderImpl {
        private MimeMessage message;

        @Override
        protected void doSend(MimeMessage[] messages, Object[] originals) {
            message = messages[0];
        }
    }

}
