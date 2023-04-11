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
import org.springframework.beans.factory.InitializingBean;

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
}
