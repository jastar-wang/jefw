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
package com.jastarwang.jefw.weixin.model.dev;

import java.io.Serial;
import java.io.Serializable;

/**
 * 一次性订阅消息请求参数（公众平台：公众号+服务号）
 *
 * @author Jastar Wang
 * @date 2025/6/27
 * @since 2.2.0
 */
public class OnceSubscribeMsgOaRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 非空，接口调用凭证
     */
    private String accessToken;
    /**
     * 非空，接收消息的用户openid
     */
    private String toUserOpenid;
    /**
     * 非空，订阅消息模板ID
     */
    private String templateId;
    /**
     * 可空，点击消息跳转网页链接（如：https://example.com）
     * <p>需ICP备案，若同时传递了跳转小程序的参数，则优先跳转小程序</p>
     */
    private String jumpUrl;
    /**
     * 可空，点击消息跳转小程序AppId
     * <p>该参数有值时，jumMpPagePath不能为空</p>
     */
    private String jumpMpAppId;
    /**
     * 可空，点击消息跳转小程序页面路径（如：index?foo=bar）
     * <p>该参数有值时，jumpMpAppId不能为空</p>
     */
    private String jumpMpPagePath;
    /**
     * 非空，自定义订阅场景值
     * <p>范围：0-10000，<b>同一用户在同一scene场景值下的多次授权不累积下发权限，只能下发一条。若要订阅多条，需要不同scene场景值</b></p>
     */
    private String scene;
    /**
     * 非空，消息标题
     * <p>15字以内</p>
     */
    private String title;
    /**
     * 非空，消息内容
     * <p>200字以内</p>
     */
    private String content;
    /**
     * 非空，消息内容颜色（如：#FF0000）
     */
    private String contentColor;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getToUserOpenid() {
        return toUserOpenid;
    }

    public void setToUserOpenid(String toUserOpenid) {
        this.toUserOpenid = toUserOpenid;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public String getJumpMpAppId() {
        return jumpMpAppId;
    }

    public void setJumpMpAppId(String jumpMpAppId) {
        this.jumpMpAppId = jumpMpAppId;
    }

    public String getJumpMpPagePath() {
        return jumpMpPagePath;
    }

    public void setJumpMpPagePath(String jumpMpPagePath) {
        this.jumpMpPagePath = jumpMpPagePath;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentColor() {
        return contentColor;
    }

    public void setContentColor(String contentColor) {
        this.contentColor = contentColor;
    }

    @Override
    public String toString() {
        return "OnceSubscribeMsgOaRequest{" +
                "accessToken='" + accessToken + '\'' +
                ", toUserOpenid='" + toUserOpenid + '\'' +
                ", templateId='" + templateId + '\'' +
                ", jumpUrl='" + jumpUrl + '\'' +
                ", jumpMpAppId='" + jumpMpAppId + '\'' +
                ", jumpMpPagePath='" + jumpMpPagePath + '\'' +
                ", scene='" + scene + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", contentColor='" + contentColor + '\'' +
                '}';
    }

}
