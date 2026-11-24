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
import com.jastarwang.jefw.oss.model.ObjectPage;
import com.jastarwang.jefw.oss.model.ObjectSummary;
import com.jastarwang.jefw.oss.model.Owner;
import io.minio.*;
import io.minio.Http.Method;
import io.minio.errors.ErrorResponseException;
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

    private MinioAsyncClient listingClient;
    private final boolean ownsListingClient;

    public MinioJefwOssService(MinioClient client, JefwOssProperties.Minio config) {
        this(client, config, null);
    }

    public MinioJefwOssService(MinioClient client, JefwOssProperties.Minio config, MinioAsyncClient listingClient) {
        this.listingClient = listingClient;
        this.ownsListingClient = listingClient == null;
        this.client = client;
        this.config = config;
    }

    // 同步客户端只提供自动翻页的迭代器，单页 V2 响应需要异步客户端。
    private synchronized MinioAsyncClient listingClient() {
        if (listingClient == null) {
            listingClient = MinioAsyncClient.builder().endpoint(config.endpointUrl())
                    .credentials(config.getAccessKeyId(), config.getAccessKeySecret()).build();
        }
        return listingClient;
    }

    @Override
    public void init() {
        // do nothing
    }

    @Override
    public synchronized void destroy() {
        try (MinioAsyncClient owned = ownsListingClient ? listingClient : null) {
            if (client != null) {
                client.close();
            }
        } catch (Exception e) {
            throw new JefwOssException(e);
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
                    .stream(new ByteArrayInputStream(bytes), (long) bytes.length, -1L)
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
    public ObjectPage listObjPage(String bucketName, String prefix, int maxKeys, String continuationToken) {
        ObjectPage.validateMaxKeys(maxKeys);
        try {
            ListObjectsV2Args args = ListObjectsV2Args.builder().bucket(bucketName)
                    .prefix(prefix).maxKeys(maxKeys).continuationToken(continuationToken)
                    .delimiter("").encodingType("url").build();
            var result = listingClient().listObjectsV2(args).get().result();
            List<ObjectSummary> items = new ArrayList<>();
            for (Item item : result.contents()) {
                item.setEncodingType(result.encodingType());
                if (!item.isDir()) {
                    items.add(convertToObjectSummary(bucketName, item));
                }
            }
            return new ObjectPage(items, result.isTruncated() ? result.nextContinuationToken() : null);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new JefwOssException(e);
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
