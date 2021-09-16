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
package com.jastarwang.jefw.oss.enums;

/**
 * 对象存储权限枚举
 *
 * @author Jastar Wang
 * @date 2021/9/15
 * @since 1.2.0
 */
public enum OssAuthEnum {

    /**
     * 默认，跟随平台自身或继承Bucket权限
     */
    DEFAULT,

    /**
     * 私有（只有拥有者和授权者能读写，其他用户无权限）
     */
    PRIVATE,

    /**
     * 公共读（拥有者和授权者能读写，其他用户只能读）
     */
    PUBLIC_READ,

    /**
     * 公共读写（所有用户都有该文件的读写权限，谨慎使用该权限）
     */
    PUBLIC_READ_WRITE,

}
