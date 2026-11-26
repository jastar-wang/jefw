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
package com.jastarwang.jefw.sms.test;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jastarwang.jefw.sms.JefwSmsException;
import com.jastarwang.jefw.sms.config.JefwSmsProperties;
import com.jastarwang.jefw.sms.impl.AliyunJefwSmsService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 离线验证阿里云短信请求和异常转换。
 *
 * @author Jastar Wang
 * @since 2.5.1
 */
class AliyunJefwSmsServiceTest {

    private final Client client = mock(Client.class);
    private final JefwSmsProperties.Aliyun config = new JefwSmsProperties.Aliyun();
    private final AliyunJefwSmsService service = new AliyunJefwSmsService(config, client);

    @Test
    void sendsTemplateCodeAndNamedParametersToAllRecipients() throws Exception {
        config.setSignName("测试签名");
        when(client.sendSms(any())).thenReturn(new SendSmsResponse().setBody(new SendSmsResponseBody().setCode("OK")));
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("code", "0\"12");
        params.put("time", 3);
        params.put("date", LocalDate.of(2026, 11, 24));

        service.send("13800000001,13800000002", "SMS_123", params);

        ArgumentCaptor<SendSmsRequest> captor = ArgumentCaptor.forClass(SendSmsRequest.class);
        verify(client).sendSms(captor.capture());
        SendSmsRequest request = captor.getValue();
        assertThat(request.getPhoneNumbers()).isEqualTo("13800000001,13800000002");
        assertThat(request.getTemplateCode()).isEqualTo("SMS_123");
        assertThat(request.getSignName()).isEqualTo("测试签名");
        ObjectMapper mapper = new ObjectMapper();
        assertThat(mapper.readTree(request.getTemplateParam())).isEqualTo(
                mapper.valueToTree(Map.of("code", "0\"12", "time", "3", "date", "2026-11-24")));
    }

    @Test
    void reportsRejectedRequestAndOmitsAbsentParameters() throws Exception {
        SendSmsResponse response = new SendSmsResponse().setBody(new SendSmsResponseBody().setCode("isv.BUSINESS_LIMIT_CONTROL"));
        when(client.sendSms(any())).thenReturn(response);

        JefwSmsException failure = catchThrowableOfType(JefwSmsException.class,
                () -> service.send("13800000001,13800000002", "SMS_123", null));

        assertThat(failure.getOriginResponse()).isSameAs(response);
        assertThat(failure.getFailedPhones()).containsExactly("13800000001", "13800000002");
        ArgumentCaptor<SendSmsRequest> captor = ArgumentCaptor.forClass(SendSmsRequest.class);
        verify(client).sendSms(captor.capture());
        assertThat(captor.getValue().getTemplateParam()).isNull();
    }

    @Test
    void retainsSdkFailureAndOriginalParameters() throws Exception {
        IOException cause = new IOException("connection failed");
        when(client.sendSms(any())).thenThrow(cause);
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        JefwSmsException failure = catchThrowableOfType(JefwSmsException.class,
                () -> service.send("13800000001", "SMS_123", params));
        assertThat(failure).hasCause(cause);
        assertThat(failure.getOriginParam()).isSameAs(params);
    }
}
