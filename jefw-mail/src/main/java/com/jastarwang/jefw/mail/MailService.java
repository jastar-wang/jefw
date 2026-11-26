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

import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;

import java.util.function.Supplier;

/**
 * 通过宿主配置的 SMTP 账号发送文本或 HTML 邮件。
 *
 * @author Jastar Wang
 * @date 2026/11/25
 * @since 2.6.0
 */
public class MailService {

    private final MailProperties properties;
    private final String from;
    private final Supplier<JavaMailSender> sender;

    MailService(MailProperties properties, String from, Supplier<JavaMailSender> sender) {
        this.properties = properties;
        this.from = StringUtils.hasText(from) ? from : properties.getUsername();
        this.sender = sender;
    }

    public void sendText(String to, String subject, String body) {
        send(to, subject, body, false);
    }

    public void sendHtml(String to, String subject, String body) {
        send(to, subject, body, true);
    }

    private void send(String to, String subject, String body, boolean html) {
        JavaMailSender mailSender = sender.get();
        if (mailSender == null) {
            throw new MailPreparationException("缺少 JavaMailSender，请配置 spring.mail.host 或提供邮件发送器");
        }
        if (!StringUtils.hasText(from)) {
            throw new MailPreparationException("请通过 jefw.mail.from 或 spring.mail.username 指定发件地址");
        }
        mailSender.send(message -> {
            MimeMessageHelper helper = new MimeMessageHelper(message, properties.getDefaultEncoding().name());
            helper.setValidateAddresses(true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, html);
        });
    }

}
