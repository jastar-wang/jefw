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

import com.jastarwang.jefw.oss.config.JefwOssProperties;
import com.jastarwang.jefw.oss.model.ObjectSummary;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 对象存储服务接口
 * <p><b>注意：对象完整名称不能以斜杠“/”开头</b></p>
 *
 * @author Jastar Wang
 * @date 2023/2/19
 * @since 1.2.4
 */
public interface JefwOssService {
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
    Object getClient();

    /**
     * 获取存储渠道
     *
     * @return 实现类对应的存储渠道
     */
    OssChannel getChannel();

    /**
     * 获取本渠道配置信息
     *
     * @return 配置信息子类
     */
    JefwOssProperties.Base getConfig();

    /**
     * 检查bucket是否存在
     * <p><b>注意：</b></p>
     * <li>阿里云：非本账号下的bucket存在时，也会返回true</li>
     * <li>腾讯云：bucketName格式必须为BucketName-APPID，否则会抛出Bad异常</li>
     * <li>七牛云：本身不支持直接判断是否存在的功能，此处利用查询所有桶列表来实现，桶过多时可能会有性能风险</li>
     *
     * @param bucketName 非空，bucket名称
     * @return true-存在，false-不存在
     */
    boolean ifBucketExist(String bucketName);

    /**
     * 检查object是否存在
     *
     * @param bucketName 非空，bucket名称
     * @param key        非空，object完整名称
     * @return true-存在，false-bucket或object不存在
     */
    boolean ifObjExist(String bucketName, String key);

    /**
     * 设置object访问权限
     * <p><b>注意：</b></p>
     * <li>七牛云：不支持对象级别的权限，因此不支持此接口调用</li>
     *
     * @param bucketName 非空，bucket名称
     * @param key        非空，object完整名称
     * @param acl        非空，权限
     */
    void setObjAcl(String bucketName, String key, OssAccessPolicy acl);

    /**
     * 删除object
     *
     * @param bucketName 非空，bucket名称
     * @param key        非空，object完整名称
     */
    void delObj(String bucketName, String key);

    /**
     * 上传object
     *
     * @param bucketName 非空，bucket名称
     * @param key        非空，object完整名称
     * @param bytes      非空，数据字节数组
     */
    void putObj(String bucketName, String key, byte[] bytes);

    /**
     * 上传object
     *
     * @param bucketName 非空，bucket名称
     * @param key        非空，object完整名称
     * @param file       非空，文件
     */
    void putObj(String bucketName, String key, File file);

    /**
     * 上传object并指定权限
     * <p><b>注意：</b></p>
     * <li>七牛云：不支持对象级别的权限，因此不支持此接口调用</li>
     *
     * @param bucketName 非空，bucket名称
     * @param key        非空，object完整名称
     * @param bytes      非空，数据字节数组
     * @param acl        非空，权限
     */
    void putObj(String bucketName, String key, byte[] bytes, OssAccessPolicy acl);

    /**
     * 上传object并指定权限
     * <p><b>注意：</b></p>
     * <li>七牛云：不支持对象级别的权限，因此不支持此接口调用</li>
     *
     * @param bucketName 非空，bucket名称
     * @param key        非空，object完整名称
     * @param file       非空，文件
     * @param acl        非空，权限
     */
    void putObj(String bucketName, String key, File file, OssAccessPolicy acl);

    /**
     * 下载object
     *
     * @param bucketName 非空，bucket名称
     * @param key        非空，object完整名称
     * @return 数据字节数组
     */
    byte[] getObj(String bucketName, String key);

    /**
     * 为非公开的object生成带有时效签名的访问路径
     *
     * @param bucketName 非空，bucket名称
     * @param key        非空，object完整名称
     * @param expiration 非空，签名过期时间
     * @return 带有签名的访问路径（官方域名）<b>（注意：七牛云只能返回自定义域名）</b>
     */
    String getPrivateUrl(String bucketName, String key, LocalDateTime expiration);

    /**
     * 为非公开的object生成带有时效签名的访问路径
     *
     * @param bucketName      非空，bucket名称
     * @param key             非空，object完整名称
     * @param expiration      非空，签名过期时间
     * @param useCustomDomain 非空，是否使用自定义域名<b>（注意：七牛云只能传true，否则报错）</b>
     * @return 带有签名的访问路径（由useCustomDomain参数决定使用官方域名或者自定义域名）
     * @since 1.3.1
     */
    String getPrivateUrl(String bucketName, String key, LocalDateTime expiration, boolean useCustomDomain);

    /**
     * 根据前缀查询object列表
     *
     * @param bucketName 非空，bucket名称
     * @param prefix     可空，路径前缀（根目录请传NULL或空字符串，不要传“/”）
     * @return object对象
     * @since 1.3.1
     */
    List<ObjectSummary> listObj(String bucketName, String prefix);
}
