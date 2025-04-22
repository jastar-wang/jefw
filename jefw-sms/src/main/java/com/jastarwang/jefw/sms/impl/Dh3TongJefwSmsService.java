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
package com.jastarwang.jefw.sms.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.jastarwang.jefw.sms.JefwSmsService;
import com.jastarwang.jefw.sms.SmsChannel;
import com.jastarwang.jefw.sms.JefwSmsException;
import com.jastarwang.jefw.sms.config.JefwSmsProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信服务实现之大汉三通
 *
 * @author Jastar Wang
 * @date 2023/4/5
 * @since 1.3.0
 */
public class Dh3TongJefwSmsService implements JefwSmsService {

    private final JefwSmsProperties jefwSmsProperties;

    public Dh3TongJefwSmsService(JefwSmsProperties jefwSmsProperties) {
        this.jefwSmsProperties = jefwSmsProperties;
    }

    @Override
    public void init() {
        // do nothing
    }

    @Override
    public void destroy() {
        // do nothing
    }

    @Override
    public Object prototype() {
        return null;
    }

    @Override
    public SmsChannel getChannel() {
        return SmsChannel.DH3TONG;
    }

    @Override
    public void send(String phones, String templateOrId, Map<String, Object> params) {
        Map<String, Object> param = new HashMap<>();
        param.put("account", jefwSmsProperties.getDh3tong().getAccount());
        param.put("password", jefwSmsProperties.getDh3tong().getPwd());
        param.put("phones", phones);
        param.put("content", StrUtil.format(templateOrId, params));

        String paramJson;
        try {
            paramJson = JSONUtil.toJsonStr(param);
            if (paramJson.contains("\\\\n")) {
                paramJson = paramJson.replaceAll("\\\\n", "\n");
            }

            String body = HttpUtil.post(jefwSmsProperties.getDh3tong().getApiUrl(), paramJson);

            Dh3TongResponse response = JSONUtil.toBean(body, Dh3TongResponse.class);
            if (!"0".equals(response.getResult())) {
                throw new JefwSmsException("发送短信后对方响应异常", params, body, StrUtil.split(response.getFailPhones(), ","));
            }
        } catch (Throwable e) {
            throw new JefwSmsException("发送短信出错", e, params);
        }
    }

    public static class Dh3TongResponse {
        /**
         * 状态描述
         */
        private String desc;
        /**
         * 该批短信编号
         */
        private String msgid;
        /**
         * 该批短信提交结果
         * 详见<a href="http://help.dahantc.com/docs/oss/1apkg9ncgtaq9.html">错误码</a>
         */
        private String result;
        /**
         * 如果提交的号码中含有错误（格式）号码将在此显示
         */
        private String failPhones;
        /**
         * 有长链接地址替换时返回该参数，短链接的任务编号
         */
        private String taskid;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getMsgid() {
            return msgid;
        }

        public void setMsgid(String msgid) {
            this.msgid = msgid;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getFailPhones() {
            return failPhones;
        }

        public void setFailPhones(String failPhones) {
            this.failPhones = failPhones;
        }

        public String getTaskid() {
            return taskid;
        }

        public void setTaskid(String taskid) {
            this.taskid = taskid;
        }

        @Override
        public String toString() {
            return "Dh3TongResponse{" +
                    "desc='" + desc + '\'' +
                    ", msgid='" + msgid + '\'' +
                    ", result='" + result + '\'' +
                    ", failPhones='" + failPhones + '\'' +
                    ", taskid='" + taskid + '\'' +
                    '}';
        }
    }
}
