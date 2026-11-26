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
import com.jastarwang.jefw.oss.JefwOssService;
import com.jastarwang.jefw.oss.OssAccessPolicy;
import com.jastarwang.jefw.oss.OssChannel;
import com.jastarwang.jefw.oss.config.JefwOssProperties;
import com.jastarwang.jefw.oss.model.ObjectPage;
import com.jastarwang.jefw.oss.model.ObjectSummary;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.Headers;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.CannedAccessControlList;
import com.qcloud.cos.model.ListObjectsRequest;
import com.qcloud.cos.model.ObjectListing;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jastarwang.jefw.oss.OssAccessPolicy.DEFAULT;
import static com.jastarwang.jefw.oss.OssAccessPolicy.PRIVATE;
import static com.jastarwang.jefw.oss.OssAccessPolicy.PUBLIC_READ;
import static com.jastarwang.jefw.oss.OssAccessPolicy.PUBLIC_READ_WRITE;

/**
 * OSS对象存储之腾讯云实现
 *
 * <li><a href="https://cloud.tencent.com/document/product/436/10199">SDK文档</a></li>
 * <li><a href="https://cloud.tencent.com/document/product/436/7751">API文档</a></li>
 *
 * @author Jastar Wang
 * @since 1.2.4
 */
public class TencentJefwOssService implements JefwOssService {

    private final COSClient client;
    private final JefwOssProperties.Tencent config;

    public TencentJefwOssService(COSClient client, JefwOssProperties.Tencent config) {
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
        return OssChannel.TENCENT;
    }

    @Override
    public JefwOssProperties.Tencent getConfig() {
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
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bytes.length);
        client.putObject(bucketName, key, new ByteArrayInputStream(bytes), metadata);
    }

    @Override
    public void putObj(String bucketName, String key, File file) {
        client.putObject(bucketName, key, file);
    }

    @Override
    public void putObj(String bucketName, String key, byte[] bytes, OssAccessPolicy acl) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bytes.length);
        PutObjectRequest request = new PutObjectRequest(bucketName, key, new ByteArrayInputStream(bytes), metadata);
        request.setCannedAcl(aclAdapter(acl));
        client.putObject(request);
    }

    @Override
    public void putObj(String bucketName, String key, File file, OssAccessPolicy acl) {
        PutObjectRequest request = new PutObjectRequest(bucketName, key, file);
        request.setCannedAcl(aclAdapter(acl));
        client.putObject(request);
    }

    @Override
    public byte[] getObj(String bucketName, String key) {
        InputStream objectContent = null;
        try {
            COSObject object = client.getObject(bucketName, key);
            objectContent = object.getObjectContent();
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
            Map<String, String> headers = new HashMap<>();
            headers.put(Headers.HOST, URLUtil.url(config.getUrlPrefix()).getHost());
            url = client.generatePresignedUrl(bucketName, key, date, HttpMethodName.GET, headers, new HashMap<>());
            return StrUtil.removeSuffix(config.getUrlPrefix(), "/") + url.getPath() + "?" + url.getQuery();
        } else {
            url = client.generatePresignedUrl(bucketName, key, date);
            return url.toString();
        }
    }

    @Override
    public ObjectPage listObjPage(String bucketName, String prefix, int maxKeys, String continuationToken) {
        ObjectPage.validateMaxKeys(maxKeys);
        ListObjectsRequest request = new ListObjectsRequest(bucketName, prefix, continuationToken, null, maxKeys);
        ObjectListing result = client.listObjects(request);
        List<ObjectSummary> items = result.getObjectSummaries().stream()
                .filter(obj -> !(obj.getSize() <= 0 && obj.getKey().endsWith("/")))
                .map(obj -> BeanUtil.copyProperties(obj, ObjectSummary.class))
                .toList();
        return new ObjectPage(items, result.isTruncated() ? result.getNextMarker() : null);
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
