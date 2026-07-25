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

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.jastarwang.jefw.oss.JefwOssException;
import com.jastarwang.jefw.oss.JefwOssService;
import com.jastarwang.jefw.oss.OssAccessPolicy;
import com.jastarwang.jefw.oss.OssChannel;
import com.jastarwang.jefw.oss.config.JefwOssProperties;
import com.jastarwang.jefw.oss.model.ObjectSummary;
import com.jastarwang.jefw.oss.model.Owner;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import io.minio.messages.Item;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * OSS对象存储之MinIO实现
 *
 * <li><a href="https://docs.min.io/enterprise/aistor-object-store/developers/sdk/java/">SDK文档</a></li>
 * <li><a href="https://minio-java.min.io/">API文档</a></li>
 *
 * @author Jastar Wang
 * @date 2026/4/27
 * @since 2.3.0
 */
public class MinioJefwOssService implements JefwOssService {

    private final MinioClient client;
    private final JefwOssProperties.Minio config;

    public MinioJefwOssService(MinioClient client, JefwOssProperties.Minio config) {
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
            try {
                client.close();
            } catch (Exception e) {
                throw new JefwOssException(e);
            }
        }
    }

    @Override
    public Object getClient() {
        return client;
    }

    @Override
    public OssChannel getChannel() {
        return OssChannel.MINIO;
    }

    @Override
    public JefwOssProperties.Minio getConfig() {
        return config;
    }

    @Override
    public boolean ifBucketExist(String bucketName) {
        try {
            return client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new JefwOssException(e);
        }
    }

    @Override
    public boolean ifObjExist(String bucketName, String key) {
        try {
            client.statObject(StatObjectArgs.builder().bucket(bucketName).object(key).build());
            return true;
        } catch (Exception e) {
            if (e instanceof ErrorResponseException ex) {
                if (ex.response() != null && ex.response().code() == 404) {
                    return false;
                }
            }
            throw new JefwOssException(e);
        }
    }

    @Override
    public void setObjAcl(String bucketName, String key, OssAccessPolicy acl) {
        throw new UnsupportedOperationException("MinIO不支持对象级别的ACL设置，请使用Bucket Policy或IAM Policy控制访问权限");
    }

    @Override
    public void delObj(String bucketName, String key) {
        try {
            client.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(key).build());
        } catch (Exception e) {
            throw new JefwOssException(e);
        }
    }

    @Override
    public void putObj(String bucketName, String key, byte[] bytes) {
        try {
            client.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(key)
                    .stream(new ByteArrayInputStream(bytes), bytes.length, -1)
                    .build());
        } catch (Exception e) {
            throw new JefwOssException(e);
        }
    }

    @Override
    public void putObj(String bucketName, String key, File file) {
        try {
            client.uploadObject(UploadObjectArgs.builder()
                    .bucket(bucketName)
                    .object(key)
                    .filename(file.getAbsolutePath())
                    .build());
        } catch (Exception e) {
            throw new JefwOssException(e);
        }
    }

    @Override
    public void putObj(String bucketName, String key, byte[] bytes, OssAccessPolicy acl) {
        throw new UnsupportedOperationException("MinIO不支持对象级别的ACL设置，请使用Bucket Policy或IAM Policy控制访问权限");
    }

    @Override
    public void putObj(String bucketName, String key, File file, OssAccessPolicy acl) {
        throw new UnsupportedOperationException("MinIO不支持对象级别的ACL设置，请使用Bucket Policy或IAM Policy控制访问权限");
    }

    @Override
    public byte[] getObj(String bucketName, String key) {
        InputStream objectContent = null;
        try {
            objectContent = client.getObject(GetObjectArgs.builder().bucket(bucketName).object(key).build());
            return IoUtil.readBytes(objectContent);
        } catch (Exception e) {
            throw new JefwOssException(e);
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
        try {
            GetPresignedObjectUrlArgs.Builder builder = GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(key);
            if (expiration != null) {
                long seconds = expiration.atZone(ZoneId.of("+8")).toEpochSecond() - LocalDateTime.now().atZone(ZoneId.of("+8")).toEpochSecond();
                if (seconds > 0) {
                    builder.expiry((int) seconds, TimeUnit.SECONDS);
                }
            }
            if (useCustomDomain) {
                builder.extraHeaders(Collections.singletonMap("Host", URLUtil.url(config.getUrlPrefix()).getAuthority()));
            }
            String url = client.getPresignedObjectUrl(builder.build());
            if (useCustomDomain) {
                return StrUtil.removeSuffix(config.getUrlPrefix(), "/") + URLUtil.url(url).getFile();
            }
            return url;
        } catch (Exception e) {
            throw new JefwOssException(e);
        }
    }

    @Override
    public List<ObjectSummary> listObj(String bucketName, String prefix) {
        try {
            ListObjectsArgs.Builder builder = ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .recursive(true);
            if (StrUtil.isNotEmpty(prefix)) {
                builder.prefix(prefix);
            }
            Iterable<Result<Item>> results = client.listObjects(builder.build());
            List<ObjectSummary> objectList = new ArrayList<>();
            for (Result<Item> result : results) {
                Item item = result.get();
                if (item.isDir()) {
                    continue;
                }
                objectList.add(convertToObjectSummary(bucketName, item));
            }
            return objectList;
        } catch (Exception e) {
            throw new JefwOssException(e);
        }
    }

    private ObjectSummary convertToObjectSummary(String bucketName, Item item) {
        ObjectSummary objectSummary = new ObjectSummary();
        objectSummary.setBucketName(bucketName);
        objectSummary.setKey(item.objectName());
        objectSummary.setETag(item.etag());
        objectSummary.setSize(item.size());
        objectSummary.setStorageClass(item.storageClass());
        if (item.lastModified() != null) {
            objectSummary.setLastModified(Date.from(item.lastModified().toInstant()));
        }
        if (item.owner() != null) {
            Owner owner = new Owner();
            owner.setDisplayName(item.owner().displayName());
            owner.setId(item.owner().id());
            objectSummary.setOwner(owner);
        }
        return objectSummary;
    }

}
