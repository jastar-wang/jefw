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

import com.jastarwang.jefw.oss.JefwOssService;
import com.jastarwang.jefw.oss.JefwOssTemplate;
import com.jastarwang.jefw.oss.OssAccessPolicy;
import com.jastarwang.jefw.oss.OssChannel;
import com.jastarwang.jefw.oss.model.ObjectPage;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 在一个已配置的存储桶中验证所选渠道。分页用例读取预置对象，写入用例需单独启用。
 *
 * @author Jastar Wang
 * @date 2021/09/14
 * @since 1.2.0
 */
@EnabledIfEnvironmentVariable(named = "JEFW_OSS_IT", matches = "true")
@SpringBootTest(classes = JefwOssTestConfig.class)
class JefwOssServiceIT {

    private final List<String> uploadKeys = new ArrayList<>();

    @Resource
    private JefwOssTemplate template;

    @Resource
    private JefwOssService service;

    @Test
    void selectsOnlyConfiguredProvider() {
        assertSame(service, template.getDefaultService());
        assertEquals(template.getProperties().getDefaultChannel(), service.getChannel());
    }

    @Test
    void listsAllFixtureKeysAcrossTwoPages() {
        String prefix = System.getenv("JEFW_OSS_LIST_PREFIX");
        assertTrue(prefix != null && !prefix.isBlank(), "请设置 JEFW_OSS_LIST_PREFIX 指向分页测试专用前缀");
        if (!prefix.endsWith("/")) {
            prefix += "/";
        }
        // 专用前缀下仅放置三个文件，不创建同名目录占位对象。
        ObjectPage first = template.listObjPage(prefix, 2, null);
        assertEquals(2, first.items().size(), "首页应包含两个预置对象");
        String token = first.nextContinuationToken();
        assertTrue(token != null && !token.isBlank(), "首页应返回续页令牌");
        ObjectPage last = template.listObjPage(prefix, 2, token);
        assertEquals(1, last.items().size(), "末页应包含剩余的一个对象");
        assertNull(last.nextContinuationToken(), "末页应结束分页");

        Set<String> keys = new HashSet<>();
        first.items().forEach(item -> keys.add(item.getKey()));
        last.items().forEach(item -> keys.add(item.getKey()));
        assertEquals(Set.of(prefix + "a.txt", prefix + "b.txt", prefix + "c.txt"), keys);
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "JEFW_OSS_WRITE_IT", matches = "true")
    void uploadsReadsAndDeletesOwnObject(@TempDir Path tempDir) throws IOException {
        // 两种上传方式使用独立对象键，防止下载域名缓存前一次上传的内容。
        String prefix = "jefw-it/" + UUID.randomUUID() + "/";
        String fileKey = prefix + "file.txt";
        String bytesKey = prefix + "bytes.txt";
        assertFalse(template.ifObjExist(fileKey), "文件上传键必须尚未使用");
        assertFalse(template.ifObjExist(bytesKey), "字节上传键必须尚未使用");
        byte[] content = "对象存储集成测试".getBytes(StandardCharsets.UTF_8);
        Path file = Files.writeString(tempDir.resolve("upload.txt"), "文件上传测试");
        uploadKeys.add(fileKey);
        template.putObj(fileKey, file.toFile());
        assertTrue(template.ifObjExist(fileKey));
        assertArrayEquals(Files.readAllBytes(file), template.getObj(fileKey));

        // 阿里云和腾讯云支持对象级 ACL；其他渠道使用桶的访问策略。
        OssChannel channel = template.getDefaultChannel();
        uploadKeys.add(bytesKey);
        if (channel == OssChannel.ALIYUN || channel == OssChannel.TENCENT) {
            template.putObj(bytesKey, content, OssAccessPolicy.PRIVATE);
            template.setObjAcl(bytesKey, OssAccessPolicy.PRIVATE);
        } else {
            template.putObj(bytesKey, content);
        }
        assertArrayEquals(content, template.getObj(bytesKey));
        LocalDateTime expires = LocalDateTime.now().plusMinutes(5);
        assertSignedUrl(template.getPrivateUrl(bytesKey, expires), bytesKey);
        String domain = template.getDefaultConfig().getUrlPrefix();
        if (domain != null && !domain.isBlank() && channel != OssChannel.QINIU) {
            URI customUrl = assertSignedUrl(template.getPrivateUrl(bytesKey, expires, true), bytesKey);
            assertEquals(URI.create(domain).getAuthority(), customUrl.getAuthority());
        }
    }

    /** 上传前登记键，即使服务端已写入而客户端超时，也会尝试清理每个对象。 */
    @AfterEach
    void cleanUpUploads() {
        assertAll("清理本次测试对象", uploadKeys.stream().map(key -> () -> {
            template.delObj(key);
            assertFalse(template.ifObjExist(key), () -> "测试对象未删除：" + key);
        }));
    }

    private URI assertSignedUrl(String url, String key) {
        URI uri = URI.create(url);
        assertNotNull(uri.getHost(), "签名地址应包含主机名");
        assertTrue(uri.getPath().endsWith("/" + key), "签名地址应指向当前测试对象");
        assertTrue(uri.getRawQuery() != null && !uri.getRawQuery().isBlank(), "签名地址应包含签名参数");
        return uri;
    }
}
