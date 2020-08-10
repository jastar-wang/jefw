/*
 * Copyright (c) 2020 Jastar Wang
 * jefw is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.jastarwang.jefw.util.extra.jwt;

/**
 * Jwt属性配置
 *
 * @author Jastar Wang
 * @date 2020/8/10
 * @since 1.0
 */
public class JwtProperties {

    private String secret;
    private Long expiration;

    /**
     * 获取密钥
     *
     * @return 用于生成token的密钥
     */
    public String getSecret() {
        return secret;
    }

    /**
     * 设置密钥
     *
     * @param secret 用于生成token的密钥
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * 获取过期时间
     *
     * @return token的过期时间（单位：秒）
     */
    public Long getExpiration() {
        return expiration;
    }

    /**
     * 设置过期时间
     *
     * @param expiration token的过期时间（单位：秒）
     */
    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }
}
