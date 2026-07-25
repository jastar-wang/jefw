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

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.jastarwang.jefw.oss.JefwOssException;
import com.jastarwang.jefw.oss.JefwOssService;
import com.jastarwang.jefw.oss.OssAccessPolicy;
import com.jastarwang.jefw.oss.OssChannel;
import com.jastarwang.jefw.oss.config.JefwOssProperties;
import com.jastarwang.jefw.oss.model.ObjectSummary;
import com.jastarwang.jefw.oss.model.QiniuOssClient;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.storage.model.FileListing;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * OSS对象存储之七牛云实现
 *
 * <li><a href="https://developer.qiniu.com/kodo/1239/java">SDK文档</a></li>
 * <li><a href="https://developer.qiniu.com/kodo/3939/overview-of-the-api">API文档</a></li>
 *
 * @author Jastar Wang
 * @date 2025/4/21
 * @since 2.1.0
 */
public class QiniuJefwOssService implements JefwOssService {

    private final QiniuOssClient client;
    private final JefwOssProperties.Qiniu config;

    public QiniuJefwOssService(QiniuOssClient client, JefwOssProperties.Qiniu config) {
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
        return OssChannel.QINIU;
    }

    @Override
    public JefwOssProperties.Qiniu getConfig() {
        return config;
    }

    @Override
    public boolean ifBucketExist(String bucketName) {
        try {
            BucketManager bucketManager = client.getBucketManager();
            String[] buckets = bucketManager.buckets();
            if (buckets != null) {
                for (String bucket : buckets) {
                    if (bucket.equals(bucketName)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            throw new JefwOssException(e);
        }
    }

    @Override
    public boolean ifObjExist(String bucketName, String key) {
        try {
            BucketManager bucketManager = client.getBucketManager();
            FileInfo fileInfo = bucketManager.stat(bucketName, key);
            return fileInfo != null;
        } catch (Exception e) {
            if (e instanceof QiniuException ex && ex.code() == 612) {
                return false;
            }
            throw new JefwOssException(e);
        }
    }

    @Override
    public void setObjAcl(String bucketName, String key, OssAccessPolicy acl) {
        throw new UnsupportedOperationException("七牛云不支持对象级别的权限设置");
    }

    @Override
    public void delObj(String bucketName, String key) {
        try {
            BucketManager bucketManager = client.getBucketManager();
            bucketManager.delete(bucketName, key);
        } catch (Exception e) {
            if (e instanceof QiniuException ex && ex.code() == 612) {
                return;
            }
            throw new JefwOssException(e);
        }
    }

    @Override
    public void putObj(String bucketName, String key, byte[] bytes) {
        try {
            String token = getUploadToken(bucketName, key, true);
            UploadManager uploadManager = client.getUploadManager();
            uploadManager.put(bytes, key, token);
        } catch (Exception e) {
            throw new JefwOssException(e);
        }
    }

    @Override
    public void putObj(String bucketName, String key, File file) {
        try {
            String token = getUploadToken(bucketName, key, true);
            UploadManager uploadManager = client.getUploadManager();
            uploadManager.put(file, key, token);
        } catch (Exception e) {
            throw new JefwOssException(e);
        }
    }

    @Override
    public void putObj(String bucketName, String key, byte[] bytes, OssAccessPolicy acl) {
        throw new UnsupportedOperationException("七牛云不支持对象级别的权限设置");
    }

    @Override
    public void putObj(String bucketName, String key, File file, OssAccessPolicy acl) {
        throw new UnsupportedOperationException("七牛云不支持对象级别的权限设置");
    }

    @Override
    public byte[] getObj(String bucketName, String key) {
        String privateUrl = getPrivateUrl(bucketName, key, LocalDateTime.now().plusMinutes(5));
        try {
            return IoUtil.readBytes(new URL(privateUrl).openStream());
        } catch (IOException e) {
            throw new JefwOssException(e);
        }
    }

    @Override
    public String getPrivateUrl(String bucketName, String key, LocalDateTime expiration) {
        return getPrivateUrl(bucketName, key, expiration, true);
    }

    @Override
    public String getPrivateUrl(String bucketName, String key, LocalDateTime expiration, boolean useCustomDomain) {
        if (!useCustomDomain) {
            throw new UnsupportedOperationException("七牛云仅支持使用自定义域名访问");
        }
        String pureUrl = StrUtil.appendIfMissing(config.getUrlPrefix(), "/") + key;
        Auth auth = client.getAuth();
        if (expiration != null) {
            long seconds = LocalDateTimeUtil.between(LocalDateTime.now(), expiration, ChronoUnit.SECONDS);
            return auth.privateDownloadUrl(pureUrl, seconds);
        } else {
            return client.getAuth().privateDownloadUrl(pureUrl);
        }
    }

    @Override
    public List<ObjectSummary> listObj(String bucketName, String prefix) {
        try {
            BucketManager bucketManager = client.getBucketManager();
            FileListing fileListing = bucketManager.listFiles(bucketName, prefix, null, Integer.MAX_VALUE, null);
            if (fileListing == null || fileListing.items == null || fileListing.items.length == 0) {
                return List.of();
            }
            List<ObjectSummary> objectList = new ArrayList<>(fileListing.items.length);
            for (FileInfo fileInfo : fileListing.items) {
                objectList.add(convertToObjectSummary(bucketName, fileInfo));
            }
            return objectList;
        } catch (Exception e) {
            throw new JefwOssException(e);
        }
    }

    private String getUploadToken(String bucketName, String key, boolean overwrite) {
        if (overwrite) {
            return client.getAuth().uploadToken(bucketName, key);
        } else {
            // 注意：覆盖是针对于同一个key再次上传不同的文件内容，当文件内容不发生变更时可以多次调用上传不会报错
            // 方式1：不带key生成的默认规则不允许覆盖
            // return client.getAuth().uploadToken(bucketName);
            // 方式2：带key并且指定参数不允许覆盖
            return client.getAuth().uploadToken(bucketName, key, 3600, new StringMap().put("insertOnly", 1));
        }
    }

    /**
     * 将100纳秒为单位的时间戳转换为Date
     *
     * @param timestampIn100ns 以100纳秒为单位的时间戳
     * @return 对应的Date对象
     */
    private Date convertFrom100Nanoseconds(long timestampIn100ns) {
        // 转换为毫秒: 除以10,000 (因为1毫秒=10,000个100纳秒)
        long milliseconds = timestampIn100ns / 10_000;
        // 处理余数部分，四舍五入
        long remainder = timestampIn100ns % 10_000;
        if (remainder >= 5_000) {
            milliseconds += 1; // 四舍五入
        }
        return new Date(milliseconds);
    }

    private ObjectSummary convertToObjectSummary(String bucketName, FileInfo fileInfo) {
        ObjectSummary objectSummary = new ObjectSummary();
        objectSummary.setBucketName(bucketName);
        objectSummary.setKey(fileInfo.key);
        objectSummary.setETag(fileInfo.hash);
        objectSummary.setSize(fileInfo.fsize);
        objectSummary.setStorageClass(fileInfo.type + "");
        objectSummary.setLastModified(convertFrom100Nanoseconds(fileInfo.putTime));
        return objectSummary;
    }

}
