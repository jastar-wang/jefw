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
package com.jastarwang.jefw.oss.aliyun;

import com.aliyun.oss.OSS;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 阿里云OSS工具类
 *
 * @author Jastar Wang
 * @date 2021/9/14
 * @since 1.2.0
 */
public class AliyunOssUtil {

    private static OSS client;

    @Resource
    private OSS aliyunOssClient;

    @PostConstruct
    private synchronized void init() {
        AliyunOssUtil.client = aliyunOssClient;
    }

}
