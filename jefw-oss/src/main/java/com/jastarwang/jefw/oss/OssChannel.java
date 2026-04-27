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
package com.jastarwang.jefw.oss;

/**
 * 对象存储渠道（平台）
 *
 * @author Jastar Wang
 * @date 2023/4/5
 * @since 1.3.0
 */
public enum OssChannel {
    /**
     * 本地
     */
    LOCAL,
    /**
     * 阿里云
     */
    ALIYUN,
    /**
     * 腾讯云
     */
    TENCENT,
    /**
     * 七牛云
     */
    QINIU,
    /**
     * MinIO
     */
    MINIO,
    /**
     * 又拍云
     */
    UPYUN
}