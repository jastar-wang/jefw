/*
 * Copyright 2020 Jastar Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jastarwang.jefw.oss.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.ListObjectsV2Request;
import com.aliyun.oss.model.ListObjectsV2Result;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.jastarwang.jefw.oss.JefwOssService;
import com.jastarwang.jefw.oss.OssAccessPolicy;
import com.jastarwang.jefw.oss.OssChannel;
import com.jastarwang.jefw.oss.config.JefwOssProperties;
import com.jastarwang.jefw.oss.model.ObjectPage;
import com.jastarwang.jefw.oss.model.ObjectSummary;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static com.jastarwang.jefw.oss.OssAccessPolicy.DEFAULT;
import static com.jastarwang.jefw.oss.OssAccessPolicy.PRIVATE;
import static com.jastarwang.jefw.oss.OssAccessPolicy.PUBLIC_READ;
import static com.jastarwang.jefw.oss.OssAccessPolicy.PUBLIC_READ_WRITE;

/**
 * OSS对象存储之阿里云实现
 *
 * <li><a href="https://help.aliyun.com/document_detail/32007.html">SDK文档</a></li>
 * <li><a href="https://help.aliyun.com/document_detail/31946.html">API文档</a></li>
 *
 * @author Jastar Wang
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
        return getPrivateUrl(bucketName, key, expiration, false);
    }

    @Override
    public String getPrivateUrl(String bucketName, String key, LocalDateTime expiration, boolean useCustomDomain) {
        Date date = null;
        if (expiration != null) {
            date = Date.from(expiration.atZone(ZoneId.of("+8")).toInstant());
        }
        URL url;
        if (useCustomDomain) {
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key);
            request.setExpiration(date);
            request.addHeader("Host", URLUtil.url(config.getUrlPrefix()).getHost());
            url = client.generatePresignedUrl(request);
            return StrUtil.removeSuffix(config.getUrlPrefix(), "/") + url.getPath() + "?" + url.getQuery();
        } else {
            url = client.generatePresignedUrl(bucketName, key, date);
            return url.toString();
        }
    }

    @Override
    public ObjectPage listObjPage(String bucketName, String prefix, int maxKeys, String continuationToken) {
        ObjectPage.validateMaxKeys(maxKeys);
        ListObjectsV2Request request = new ListObjectsV2Request(bucketName);
        request.setPrefix(prefix);
        request.setMaxKeys(maxKeys);
        request.setContinuationToken(continuationToken);
        request.setFetchOwner(true);
        ListObjectsV2Result result = client.listObjectsV2(request);
        List<ObjectSummary> items = result.getObjectSummaries().stream()
                .filter(obj -> !(obj.getSize() <= 0 && obj.getKey().endsWith("/")))
                .map(obj -> BeanUtil.copyProperties(obj, ObjectSummary.class))
                .toList();
        return new ObjectPage(items, result.isTruncated() ? result.getNextContinuationToken() : null);
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
