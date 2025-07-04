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
 * 一次性或长期订阅消息请求参数（小程序）
 *
 * @author Jastar Wang
 * @date 2025/7/3
 * @since 2.2.0
 */
public class SubscribeMsgMpRequest implements Serializable {

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
     * 可空，点击消息跳转小程序页面路径（如：index?foo=bar）
     * <p>仅限本小程序内的页面，不填则无跳转</p>
     */
    private String jumpPagePath;
    /**
     * 可空，跳转的小程序版本类型
     * <p>支持developer(开发版)、trial(体验版)、formal(正式版)，默认为正式版</p>
     */
    private String envVersion;
    /**
     * 可空，进入小程序查看的语言类型
     * <p>支持zh_CN(简体中文)、en_US(英文)、zh_HK(繁体中文)、zh_TW(繁体中文)，默认为zh_CN</p>
     */
    private String lang;
    /**
     * 非空，要发送的消息内容（支持 Map 和 Class 对象）
     * <p>
     * 例如，订阅模板定义如下：
     * <pre>
     *  姓名: {{name01.DATA}}
     *  金额: {{amount01.DATA}}
     *  行程: {{thing01.DATA}}
     *  日期: {{date01.DATA}}
     * </pre>
     * </p>
     * <li>当此参数为 Map 时：key为模板中的变量名称(如：name01)，value为要传入的变量值(如：张三)</li>
     * <li>当此参数为 Class 时：字段名称为模板中的变量名称(如：name01)，字段值为要传入的变量值(如：张三)</li>
     */
    private Object data;

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

    public String getJumpPagePath() {
        return jumpPagePath;
    }

    public void setJumpPagePath(String jumpPagePath) {
        this.jumpPagePath = jumpPagePath;
    }

    public String getEnvVersion() {
        return envVersion;
    }

    public void setEnvVersion(String envVersion) {
        this.envVersion = envVersion;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SubscribeMsgMpRequest{" +
                "accessToken='" + accessToken + '\'' +
                ", toUserOpenid='" + toUserOpenid + '\'' +
                ", templateId='" + templateId + '\'' +
                ", jumpPagePath='" + jumpPagePath + '\'' +
                ", envVersion='" + envVersion + '\'' +
                ", lang='" + lang + '\'' +
                ", data=" + data +
                '}';
    }

}
