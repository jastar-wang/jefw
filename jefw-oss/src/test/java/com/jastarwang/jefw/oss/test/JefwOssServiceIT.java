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
package com.jastarwang.jefw.oss.test;

import cn.hutool.core.io.FileUtil;
import com.jastarwang.jefw.oss.JefwOssService;
import com.jastarwang.jefw.oss.JefwOssTemplate;
import com.jastarwang.jefw.oss.OssAccessPolicy;
import com.jastarwang.jefw.oss.config.JefwOssProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * OSS对象存储集成测试
 *
 * @author Jastar Wang
 * @date 2021/09/14
 * @since 1.2.0
 */
@EnabledIfEnvironmentVariable(named = "JEFW_OSS_IT", matches = "true")
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@SpringBootTest
public class JefwOssServiceIT {

    private static final String UN_EXIST_BUCKET = "adsfdasfdfdallllmkjd";
    private static final String OBJ1 = "test/demo.jpg";
    private static final String OBJ2 = "test/fun/readme.txt";
    private String ALIYUN_EXIST_BUCKET;
    private String TENCENT_EXIST_BUCKET;
    private String QINIU_EXIST_BUCKET;
    private String MINIO_EXIST_BUCKET;

    @TempDir
    Path tempDir;

    @Resource
    private JefwOssProperties properties;

    // =============== 方式1：自定义获取 =============== //
    @Resource
    @Qualifier("aliyunJefwOssService")
    private JefwOssService aliyunJefwOssService;

    @Resource
    @Qualifier("tencentJefwOssService")
    private JefwOssService tencentJefwOssService;

    @Resource
    @Qualifier("qiniuJefwOssService")
    private JefwOssService qiniuJefwOssService;

    @Resource
    @Qualifier("minioJefwOssService")
    private JefwOssService minioJefwOssService;

    // =============== 方式2：统一模板类 =============== //
    @Resource
    private JefwOssTemplate ossTemplate;

    @PostConstruct
    public void init() {
        // 初始化用来测试的真实存在的bucket
        ALIYUN_EXIST_BUCKET = properties.getAliyun().getBucketName();
        TENCENT_EXIST_BUCKET = properties.getTencent().getBucketName();
        QINIU_EXIST_BUCKET = properties.getQiniu().getBucketName();
        MINIO_EXIST_BUCKET = properties.getMinio().getBucketName();
    }

    @Test
    public void contextLoads() {
        System.out.println(properties);
        Assertions.assertNotNull(properties, "属性配置对象未正常注入");
        Assertions.assertFalse(aliyunJefwOssService.ifBucketExist(UN_EXIST_BUCKET));
        Assertions.assertFalse(tencentJefwOssService.ifBucketExist("abcdefg-123456"));
        Assertions.assertFalse(qiniuJefwOssService.ifBucketExist(UN_EXIST_BUCKET));
        Assertions.assertFalse(minioJefwOssService.ifBucketExist(UN_EXIST_BUCKET));
    }

    @Test
    public void getDefaultService() {
        System.out.println("阿里云实现类：" + aliyunJefwOssService);
        System.out.println("腾讯云实现类：" + tencentJefwOssService);
        System.out.println("七牛云实现类：" + qiniuJefwOssService);
        System.out.println("MinIO实现类：" + minioJefwOssService);
        System.out.println("默认的实现类：" + ossTemplate.getDefaultService());
        System.out.println("默认渠道配置：" + ossTemplate.getDefaultConfig());
    }

