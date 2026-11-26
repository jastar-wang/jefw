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

import com.jastarwang.jefw.oss.config.JefwOssProperties;
import com.jastarwang.jefw.oss.impl.MinioJefwOssService;
import io.minio.MinioClient;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 使用固定地域和测试凭据，离线验证 MinIO 下载链接的签名。
 *
 * @author Jastar Wang
 * @since 2.5.1
 */
class MinioSigningTest {
    @Test
    void signsDownloadWithConfiguredRegionOffline() throws Exception {
        try (MinioClient client = MinioClient.builder()
                .endpoint("https://storage.example.org")
                .credentials("test-access", "test-secret")
                .region("us-east-1")
                .build()) {
            MinioJefwOssService service = new MinioJefwOssService(client, new JefwOssProperties.Minio());
            URI url = URI.create(service.getPrivateUrl("sample-bucket", "documents/test.txt", null));
            assertEquals("storage.example.org", url.getHost());
            assertEquals("/sample-bucket/documents/test.txt", url.getPath());
            assertTrue(url.getRawQuery().contains("X-Amz-Signature="));
        }
    }
}
