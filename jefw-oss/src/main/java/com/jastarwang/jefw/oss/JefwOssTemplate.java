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
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对象存储操作模板类
 *
 * @author Jastar Wang
 * @date 2023/4/5
 * @since 1.3.0
 */
public class JefwOssTemplate implements InitializingBean {
    /**
     * 服务顶级容器
     */
    private final Map<OssChannel, JefwOssService> serviceRegistry = new HashMap<>();
    /**
     * 系统存储配置
     */
    private final JefwOssProperties jefwOssProperties;
    /**
     * 所有存储服务
     */
    private final List<JefwOssService> jefwOssServices;
    /**
     * 默认存储服务
     */
    private JefwOssService defaultService;
    /**
     * 默认存储渠道
     */
    private OssChannel defaultChannel;

    public JefwOssTemplate(JefwOssProperties jefwOssProperties, List<JefwOssService> jefwOssServices) {
        this.jefwOssProperties = jefwOssProperties;
        this.jefwOssServices = jefwOssServices;
    }

    /**
     * 获取存储配置
     *
     * @return 存储配置信息
     */
    public JefwOssProperties getProperties() {
        return jefwOssProperties;
    }

    /**
     * 根据指定渠道获取存储服务
     *
     * @param channel 指定渠道
     * @return 对应渠道的存储服务
     */
    public JefwOssService getService(OssChannel channel) {
        return serviceRegistry.get(channel);
    }

    /**
     * 获取默认的存储服务
     *
     * @return 默认的存储服务
     * @since 1.3.1
     */
    public JefwOssService getDefaultService() {
        return defaultService;
    }

    /**
     * 获取默认的存储渠道
     *
     * @return 默认的存储渠道
     */
    public OssChannel getDefaultChannel() {
        return defaultChannel;
    }

    /**
     * 获取默认的存储配置
     *
     * @return 默认的存储配置（子类）
     */
    public JefwOssProperties.Base getDefaultConfig() {
        return defaultService.getConfig();
    }

    @Override
    public void afterPropertiesSet() {
        for (JefwOssService jefwOssService : jefwOssServices) {
            serviceRegistry.put(jefwOssService.getChannel(), jefwOssService);
        }
        if (jefwOssProperties.getDefaultChannel() != null) {
            defaultService = serviceRegistry.get(jefwOssProperties.getDefaultChannel());
        } else {
            defaultService = jefwOssServices.get(0);
        }
        defaultChannel = defaultService.getChannel();
    }
    // ==================================== 装饰方法区 ==================================== //

    /**
     * 检查bucket是否存在
     * <p>注意：</p>
     * <li>阿里云：非本账号下的bucket存在时，也会返回true</li>
     * <li>腾讯云：bucketName格式必须为BucketName-APPID，否则会抛出Bad异常</li>
     *
     * @return true-存在，false-不存在
     */
    public boolean ifBucketExist() {
        return defaultService.ifBucketExist(defaultService.getConfig().getBucketName());
    }

    /**
     * 检查object是否存在
     *
     * @param key 非空，object完整名称
     * @return true-存在，false-bucket或object不存在
     */
    public boolean ifObjExist(String key) {
        return defaultService.ifObjExist(defaultService.getConfig().getBucketName(), key);
    }

    /**
     * 设置object访问权限
     *
     * @param key 非空，object完整名称
     * @param acl 非空，权限
     */
    public void setObjAcl(String key, OssAccessPolicy acl) {
        defaultService.setObjAcl(defaultService.getConfig().getBucketName(), key, acl);
    }

    /**
     * 删除object
     *
     * @param key 非空，object完整名称
     */
    public void delObj(String key) {
        defaultService.delObj(defaultService.getConfig().getBucketName(), key);
    }

    /**
     * 上传object
     *
     * @param key   非空，object完整名称
     * @param bytes 非空，数据字节数组
     */
    public void putObj(String key, byte[] bytes) {
        defaultService.putObj(defaultService.getConfig().getBucketName(), key, bytes);
    }

    /**
     * 上传object
     *
     * @param key  非空，object完整名称
     * @param file 非空，文件
     */
    public void putObj(String key, File file) {
        defaultService.putObj(defaultService.getConfig().getBucketName(), key, file);
    }

    /**
     * 上传object并指定权限
     *
     * @param key   非空，object完整名称
     * @param bytes 非空，数据字节数组
     * @param acl   非空，权限
     */
    public void putObj(String key, byte[] bytes, OssAccessPolicy acl) {
        defaultService.putObj(defaultService.getConfig().getBucketName(), key, bytes, acl);
    }

    /**
     * 上传object并指定权限
     *
     * @param key  非空，object完整名称
     * @param file 非空，文件
     * @param acl  非空，权限
     */
    public void putObj(String key, File file, OssAccessPolicy acl) {
        defaultService.putObj(defaultService.getConfig().getBucketName(), key, file, acl);
    }

    /**
     * 下载object
     *
     * @param key 非空，object完整名称
     * @return 数据字节数组
     */
    public byte[] getObj(String key) {
        return defaultService.getObj(defaultService.getConfig().getBucketName(), key);
    }

    /**
     * 为非公开的object生成带有时效签名的访问路径
     *
     * @param key        非空，object完整名称
     * @param expiration 非空，签名过期时间
     * @return 带有签名的访问路径（官方域名）
     */
    public String getPrivateUrl(String key, LocalDateTime expiration) {
        return defaultService.getPrivateUrl(defaultService.getConfig().getBucketName(), key, expiration);
    }

    /**
     * 为非公开的object生成带有时效签名的访问路径
     *
     * @param key             非空，object完整名称
     * @param expiration      非空，签名过期时间
     * @param useCustomDomain 非空，是否使用自定义域名
     * @return 带有签名的访问路径（由useCustomDomain参数决定使用官方域名或者自定义域名）
     * @since 1.3.1
     */
    public String getPrivateUrl(String key, LocalDateTime expiration, boolean useCustomDomain) {
        return defaultService.getPrivateUrl(defaultService.getConfig().getBucketName(), key, expiration, useCustomDomain);
    }

    /**
     * 根据前缀查询object列表
     *
     * @param prefix 可空，路径前缀（根目录请传NULL或空字符串，不要传“/”）
     * @return object对象
     * @since 1.3.1
     */
    public List<ObjectSummary> listObj(String prefix) {
        return defaultService.listObj(defaultService.getConfig().getBucketName(), prefix);
    }
}
