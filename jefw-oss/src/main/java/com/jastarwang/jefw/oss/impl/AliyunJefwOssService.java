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
package com.jastarwang.jefw.oss.impl;

import cn.hutool.core.io.IoUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.jastarwang.jefw.oss.JefwOssService;
import com.jastarwang.jefw.oss.OssAccessPolicy;
import com.jastarwang.jefw.oss.OssChannel;
import com.jastarwang.jefw.oss.config.JefwOssProperties;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static com.jastarwang.jefw.oss.OssAccessPolicy.*;

/**
 * OSS对象存储之阿里云实现
 *
 * <li><a href="https://help.aliyun.com/document_detail/32007.html">SDK文档</a></li>
 * <li><a href="https://help.aliyun.com/document_detail/31946.html">API文档</a></li>
 *
 * @author Jastar Wang
 * @date 2023/2/19
 * @since 1.2.4
 */
public class AliyunJefwOssService implements JefwOssService {

    private final OSS client;
    private final JefwOssProperties.Aliyun config;

    public AliyunJefwOssService(OSS client, JefwOssProperties.Aliyun config) {
        this.client = client;
        this.config = config;
    }

    @Override
    public void init() {
        // do nothing
    }

    @Override
    public void destroy() {
        if (client != null) {
            client.shutdown();
        }
    }

    @Override
    public Object getClient() {
        return client;
    }

    @Override
    public OssChannel getChannel() {
        return OssChannel.ALIYUN;
    }

    @Override
    public JefwOssProperties.Aliyun getConfig() {
        return config;
    }

    @Override
    public boolean ifBucketExist(String bucketName) {
        return client.doesBucketExist(bucketName);
    }

    @Override
    public boolean ifObjExist(String bucketName, String key) {
        return client.doesObjectExist(bucketName, key);
    }

    @Override
    public void setObjAcl(String bucketName, String key, OssAccessPolicy acl) {
        client.setObjectAcl(bucketName, key, aclAdapter(acl));
    }

    @Override
    public void delObj(String bucketName, String key) {
        client.deleteObject(bucketName, key);
    }

    @Override
    public void putObj(String bucketName, String key, byte[] bytes) {
        client.putObject(bucketName, key, new ByteArrayInputStream(bytes));
    }

    @Override
    public void putObj(String bucketName, String key, File file) {
        client.putObject(bucketName, key, file);
    }

    @Override
    public void putObj(String bucketName, String key, byte[] bytes, OssAccessPolicy acl) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setObjectAcl(aclAdapter(acl));
        client.putObject(bucketName, key, new ByteArrayInputStream(bytes), metadata);
    }

    @Override
    public void putObj(String bucketName, String key, File file, OssAccessPolicy acl) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setObjectAcl(aclAdapter(acl));
        client.putObject(bucketName, key, file, metadata);
    }

    @Override
    public byte[] getObj(String bucketName, String key) {
        InputStream objectContent = null;
        try {
            OSSObject ossObject = client.getObject(bucketName, key);
            objectContent = ossObject.getObjectContent();
            return IoUtil.readBytes(objectContent);
        } finally {
            IoUtil.close(objectContent);
        }
    }

    @Override
    public String getPrivateUrl(String bucketName, String key, LocalDateTime expiration) {
        Date date = null;
        if (expiration != null) {
            date = Date.from(expiration.atZone(ZoneId.of("+8")).toInstant());
        }
        URL url = client.generatePresignedUrl(bucketName, key, date);
        return url.toString();
    }

    private CannedAccessControlList aclAdapter(OssAccessPolicy accessPolicy) {
        if (DEFAULT == accessPolicy) {
            return CannedAccessControlList.Default;
        } else if (PRIVATE == accessPolicy) {
            return CannedAccessControlList.Private;
        } else if (PUBLIC_READ == accessPolicy) {
            return CannedAccessControlList.PublicRead;
        } else if (PUBLIC_READ_WRITE == accessPolicy) {
            return CannedAccessControlList.PublicReadWrite;
        } else {
            throw new IllegalArgumentException("Unexpected OssAccessPolicy value[" + accessPolicy + "]!");
        }
    }

}
