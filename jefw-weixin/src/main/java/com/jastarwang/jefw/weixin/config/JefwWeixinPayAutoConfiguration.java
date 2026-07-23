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
package com.jastarwang.jefw.weixin.config;

import cn.hutool.core.io.IoUtil;
import com.jastarwang.jefw.weixin.JefwWeixinPayJsapiService;
import com.jastarwang.jefw.weixin.JefwWeixinPayMpService;
import com.jastarwang.jefw.weixin.impl.JefwWeixinPayJsapiServiceImpl;
import com.jastarwang.jefw.weixin.impl.JefwWeixinPayMpServiceImpl;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.InputStream;

/**
 * 微信支付自动配置-主入口
 *
 * @author Jastar Wang
 * @date 2025/4/27
 * @since 2.2.0
 */
@Configuration
@ConditionalOnClass(Config.class)
@ConditionalOnProperty(value = "jefw.weixin.enabled", havingValue = "true")
public class JefwWeixinPayAutoConfiguration {

    @Bean(name = "weixinPayConfig")
    @ConditionalOnMissingBean(Config.class)
    public Config config(JefwWeixinProperties jefwWeixinProperties) {
        JefwWeixinProperties.Pay payProperties = jefwWeixinProperties.getPay();
        // 读取私钥
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(payProperties.getPrivateKeyPath());
        String content = IoUtil.readUtf8(stream);
        // 自动更新证书
        return new RSAAutoCertificateConfig.Builder()
                .merchantId(payProperties.getMchId())
                .privateKey(content)
                .merchantSerialNumber(payProperties.getMchSerialNo())
                .apiV3Key(payProperties.getApiV3Key())
                .build();

        /// 不自动更新证书
        // return new RSAConfig.Builder()
        //         .merchantId(wechatProperties.getMchId())
        //         .privateKeyFromPath(wechatProperties.getPrivateKeyPath())
        //         .merchantSerialNumber(wechatProperties.getMchSerialNo())
        //         .wechatPayCertificatesFromPath("unknown")
        //         .build();

        /// 尚无配置信息时临时解决启动不报错
        // return new Config() {
        //     @Override
        //     public PrivacyEncryptor createEncryptor() {
        //         return null;
        //     }
        //
        //     @Override
        //     public PrivacyDecryptor createDecryptor() {
        //         return null;
        //     }
        //
        //     @Override
        //     public Credential createCredential() {
        //         return null;
        //     }
        //
        //     @Override
        //     public Validator createValidator() {
        //         return null;
        //     }
        //
        //     @Override
        //     public Signer createSigner() {
        //         return null;
        //     }
        // };
    }

    @Primary
    @Bean
    @ConditionalOnMissingBean
    public JefwWeixinPayJsapiService jefwWeixinPayJsapiService(Config config, JefwWeixinProperties jefwWeixinProperties) {
        return new JefwWeixinPayJsapiServiceImpl(config, jefwWeixinProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public JefwWeixinPayMpService jefwWeixinPayMpService(Config config, JefwWeixinProperties jefwWeixinProperties) {
        return new JefwWeixinPayMpServiceImpl(config, jefwWeixinProperties);
    }

}
