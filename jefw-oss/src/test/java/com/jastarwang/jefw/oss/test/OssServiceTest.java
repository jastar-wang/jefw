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
package com.jastarwang.jefw.oss.test;

import cn.hutool.core.io.FileUtil;
import com.jastarwang.jefw.oss.OssAccessPolicy;
import com.jastarwang.jefw.oss.OssService;
import com.jastarwang.jefw.oss.config.JefwOssProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * OSS对象存储单元测试
 *
 * @author Jastar Wang
 * @date 2021/09/14
 * @since 1.2.0
 */
@Disabled
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@SpringBootTest
@SpringBootConfiguration
public class OssServiceTest {

    private static final String UN_EXIST_BUCKET = "adsfdasfdfdallllmkjd";
    private static final String OBJ1 = "test/demo.jpg";
    private static final String OBJ2 = "test/fun/readme.txt";
    private String ALIYUN_EXIST_BUCKET;
    private String TENCENT_EXIST_BUCKET;

    @Resource
    private JefwOssProperties properties;

    @Resource
    @Qualifier("ossServiceAliyunImpl")
    private OssService aliyunOssService;

    @Resource
    @Qualifier("ossServiceTencentImpl")
    private OssService tencentOssService;

    @PostConstruct
    public void init() {
        // 初始化用来测试的真实存在的bucket
        ALIYUN_EXIST_BUCKET = properties.getAliyun().getBucketName();
        TENCENT_EXIST_BUCKET = properties.getTencent().getBucketName();
    }


    @Test
    public void contextLoads() {
        System.out.println(properties);
        Assertions.assertNotNull(properties, "属性配置对象未正常注入");
        Assertions.assertFalse(aliyunOssService.ifBucketExist(UN_EXIST_BUCKET));
        Assertions.assertFalse(tencentOssService.ifBucketExist("abcdefg-123456"));
    }

    @Test
    public void aliyunObjCrud() {
        // 对象不存在
        Assertions.assertFalse(aliyunOssService.ifObjExist(ALIYUN_EXIST_BUCKET, OBJ1));
        Assertions.assertFalse(aliyunOssService.ifObjExist(ALIYUN_EXIST_BUCKET, OBJ2));

        // 上传对象
        File file = new File("F:\\upload.jpg");
        aliyunOssService.putObj(ALIYUN_EXIST_BUCKET, OBJ1, file);
        aliyunOssService.putObj(ALIYUN_EXIST_BUCKET, OBJ2, "这是内容".getBytes(StandardCharsets.UTF_8));

        // 对象已存在
        Assertions.assertTrue(aliyunOssService.ifObjExist(ALIYUN_EXIST_BUCKET, OBJ1));
        Assertions.assertTrue(aliyunOssService.ifObjExist(ALIYUN_EXIST_BUCKET, OBJ2));

        // 设置对象访问权限
        aliyunOssService.setObjAcl(ALIYUN_EXIST_BUCKET, OBJ1, OssAccessPolicy.PUBLIC_READ);

        // 获取对象
        byte[] txt = aliyunOssService.getObj(ALIYUN_EXIST_BUCKET, OBJ2);
        Assertions.assertEquals("这是内容", new String(txt));

        byte[] img = aliyunOssService.getObj(ALIYUN_EXIST_BUCKET, OBJ1);
        FileUtil.writeBytes(img, "F:\\download.jpg");

        // 删除对象
        aliyunOssService.delObj(ALIYUN_EXIST_BUCKET, OBJ1);
        aliyunOssService.delObj(ALIYUN_EXIST_BUCKET, OBJ2);

        // 对象不存在
        Assertions.assertFalse(aliyunOssService.ifObjExist(ALIYUN_EXIST_BUCKET, OBJ1));
        Assertions.assertFalse(aliyunOssService.ifObjExist(ALIYUN_EXIST_BUCKET, OBJ2));

        // 上传对象
        aliyunOssService.putObj(ALIYUN_EXIST_BUCKET, OBJ1, new File("F:\\upload.jpg"), OssAccessPolicy.PUBLIC_READ);
        aliyunOssService.putObj(ALIYUN_EXIST_BUCKET, OBJ2, "这是自定义权限的内容".getBytes(StandardCharsets.UTF_8), OssAccessPolicy.PUBLIC_READ);

        // 删除对象
        aliyunOssService.delObj(ALIYUN_EXIST_BUCKET, OBJ1);
        aliyunOssService.delObj(ALIYUN_EXIST_BUCKET, OBJ2);

        // 对象不存在
        Assertions.assertFalse(aliyunOssService.ifObjExist(ALIYUN_EXIST_BUCKET, OBJ1));
        Assertions.assertFalse(aliyunOssService.ifObjExist(ALIYUN_EXIST_BUCKET, OBJ2));
    }

