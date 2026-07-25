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
package com.jastarwang.jefw.weixin.model.dev;

import com.jastarwang.jefw.weixin.model.BaseResponse;

import java.io.Serial;

/**
 * 网页授权接口调用凭证
 *
 * @author Jastar Wang
 * @date 2025/4/9
 * @since 2.2.0
 */
public class OAuth2AccessTokenResponse extends BaseResponse {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 获取到的凭证
     */
    private String accessToken;
    /**
     * 凭证有效时间（单位：秒）
     */
    private Long expiresIn;
    /**
     * 用户刷新access_token
     */
    private String refreshToken;
    /**
     * 用户唯一标识，请注意，在未关注公众号时，用户访问公众号的网页，也会产生一个用户和公众号唯一的OpenID
     */
    private String openid;
    /**
     * 用户授权的作用域，使用逗号（,）分隔
     */
    private String scope;
    /**
     * 是否为快照页模式虚拟账号，<b>只有当用户是快照页模式虚拟账号时返回，值为1</b>
     */
    private Integer isSnapshotuser;
    /**
     * 用户统一标识（针对一个微信开放平台账号下的应用，同一用户的 unionid 是唯一的），<b>只有当scope为"snsapi_userinfo"时返回</b>
     */
    private String unionid;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Integer getIsSnapshotuser() {
        return isSnapshotuser;
    }

    public void setIsSnapshotuser(Integer isSnapshotuser) {
        this.isSnapshotuser = isSnapshotuser;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    @Override
    public String toString() {
        return "OAuth2AccessTokenResponse{" +
                "accessToken='" + accessToken + '\'' +
                ", expiresIn=" + expiresIn +
                ", refreshToken='" + refreshToken + '\'' +
                ", openid='" + openid + '\'' +
                ", scope='" + scope + '\'' +
                ", isSnapshotuser=" + isSnapshotuser +
                ", unionid='" + unionid + '\'' +
                "} " + super.toString();
    }
    
}