    @Test
    public void aliyunObjCrud() {
        // 对象不存在
        Assertions.assertFalse(aliyunJefwOssService.ifObjExist(ALIYUN_EXIST_BUCKET, OBJ1));
        Assertions.assertFalse(aliyunJefwOssService.ifObjExist(ALIYUN_EXIST_BUCKET, OBJ2));

        // 上传对象
        File file = uploadFile();
        aliyunJefwOssService.putObj(ALIYUN_EXIST_BUCKET, OBJ1, file);
        aliyunJefwOssService.putObj(ALIYUN_EXIST_BUCKET, OBJ2, "这是内容".getBytes(StandardCharsets.UTF_8));

        // 对象已存在
        Assertions.assertTrue(aliyunJefwOssService.ifObjExist(ALIYUN_EXIST_BUCKET, OBJ1));
        Assertions.assertTrue(aliyunJefwOssService.ifObjExist(ALIYUN_EXIST_BUCKET, OBJ2));

        // 设置对象访问权限
        aliyunJefwOssService.setObjAcl(ALIYUN_EXIST_BUCKET, OBJ1, OssAccessPolicy.PUBLIC_READ);

        // 获取对象
        byte[] txt = aliyunJefwOssService.getObj(ALIYUN_EXIST_BUCKET, OBJ2);
        Assertions.assertEquals("这是内容", new String(txt));

        byte[] img = aliyunJefwOssService.getObj(ALIYUN_EXIST_BUCKET, OBJ1);
        FileUtil.writeBytes(img, tempDir.resolve("download.jpg").toFile());

        // 删除对象
        aliyunJefwOssService.delObj(ALIYUN_EXIST_BUCKET, OBJ1);
        aliyunJefwOssService.delObj(ALIYUN_EXIST_BUCKET, OBJ2);

        // 对象不存在
        Assertions.assertFalse(aliyunJefwOssService.ifObjExist(ALIYUN_EXIST_BUCKET, OBJ1));
        Assertions.assertFalse(aliyunJefwOssService.ifObjExist(ALIYUN_EXIST_BUCKET, OBJ2));

        // 上传对象
        aliyunJefwOssService.putObj(ALIYUN_EXIST_BUCKET, OBJ1, uploadFile(), OssAccessPolicy.PUBLIC_READ);
        aliyunJefwOssService.putObj(ALIYUN_EXIST_BUCKET, OBJ2, "这是自定义权限的内容".getBytes(StandardCharsets.UTF_8), OssAccessPolicy.PUBLIC_READ);

        // 删除对象
        aliyunJefwOssService.delObj(ALIYUN_EXIST_BUCKET, OBJ1);
        aliyunJefwOssService.delObj(ALIYUN_EXIST_BUCKET, OBJ2);

        // 对象不存在
        Assertions.assertFalse(aliyunJefwOssService.ifObjExist(ALIYUN_EXIST_BUCKET, OBJ1));
        Assertions.assertFalse(aliyunJefwOssService.ifObjExist(ALIYUN_EXIST_BUCKET, OBJ2));
    }

