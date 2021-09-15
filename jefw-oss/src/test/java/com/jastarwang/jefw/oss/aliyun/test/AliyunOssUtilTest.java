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
package com.jastarwang.jefw.oss.aliyun.test;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.DeleteObjectsResult;
import com.aliyun.oss.model.ListObjectsV2Result;
import com.aliyun.oss.model.OSSObjectSummary;
import com.jastarwang.jefw.oss.aliyun.AliyunOssBaseProperties;
import com.jastarwang.jefw.oss.aliyun.AliyunOssUtil;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * AliyunOssUtil单元测试
 *
 * @author Jastar Wang
 * @date 2021/09/14
 * @since 1.2.0
 */
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@SpringBootTest
@SpringBootConfiguration
public class AliyunOssUtilTest {

    private static final String EXIST_BUCKET = "shenzhou-expo-test";
    private static final String UN_EXIST_BUCKET = "adsfdasfdfdallllmkjd";
    private static final String OBJ = "test/demo.jpg";
    private static final String OBJ2 = "test/fun/readme.txt";

    @Resource
    private AliyunOssBaseProperties properties;

    // 1
    @Ignore
    @Test
    public void contextLoads() {
        System.out.println(properties);
        Assert.notNull(properties, "属性配置对象未正常注入");
    }

    // 2
    @Ignore
    @Test
    public void ifBucketExist() {
        System.out.println(AliyunOssUtil.ifBucketExist(UN_EXIST_BUCKET));
    }

    // 3
    // 5
    @Ignore
    @Test
    public void ifObjExist() {
        System.out.println(AliyunOssUtil.ifObjExist(UN_EXIST_BUCKET, OBJ));

        System.out.println(AliyunOssUtil.ifObjExist(EXIST_BUCKET, OBJ));
    }

    // 8
    @Ignore
    @Test
    public void setObjAcl() {
        AliyunOssUtil.setObjAcl(EXIST_BUCKET, OBJ, CannedAccessControlList.PublicRead);
    }

    // 7
    @Ignore
    @Test
    public void listObj() {
        ListObjectsV2Result result = AliyunOssUtil.listObj(EXIST_BUCKET, "test/fun");
        if (result != null && CollUtil.isNotEmpty(result.getObjectSummaries())) {
            List<OSSObjectSummary> objs = result.getObjectSummaries();
            objs.forEach(item -> Console.log(item.getKey()));
        }
    }

    // 9
    @Ignore
    @Test
    public void deleteObj() {
        AliyunOssUtil.deleteObj(EXIST_BUCKET, "this_is_un_exist_obj.png");
        AliyunOssUtil.deleteObj(EXIST_BUCKET, OBJ2);
    }

    // 10
    @Ignore
    @Test
    public void deleteObjs() {
        DeleteObjectsResult result = AliyunOssUtil.deleteObj(EXIST_BUCKET, Arrays.asList(OBJ, OBJ2));
        if (result != null) {
            List<String> deletedObjects = result.getDeletedObjects();
            deletedObjects.forEach(Console::log);
        }
    }

    // 4
    @Ignore
    @Test
    public void putObj() {
        File file = new File("C:\\Users\\EDZ\\Desktop\\demo.jpg");
        AliyunOssUtil.putObj(EXIST_BUCKET, OBJ, file);

        AliyunOssUtil.putObj(EXIST_BUCKET, OBJ2, "这是内容".getBytes(StandardCharsets.UTF_8));
    }

    // 6
    @Ignore
    @Test
    public void getObj() {
        byte[] txt = AliyunOssUtil.getObj(EXIST_BUCKET, OBJ2);
        System.out.println(new String(txt));

        byte[] img = AliyunOssUtil.getObj(EXIST_BUCKET, OBJ);
        FileUtil.writeBytes(img, "C:\\Users\\EDZ\\Desktop\\download.jpg");
    }

    // 11
    @Ignore
    @Test
    public void putObjByAcl() {
        AliyunOssUtil.putObj(EXIST_BUCKET, OBJ, new File("C:\\Users\\EDZ\\Desktop\\demo.jpg"), CannedAccessControlList.PublicRead);

        AliyunOssUtil.putObj(EXIST_BUCKET, OBJ2, "这是自定义权限的内容".getBytes(StandardCharsets.UTF_8), CannedAccessControlList.PublicRead);
    }

}
