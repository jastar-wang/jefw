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

import java.io.File;
import java.time.LocalDateTime;

/**
 * 对象存储服务接口
 * <p><b>注意：对象完整名称不能以斜杠“/”开头</b></p>
 * FIXME 接口待完善
 *
 * @author Jastar Wang
 * @date 2023/2/19
 * @since 1.2.4
 */
public interface OssService {
    /**
     * 自定义初始化操作
     */
    void init();

    /**
     * 自定义销毁操作
     */
    void destroy();

    /**
     * 获取原始客户端
     *
     * @return 原始客户端对象，调用方自行转换类型
     */
    Object prototype();

    /**
     * 检查bucket是否存在
     * <br><b>注意：非本账号下的bucket存在也会返回true</b>
     *
     * @param bucketName bucket名称
     * @return true-存在，false-不存在
     */
    boolean ifBucketExist(String bucketName);

    /**
     * 检查object是否存在
     *
     * @param bucketName bucket名称
     * @param key        object完整名称
     * @return true-存在，false-bucket或object不存在
     */
    boolean ifObjExist(String bucketName, String key);

    /**
     * 设置object访问权限
     *
     * @param bucketName bucket名称
     * @param key        object完整名称
     * @param acl        权限
     */
    void setObjAcl(String bucketName, String key, OssAccessPolicy acl);

    /**
     * 删除object
     *
     * @param bucketName bucket名称
     * @param key        object完整名称
     */
    void delObj(String bucketName, String key);

    /**
     * 上传object
     *
     * @param bucketName bucket名称
     * @param key        object完整名称
     * @param bytes      数据字节数组
     */
    void putObj(String bucketName, String key, byte[] bytes);

    /**
     * 上传object
     *
     * @param bucketName bucket名称
     * @param key        object完整名称
     * @param file       文件
     */
    void putObj(String bucketName, String key, File file);

    /**
     * 上传object并指定权限
     *
     * @param bucketName bucket名称
     * @param key        object完整名称
     * @param bytes      数据字节数组
     * @param acl        权限
     */
    void putObj(String bucketName, String key, byte[] bytes, OssAccessPolicy acl);

    /**
     * 上传object并指定权限
     *
     * @param bucketName bucket名称
     * @param key        object完整名称
     * @param file       文件
     * @param acl        权限
     */
    void putObj(String bucketName, String key, File file, OssAccessPolicy acl);

    /**
     * 下载object
     *
     * @param bucketName bucket名称
     * @param key        object完整名称
     * @return 数据字节数组
     */
    byte[] getObj(String bucketName, String key);

    /**
     * 为非公开的object生成带有时效签名的访问路径
     *
     * @param key        object完整名称
     * @param expiration 签名过期时间
     * @return 可直接访问的路径
     */
    String getPrivateUrl(String key, LocalDateTime expiration);

}