    @Test
    public void tencentObjCrud() {
        // 对象不存在
        Assertions.assertFalse(tencentOssService.ifObjExist(TENCENT_EXIST_BUCKET, OBJ1));
        Assertions.assertFalse(tencentOssService.ifObjExist(TENCENT_EXIST_BUCKET, OBJ2));

        // 上传对象
        File file = new File("F:\\upload.jpg");
        tencentOssService.putObj(TENCENT_EXIST_BUCKET, OBJ1, file);
        tencentOssService.putObj(TENCENT_EXIST_BUCKET, OBJ2, "这是内容".getBytes(StandardCharsets.UTF_8));

        // 对象已存在
        Assertions.assertTrue(tencentOssService.ifObjExist(TENCENT_EXIST_BUCKET, OBJ1));
        Assertions.assertTrue(tencentOssService.ifObjExist(TENCENT_EXIST_BUCKET, OBJ2));

        // 设置对象访问权限
        tencentOssService.setObjAcl(TENCENT_EXIST_BUCKET, OBJ1, OssAccessPolicy.PUBLIC_READ);

        // 获取对象
        byte[] txt = tencentOssService.getObj(TENCENT_EXIST_BUCKET, OBJ2);
        Assertions.assertEquals("这是内容", new String(txt));

        byte[] img = tencentOssService.getObj(TENCENT_EXIST_BUCKET, OBJ1);
        FileUtil.writeBytes(img, "F:\\download.jpg");

        // 删除对象
        tencentOssService.delObj(TENCENT_EXIST_BUCKET, OBJ1);
        tencentOssService.delObj(TENCENT_EXIST_BUCKET, OBJ2);

        // 对象不存在
        Assertions.assertFalse(tencentOssService.ifObjExist(TENCENT_EXIST_BUCKET, OBJ1));
        Assertions.assertFalse(tencentOssService.ifObjExist(TENCENT_EXIST_BUCKET, OBJ2));

        // 上传对象
        tencentOssService.putObj(TENCENT_EXIST_BUCKET, OBJ1, new File("F:\\upload.jpg"), OssAccessPolicy.PUBLIC_READ);
        tencentOssService.putObj(TENCENT_EXIST_BUCKET, OBJ2, "这是自定义权限的内容".getBytes(StandardCharsets.UTF_8), OssAccessPolicy.PUBLIC_READ);

        // 删除对象
        tencentOssService.delObj(TENCENT_EXIST_BUCKET, OBJ1);
        tencentOssService.delObj(TENCENT_EXIST_BUCKET, OBJ2);

        // 对象不存在
        Assertions.assertFalse(tencentOssService.ifObjExist(TENCENT_EXIST_BUCKET, OBJ1));
        Assertions.assertFalse(tencentOssService.ifObjExist(TENCENT_EXIST_BUCKET, OBJ2));
    }

    @Test
    public void getPrivateUrl() {
        // 上传私有对象
        aliyunOssService.putObj(ALIYUN_EXIST_BUCKET, OBJ2, "这是内容".getBytes(StandardCharsets.UTF_8), OssAccessPolicy.PRIVATE);
        tencentOssService.putObj(TENCENT_EXIST_BUCKET, OBJ2, "这是内容".getBytes(StandardCharsets.UTF_8), OssAccessPolicy.PRIVATE);

        // 获取带签名的链接
        String url1 = aliyunOssService.getPrivateUrl(ALIYUN_EXIST_BUCKET, OBJ2, LocalDateTime.now().plusMinutes(2));
        System.out.println("阿里云：" + url1);
        String url2 = tencentOssService.getPrivateUrl(TENCENT_EXIST_BUCKET, OBJ2, LocalDateTime.now().plusMinutes(2));
        System.out.println("腾讯云：" + url2);

        // 删除对象
        aliyunOssService.delObj(ALIYUN_EXIST_BUCKET, OBJ2);
        tencentOssService.delObj(TENCENT_EXIST_BUCKET, OBJ2);

        // 对象不存在
        Assertions.assertFalse(aliyunOssService.ifObjExist(ALIYUN_EXIST_BUCKET, OBJ2));
        Assertions.assertFalse(tencentOssService.ifObjExist(TENCENT_EXIST_BUCKET, OBJ2));
    }

}
