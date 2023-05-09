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

import java.io.Serializable;
import java.util.Date;

/**
 * The summary of an object stored in bucket.
 *
 * @author Jastar Wang
 * @date 2023/5/9
 * @see com.aliyun.oss.model.OSSObjectSummary
 * @see com.qcloud.cos.model.COSObjectSummary
 * @since 1.3.1
 */
public class ObjectSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    private String bucketName;
    private String key;
    private String eTag;
    private long size;
    private Date lastModified;
    private String storageClass;
    private Owner owner;

    /**
     * 阿里云的字段
     */
    private String type;
    /**
     * 阿里云的字段
     */
    private String restoreInfo;

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getStorageClass() {
        return storageClass;
    }

    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRestoreInfo() {
        return restoreInfo;
    }

    public void setRestoreInfo(String restoreInfo) {
        this.restoreInfo = restoreInfo;
    }

    @Override
    public String toString() {
        return "ObjectSummary{" +
                "bucketName='" + bucketName + '\'' +
                ", key='" + key + '\'' +
                ", eTag='" + eTag + '\'' +
                ", size=" + size +
                ", lastModified=" + lastModified +
                ", storageClass='" + storageClass + '\'' +
                ", owner=" + owner +
                ", type='" + type + '\'' +
                ", restoreInfo='" + restoreInfo + '\'' +
                '}';
    }

}
