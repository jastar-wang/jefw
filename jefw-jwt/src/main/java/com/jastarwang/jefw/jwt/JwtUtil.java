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
package com.jastarwang.jefw.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.lang.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;

/**
 * Jwt工具类
 * <p>用来生成和解析token，底层使用 Les Hazlewood 的 jjwt 类库实现</p>
 * <pre>
 *     token数据结构参考如下：
 *     - header
 *     - payload（claims）
 *          - iss (issuer)：签发人
 *          - exp (expiration time)：过期时间
 *          - sub (subject)：主题
 *          - aud (audience)：受众
 *          - nbf (Not Before)：生效时间
 *          - iat (Issued At)：签发时间
 *          - jti (JWT ID)：编号
 *     - signature
 * </pre>
 *
 * @author Jastar Wang
 * @date 2020/8/7
 * @since 1.0
 */
public class JwtUtil {

    private static JwtProperties jwtProperties;

    @Resource
    private JwtProperties jwtPropertiesBean;

    @PostConstruct
    private synchronized void init() {
        JwtUtil.jwtProperties = jwtPropertiesBean;
    }

    /**
     * [生成]获取 jwt 构造器
     * <p>用于灵活的自定义配置</p>
     *
     * @return 构造器（已对“签发时间”、“过期时间”、“加密方式”进行了初始化）
     */
    public static JwtBuilder builder() {
        return builder(null, null, null);
    }

    /**
     * [生成]获取 jwt 构造器（使用参数配置）
     * <p>用于灵活的自定义配置</p>
     *
     * @param secret 加密密钥，为空使用默认配置
     * @param expiration 过期时间（单位：秒），为空使用默认配置
     * @param algorithm 算法，详见{@link SignatureAlgorithm}，为空使用默认配置
     * @return 构造器（已对“签发时间”、“过期时间”、“加密方式”进行了初始化）
     */
    public static JwtBuilder builder(String secret, Long expiration, SignatureAlgorithm algorithm) {
        secret = Optional.ofNullable(secret).orElse(jwtProperties.getSecret());
        expiration = Optional.ofNullable(expiration).orElse(jwtProperties.getExpiration());
        algorithm = Optional.ofNullable(algorithm).orElse(jwtProperties.getAlgorithm());

        Date createTime = new Date();
        Date expiredTime = new Date(createTime.getTime() + expiration * 1000);
        return Jwts.builder().setIssuedAt(createTime).setExpiration(expiredTime).signWith(algorithm, secret);
    }

    /**
     * [生成]使用 subject 快速生成token
     *
     * @param subject 一般使用用户唯一标识等数据，非空
     * @return token
     */
    public static String buildToken(String subject) {
        return buildToken(subject, null, null, null);
    }

    /**
     * [生成]使用 subject 快速生成token（使用参数配置）
     *
     * @param subject 一般使用用户唯一标识等数据，非空
     * @param secret 加密密钥，为空使用默认配置
     * @param expiration 过期时间（单位：秒），为空使用默认配置
     * @param algorithm 算法，详见{@link SignatureAlgorithm}，为空使用默认配置
     * @return token
     */
    public static String buildToken(String subject, String secret, Long expiration, SignatureAlgorithm algorithm) {
        Assert.notNull(subject, "subject can't be null");
        return builder(secret, expiration, algorithm).setSubject(subject).compact();
    }

    /**
     * [解析]获取 jwt 解析器
     * <p>用于灵活的自定义配置</p>
     *
     * @return 解析器（已使用配置的“加密密钥”进行了初始化）
     */
    public static JwtParser parser() {
        return parser(jwtProperties.getSecret());
    }

    /**
     * [解析]获取 jwt 解析器
     * <p>用于灵活的自定义配置</p>
     *
     * @param secret 指定的加密密钥，非空
     * @return 解析器（已使用指定的“加密密钥”进行了初始化）
     */
    public static JwtParser parser(String secret) {
        return Jwts.parser().setSigningKey(secret);
    }

    /**
     * [解析]根据 token 解析 claims
     * <p>用于灵活的取数据</p>
     *
     * @param token token
     * @return 核心Claims
     * @throws ExpiredJwtException 已过期
     * @throws UnsupportedJwtException 格式不支持
     * @throws MalformedJwtException 结构不正确
     * @throws SignatureException 签名不正确
     * @throws IllegalArgumentException 参数不正确
     */
    public static Claims parseClaims(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        return parseClaims(token, jwtProperties.getSecret());
    }

    /**
     * [解析]根据 token 解析 claims
     * <p>用于灵活的取数据</p>
     *
     * @param token token
     * @param secret 指定的加密密钥，非空
     * @return 核心Claims
     * @throws ExpiredJwtException 已过期
     * @throws UnsupportedJwtException 格式不支持
     * @throws MalformedJwtException 结构不正确
     * @throws SignatureException 签名不正确
     * @throws IllegalArgumentException 参数不正确
     */
    public static Claims parseClaims(String token, String secret) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        return parser(secret).parseClaimsJws(token).getBody();
    }

    /**
     * [解析] 快速解析token里的subject
     *
     * @param token token
     * @return claims中的subject数据，异常返回null
     */
    public static String parseSubject(String token) {
        return parseSubject(token, jwtProperties.getSecret());
    }

    /**
     * [解析] 快速解析token里的subject
     *
     * @param token token
     * @param secret 指定的加密密钥，非空
     * @return claims中的subject数据，异常返回null
     */
    public static String parseSubject(String token, String secret) {
        try {
            return parseClaims(token, secret).getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * [解析] 解析token里的expiration
     *
     * @param token token
     * @return claims中的expiration数据，异常返回null
     */
    public static Date parseExpiration(String token) {
        return parseExpiration(token, jwtProperties.getSecret());
    }

    /**
     * [解析] 解析token里的expiration
     *
     * @param token token
     * @param secret 指定的加密密钥，非空
     * @return claims中的expiration数据，异常返回null
     */
    public static Date parseExpiration(String token, String secret) {
        try {
            return parseClaims(token, secret).getExpiration();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * [解析]检测token是否已过期
     *
     * @param token token
     * @return true表示已过期，false表示未过期，token解析不正确或其他异常将会返回null
     */
    public static Boolean isExpired(String token) {
        return isExpired(token, jwtProperties.getSecret());
    }

    /**
     * [解析]检测token是否已过期
     *
     * @param token token
     * @param secret 指定的加密密钥，非空
     * @return true表示已过期，false表示未过期，token解析不正确或其他异常将会返回null
     */
    public static Boolean isExpired(String token, String secret) {
        try {
            Date expiration = parseClaims(token, secret).getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return null;
        }
    }

}