    @Test
    public void tencentObjCrud() {
        // 对象不存在
        Assertions.assertFalse(tencentJefwOssService.ifObjExist(TENCENT_EXIST_BUCKET, OBJ1));
        Assertions.assertFalse(tencentJefwOssService.ifObjExist(TENCENT_EXIST_BUCKET, OBJ2));

        // 上传对象
        File file = uploadFile();
        tencentJefwOssService.putObj(TENCENT_EXIST_BUCKET, OBJ1, file);
        tencentJefwOssService.putObj(TENCENT_EXIST_BUCKET, OBJ2, "这是内容".getBytes(StandardCharsets.UTF_8));

        // 对象已存在
        Assertions.assertTrue(tencentJefwOssService.ifObjExist(TENCENT_EXIST_BUCKET, OBJ1));
        Assertions.assertTrue(tencentJefwOssService.ifObjExist(TENCENT_EXIST_BUCKET, OBJ2));

        // 设置对象访问权限
        tencentJefwOssService.setObjAcl(TENCENT_EXIST_BUCKET, OBJ1, OssAccessPolicy.PUBLIC_READ);

        // 获取对象
        byte[] txt = tencentJefwOssService.getObj(TENCENT_EXIST_BUCKET, OBJ2);
        Assertions.assertEquals("这是内容", new String(txt));

        byte[] img = tencentJefwOssService.getObj(TENCENT_EXIST_BUCKET, OBJ1);
        FileUtil.writeBytes(img, tempDir.resolve("download.jpg").toFile());

        // 删除对象
        tencentJefwOssService.delObj(TENCENT_EXIST_BUCKET, OBJ1);
        tencentJefwOssService.delObj(TENCENT_EXIST_BUCKET, OBJ2);

        // 对象不存在
        Assertions.assertFalse(tencentJefwOssService.ifObjExist(TENCENT_EXIST_BUCKET, OBJ1));
        Assertions.assertFalse(tencentJefwOssService.ifObjExist(TENCENT_EXIST_BUCKET, OBJ2));

        // 上传对象
        tencentJefwOssService.putObj(TENCENT_EXIST_BUCKET, OBJ1, uploadFile(), OssAccessPolicy.PUBLIC_READ);
        tencentJefwOssService.putObj(TENCENT_EXIST_BUCKET, OBJ2, "这是自定义权限的内容".getBytes(StandardCharsets.UTF_8), OssAccessPolicy.PUBLIC_READ);

        // 删除对象
        tencentJefwOssService.delObj(TENCENT_EXIST_BUCKET, OBJ1);
        tencentJefwOssService.delObj(TENCENT_EXIST_BUCKET, OBJ2);

        // 对象不存在
        Assertions.assertFalse(tencentJefwOssService.ifObjExist(TENCENT_EXIST_BUCKET, OBJ1));
        Assertions.assertFalse(tencentJefwOssService.ifObjExist(TENCENT_EXIST_BUCKET, OBJ2));
    }

    @Test
    public void qiniuObjCrud() {
        // 对象不存在
        Assertions.assertFalse(qiniuJefwOssService.ifObjExist(QINIU_EXIST_BUCKET, OBJ1));
        Assertions.assertFalse(qiniuJefwOssService.ifObjExist(QINIU_EXIST_BUCKET, OBJ2));

        // 上传对象
        File file = uploadFile();
        qiniuJefwOssService.putObj(QINIU_EXIST_BUCKET, OBJ1, file);
        qiniuJefwOssService.putObj(QINIU_EXIST_BUCKET, OBJ2, "这是内容".getBytes(StandardCharsets.UTF_8));

        // 对象已存在
        Assertions.assertTrue(qiniuJefwOssService.ifObjExist(QINIU_EXIST_BUCKET, OBJ1));
        Assertions.assertTrue(qiniuJefwOssService.ifObjExist(QINIU_EXIST_BUCKET, OBJ2));

        // 获取对象
        byte[] txt = qiniuJefwOssService.getObj(QINIU_EXIST_BUCKET, OBJ2);
        Assertions.assertEquals("这是内容", new String(txt));

        byte[] img = qiniuJefwOssService.getObj(QINIU_EXIST_BUCKET, OBJ1);
        FileUtil.writeBytes(img, tempDir.resolve("download.jpg").toFile());

        // 删除对象
        qiniuJefwOssService.delObj(QINIU_EXIST_BUCKET, OBJ1);
        qiniuJefwOssService.delObj(QINIU_EXIST_BUCKET, OBJ2);

        // 对象不存在
        Assertions.assertFalse(qiniuJefwOssService.ifObjExist(QINIU_EXIST_BUCKET, OBJ1));
        Assertions.assertFalse(qiniuJefwOssService.ifObjExist(QINIU_EXIST_BUCKET, OBJ2));
    }

