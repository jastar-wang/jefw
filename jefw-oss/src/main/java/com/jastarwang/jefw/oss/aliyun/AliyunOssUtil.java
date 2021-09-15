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
package com.jastarwang.jefw.oss.aliyun;

import cn.hutool.core.io.IoUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.*;
import com.jastarwang.jefw.oss.enums.OssAuthEnum;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import static com.jastarwang.jefw.oss.enums.OssAuthEnum.*;

/**
 * 阿里云OSS工具类
 *
 * <li><a href="https://help.aliyun.com/document_detail/32007.html">SDK文档</a></li>
 * <li><a href="https://help.aliyun.com/document_detail/31946.html">API文档</a></li>
 *
 * @author Jastar Wang
 * @date 2021/9/14
 * @since 1.2.0
 */
public class AliyunOssUtil {

    private static OSS client;

    @Resource
    private OSS aliyunOssClient;

    @PostConstruct
    public void init() {
        AliyunOssUtil.client = aliyunOssClient;
    }

    public void destroy() {
        if (client != null) {
            client.shutdown();
            client = null;
            aliyunOssClient = null;
        }
    }

    /**
     * 获取原始操作客户端
     *
     * @return OSS
     */
    public static OSS prototype() {
        return client;
    }

    /*==================================== bucket ======================================*/


    /**
     * 检查bucket是否存在
     * <br><b>注意：非本账号下的bucket存在也会返回true</b>
     *
     * @param bucketName bucket名称
     * @return true-存在，false-不存在
     */
    public static boolean ifBucketExist(String bucketName) {
        return client.doesBucketExist(bucketName);
    }

    /*==================================== object ======================================*/

    /**
     * 检查object是否存在
     *
     * @param bucketName bucket名称
     * @param key        object完整名称
     * @return true-存在，false-bucket或object不存在
     */
    public static boolean ifObjExist(String bucketName, String key) {
        return client.doesObjectExist(bucketName, key);
    }

    /**
     * 管理object访问权限
     *
     * @param bucketName bucket名称
     * @param key        object完整名称
     * @param acl        权限
     */
    public static void setObjAcl(String bucketName, String key, OssAuthEnum acl) {
        client.setObjectAcl(bucketName, key, aclAdapter(acl));
    }

    /**
     * 列举bucket下指定路径前缀的object（单次请求默认列举100个，最大值为1000）
     *
     * @param bucketName bucket名称
     * @param prefix     object名称前缀
     * @return {@link ListObjectsV2Result}
     */
    public static ListObjectsV2Result listObj(String bucketName, String prefix) {
        return client.listObjectsV2(bucketName, prefix);
    }

    /**
     * 删除object
     *
     * @param bucketName bucket名称
     * @param key        object完整名称
     */
    public static void deleteObj(String bucketName, String key) {
        client.deleteObject(bucketName, key);
    }

    /**
     * 删除object（批量，最多1000个）
     *
     * @param bucketName bucket名称
     * @param keys       object完整名称集合
     * @return 删除结果（默认开启详细模式，注意在该模式下，即使object不存在，结果中也会返回）
     */
    public static DeleteObjectsResult deleteObj(String bucketName, List<String> keys) {
        DeleteObjectsRequest request = new DeleteObjectsRequest(bucketName);
        request.withKeys(keys);
        request.withQuiet(false);
        return client.deleteObjects(request);
    }

    /**
     * 上传object
     *
     * @param bucketName bucket名称
     * @param key        object完整名称
     * @param bytes      数据字节数组
     */
    public static void putObj(String bucketName, String key, byte[] bytes) {
        client.putObject(bucketName, key, new ByteArrayInputStream(bytes));
    }

    /**
     * 上传object
     *
     * @param bucketName bucket名称
     * @param key        object完整名称（根路径不用且不能以“/”开头）
     * @param file       文件
     */
    public static void putObj(String bucketName, String key, File file) {
        client.putObject(bucketName, key, file);
    }

    /**
     * 上传object，并指定权限
     *
     * @param bucketName bucket名称
     * @param key        object完整名称
     * @param bytes      数据字节数组
     * @param acl        权限
     */
    public static void putObj(String bucketName, String key, byte[] bytes, OssAuthEnum acl) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setObjectAcl(aclAdapter(acl));
        client.putObject(bucketName, key, new ByteArrayInputStream(bytes), metadata);
    }

    /**
     * 上传object，并指定权限
     *
     * @param bucketName bucket名称
     * @param key        object完整名称
     * @param file       文件
     * @param acl        权限
     */
    public static void putObj(String bucketName, String key, File file, OssAuthEnum acl) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setObjectAcl(aclAdapter(acl));
        client.putObject(bucketName, key, file, metadata);
    }

    /**
     * 下载object
     *
     * @param bucketName bucket名称
     * @param key        object完整名称
     * @return 数据字节数组
     */
    public static byte[] getObj(String bucketName, String key) {
        InputStream objectContent = null;
        try {
            OSSObject ossObject = client.getObject(bucketName, key);
            objectContent = ossObject.getObjectContent();
            return IoUtil.readBytes(objectContent);
        } finally {
            IoUtil.close(objectContent);
        }
    }

    private static CannedAccessControlList aclAdapter(OssAuthEnum authEnum) {
        if (DEFAULT == authEnum) {
            return CannedAccessControlList.Default;
        } else if (PRIVATE == authEnum) {
            return CannedAccessControlList.Private;
        } else if (PUBLIC_READ == authEnum) {
            return CannedAccessControlList.PublicRead;
        } else if (PUBLIC_READ_WRITE == authEnum) {
            return CannedAccessControlList.PublicReadWrite;
        } else {
            throw new IllegalArgumentException("OssAuthEnum param may be incorrect!");
        }
    }

}
