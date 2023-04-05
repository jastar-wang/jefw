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
package com.jastarwang.jefw.util.extra.ip;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * IP工具类
 * <p>包含反向解析IP所在地理位置的方法</p>
 *
 * @author Jastar Wang
 * @date 2020/9/18
 * @since 1.0
 */
public class IpUtil {

    private static final String API_URL = "http://ip.taobao.com/outGetIpInfo";
    private static final String API_ACCESS_KEY = "alibaba-inc";

    private IpUtil() {
    }

    /**
     * 根据ip地址反向解析ip所在的地理位置<br>
     * <b>
     * 注意：<br>
     * 1、目前底层使用淘宝的接口查询，不会返回区县的相关信息<br>
     * 2、不要过度依赖本方法，基于的第三方API存在随时不可用的风险，遇到问题请及时反馈，以便更换新的API
     * </b>
     *
     * @param ip 要查询的ip地址
     * @return IP地理位置信息，解析出错时返回null
     */
    public static IpInfo getIpInfo(String ip) {
        if (StrUtil.isBlank(ip)) {
            return null;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("ip", ip);
        param.put("accessKey", API_ACCESS_KEY);
        HttpRequest post = HttpUtil.createPost(API_URL).form(param);
        post.header(Header.USER_AGENT, StrUtil.removeSuffix(post.header(Header.USER_AGENT), " Hutool"));
        try {
            String response = post.execute().body();
            JSONObject jsonObject = JSONUtil.parseObj(response);
            Integer code = jsonObject.get("code", Integer.class);
            if (code != null && code == 0) {
                return JSONUtil.toBean(jsonObject.getJSONObject("data"), IpInfo.class);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

}