    @Test
    public void minioObjCrud() {
        // 对象不存在
        Assertions.assertFalse(minioJefwOssService.ifObjExist(MINIO_EXIST_BUCKET, OBJ1));
        Assertions.assertFalse(minioJefwOssService.ifObjExist(MINIO_EXIST_BUCKET, OBJ2));

        // 上传对象
        File file = uploadFile();
        minioJefwOssService.putObj(MINIO_EXIST_BUCKET, OBJ1, file);
        minioJefwOssService.putObj(MINIO_EXIST_BUCKET, OBJ2, "这是内容".getBytes(StandardCharsets.UTF_8));

        // 对象已存在
        Assertions.assertTrue(minioJefwOssService.ifObjExist(MINIO_EXIST_BUCKET, OBJ1));
        Assertions.assertTrue(minioJefwOssService.ifObjExist(MINIO_EXIST_BUCKET, OBJ2));

        // 获取对象
        byte[] txt = minioJefwOssService.getObj(MINIO_EXIST_BUCKET, OBJ2);
        Assertions.assertEquals("这是内容", new String(txt));

        byte[] img = minioJefwOssService.getObj(MINIO_EXIST_BUCKET, OBJ1);
        FileUtil.writeBytes(img, tempDir.resolve("download.jpg").toFile());

        // 删除对象
        minioJefwOssService.delObj(MINIO_EXIST_BUCKET, OBJ1);
        minioJefwOssService.delObj(MINIO_EXIST_BUCKET, OBJ2);

        // 对象不存在
        Assertions.assertFalse(minioJefwOssService.ifObjExist(MINIO_EXIST_BUCKET, OBJ1));
        Assertions.assertFalse(minioJefwOssService.ifObjExist(MINIO_EXIST_BUCKET, OBJ2));
    }

    @Test
    public void getPrivateUrl() {
        // 上传私有对象
        aliyunJefwOssService.putObj(ALIYUN_EXIST_BUCKET, OBJ2, "这是内容".getBytes(StandardCharsets.UTF_8), OssAccessPolicy.PRIVATE);
        tencentJefwOssService.putObj(TENCENT_EXIST_BUCKET, OBJ2, "这是内容".getBytes(StandardCharsets.UTF_8), OssAccessPolicy.PRIVATE);

        // 获取带签名的链接
        String url1 = aliyunJefwOssService.getPrivateUrl(ALIYUN_EXIST_BUCKET, OBJ2, LocalDateTime.now().plusMinutes(2));
        String url12 = aliyunJefwOssService.getPrivateUrl(ALIYUN_EXIST_BUCKET, OBJ2, LocalDateTime.now().plusMinutes(2), true);
        System.out.println("阿里云（官方域名）：" + url1);
        System.out.println("阿里云（自定义域名）：" + url12);
        String url2 = tencentJefwOssService.getPrivateUrl(TENCENT_EXIST_BUCKET, OBJ2, LocalDateTime.now().plusMinutes(2));
        String url22 = tencentJefwOssService.getPrivateUrl(TENCENT_EXIST_BUCKET, OBJ2, LocalDateTime.now().plusMinutes(2), true);
        System.out.println("腾讯云（官方域名）：" + url2);
        System.out.println("腾讯云（自定义域名）：" + url22);

        // 删除对象
        aliyunJefwOssService.delObj(ALIYUN_EXIST_BUCKET, OBJ2);
        tencentJefwOssService.delObj(TENCENT_EXIST_BUCKET, OBJ2);

        // 对象不存在
        Assertions.assertFalse(aliyunJefwOssService.ifObjExist(ALIYUN_EXIST_BUCKET, OBJ2));
        Assertions.assertFalse(tencentJefwOssService.ifObjExist(TENCENT_EXIST_BUCKET, OBJ2));
    }

    @Test
    public void listObjPage() {
        Assertions.assertTrue(aliyunJefwOssService.listObjPage(ALIYUN_EXIST_BUCKET, null, 2, null).items().size() <= 2);
        Assertions.assertTrue(tencentJefwOssService.listObjPage(TENCENT_EXIST_BUCKET, null, 2, null).items().size() <= 2);
        Assertions.assertTrue(qiniuJefwOssService.listObjPage(QINIU_EXIST_BUCKET, null, 2, null).items().size() <= 2);
        Assertions.assertTrue(minioJefwOssService.listObjPage(MINIO_EXIST_BUCKET, null, 2, null).items().size() <= 2);
    }

    private File uploadFile() {
        return FileUtil.writeUtf8String("jefw integration test", tempDir.resolve("upload.jpg").toFile());
    }

}
