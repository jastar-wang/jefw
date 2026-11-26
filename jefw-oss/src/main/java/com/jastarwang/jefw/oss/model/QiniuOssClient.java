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
