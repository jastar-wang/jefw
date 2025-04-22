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
package com.jastarwang.jefw.oss.model;

import com.qiniu.http.Client;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

import java.io.Serial;
import java.io.Serializable;

/**
 * 七牛云客户端包装类
 *
 * @author Jastar Wang
 * @date 2025/4/21
 * @since 2.1.0
 */
public class QiniuOssClient implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Auth auth;
    private Region region;
    private Configuration cfg;
    private Client client;
    private BucketManager bucketManager;
    private UploadManager uploadManager;

    public QiniuOssClient(Auth auth, Region region) {
        this.auth = auth;
        this.region = region;
        this.cfg = new Configuration(region);
        this.cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;
        this.client = new Client(this.cfg);
        this.bucketManager = new BucketManager(auth, cfg, client);
        this.uploadManager = new UploadManager(cfg);
    }

    public void shutdown() {
        this.auth = null;
        this.region = null;
        this.cfg = null;
        this.client = null;
        this.bucketManager = null;
        this.uploadManager = null;
    }

    public Auth getAuth() {
        return auth;
    }

    public Region getRegion() {
        return region;
    }

    public Configuration getCfg() {
        return cfg;
    }

    public Client getClient() {
        return client;
    }

    public BucketManager getBucketManager() {
        return bucketManager;
    }

    public UploadManager getUploadManager() {
        return uploadManager;
    }

    @Override
    public String toString() {
        return "QiniuOssClient{" +
                "auth=" + auth +
                ", region=" + region +
                ", cfg=" + cfg +
                ", client=" + client +
                ", bucketManager=" + bucketManager +
                ", uploadManager=" + uploadManager +
                '}';
    }

}
