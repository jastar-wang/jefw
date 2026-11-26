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

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.StringUtils;
import org.springframework.util.function.SingletonSupplier;

import java.util.Properties;

/**
 * 注册通用邮件发送服务和 SMTP 传输默认配置。
 *
 * @author Jastar Wang
 * @date 2026/11/25
 * @since 2.6.0
 */
@AutoConfiguration(after = MailSenderAutoConfiguration.class)
@EnableConfigurationProperties(MailProperties.class)
public class MailAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MailService jefwMailService(MailProperties properties, ObjectProvider<JavaMailSender> senders,
                                       ObjectProvider<MailSenderAutoConfiguration> bootConfiguration,
                                       @Value("${jefw.mail.from:}") String from) {
        return new MailService(properties, from, SingletonSupplier.of(() -> {
            JavaMailSender sender = senders.getIfAvailable();
            // Boot 的 SMTP 配置会给发送器设置 host；JNDI 配置只注入 Session。
            if (sender instanceof JavaMailSenderImpl smtp && bootConfiguration.getIfAvailable() != null
                    && smtp.getHost() != null) {
                return smtpSender(smtp);
            }
            return sender;
        }));
    }

    private JavaMailSenderImpl smtpSender(JavaMailSenderImpl source) {
        if (!StringUtils.hasText(source.getHost()) || !StringUtils.hasText(source.getUsername())
                || !StringUtils.hasText(source.getPassword())) {
            throw new MailPreparationException("请配置 spring.mail.host、spring.mail.username 和 spring.mail.password");
        }
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(source.getHost());
        sender.setPort(source.getPort());
        sender.setProtocol(source.getProtocol());
        sender.setUsername(source.getUsername());
        sender.setPassword(source.getPassword());
        sender.setDefaultEncoding(source.getDefaultEncoding());
        sender.setDefaultFileTypeMap(source.getDefaultFileTypeMap());
        Properties transport = (Properties) source.getJavaMailProperties().clone();
        transport.putIfAbsent("mail.smtp.auth", "true");
        transport.putIfAbsent("mail.smtp.starttls.enable", "true");
        transport.putIfAbsent("mail.smtp.starttls.required", "true");
        transport.putIfAbsent("mail.smtp.ssl.checkserveridentity", "true");
        transport.putIfAbsent("mail.smtps.auth", "true");
        transport.putIfAbsent("mail.smtps.ssl.checkserveridentity", "true");
        sender.setJavaMailProperties(transport);
        return sender;
    }

}
