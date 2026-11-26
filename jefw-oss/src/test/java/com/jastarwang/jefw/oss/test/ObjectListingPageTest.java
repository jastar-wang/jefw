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

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ListObjectsV2Request;
import com.aliyun.oss.model.ListObjectsV2Result;
import com.aliyun.oss.model.OSSObjectSummary;
import com.jastarwang.jefw.oss.impl.AliyunJefwOssService;
import com.jastarwang.jefw.oss.impl.MinioJefwOssService;
import com.jastarwang.jefw.oss.impl.QiniuJefwOssService;
import com.jastarwang.jefw.oss.impl.TencentJefwOssService;
import com.jastarwang.jefw.oss.model.ObjectPage;
import com.jastarwang.jefw.oss.model.QiniuOssClient;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ListObjectsRequest;
import com.qcloud.cos.model.ObjectListing;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.model.FileListing;
import io.minio.MinioAsyncClient;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * 验证各对象存储渠道的单页请求、续页令牌及页大小限制。
 *
 * @author Jastar Wang
 * @since 2.5.1
 */
class ObjectListingPageTest {
    private static final String BUCKET = "test-bucket";
    private static final String PREFIX = "images/";
    private static final String TOKEN = "next+/=";

    @Test
    void aliyunKeepsTokenWhenFilteringTheEntireFirstPage() {
        OSS client = mock(OSS.class);
        var service = new AliyunJefwOssService(client, null);
        OSSObjectSummary directory = new OSSObjectSummary();
        directory.setKey(PREFIX);
        ListObjectsV2Result first = new ListObjectsV2Result();
        first.addObjectSummary(directory);
        first.setTruncated(true);
        first.setNextContinuationToken(TOKEN);
        OSSObjectSummary object = new OSSObjectSummary();
        object.setKey(PREFIX + "cat.jpg");
        ListObjectsV2Result last = new ListObjectsV2Result();
        last.addObjectSummary(object);
        when(client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(first, last);

        ObjectPage page = service.listObjPage(BUCKET, PREFIX, 1, null);
        assertTrue(page.items().isEmpty());
        assertEquals(TOKEN, page.nextContinuationToken());
        verify(client).listObjectsV2(any(ListObjectsV2Request.class));
        ObjectPage next = service.listObjPage(BUCKET, PREFIX, 1, page.nextContinuationToken());
        assertEquals(object.getKey(), next.items().get(0).getKey());
        assertNull(next.nextContinuationToken());

        var requests = ArgumentCaptor.forClass(ListObjectsV2Request.class);
        verify(client, times(2)).listObjectsV2(requests.capture());
        assertNull(requests.getAllValues().get(0).getContinuationToken());
        assertEquals(TOKEN, requests.getValue().getContinuationToken());
        assertEquals(PREFIX, requests.getValue().getPrefix());
        assertEquals(1, requests.getValue().getMaxKeys());
    }

    @Test
    void tencentPassesTheNativeMarkerAndLimit() {
        COSClient client = mock(COSClient.class);
        ObjectListing result = new ObjectListing();
        result.setTruncated(true);
        result.setNextMarker(TOKEN);
        when(client.listObjects(any(ListObjectsRequest.class))).thenReturn(result);
        var service = new TencentJefwOssService(client, null);

        ObjectPage page = service.listObjPage(BUCKET, PREFIX, 1000, "previous");
        var request = ArgumentCaptor.forClass(ListObjectsRequest.class);
        verify(client).listObjects(request.capture());
        assertEquals("previous", request.getValue().getMarker());
        assertEquals(1000, request.getValue().getMaxKeys());
        assertEquals(PREFIX, request.getValue().getPrefix());
        assertEquals(TOKEN, page.nextContinuationToken());
    }

    @Test
    void qiniuUsesMarkerEvenWhenItemsAreAbsent() throws Exception {
        QiniuOssClient client = mock(QiniuOssClient.class);
        BucketManager manager = mock(BucketManager.class);
        when(client.getBucketManager()).thenReturn(manager);
        FileListing first = new FileListing();
        first.marker = TOKEN;
        when(manager.listFiles(BUCKET, PREFIX, null, 5, null)).thenReturn(first);
        when(manager.listFiles(BUCKET, PREFIX, TOKEN, 5, null)).thenReturn(new FileListing());
        var service = new QiniuJefwOssService(client, null);

        ObjectPage page = service.listObjPage(BUCKET, PREFIX, 5, null);
        assertTrue(page.items().isEmpty());
        assertEquals(TOKEN, page.nextContinuationToken());
        verify(manager).listFiles(BUCKET, PREFIX, null, 5, null);
        verifyNoMoreInteractions(manager);
        assertNull(service.listObjPage(BUCKET, PREFIX, 5, page.nextContinuationToken()).nextContinuationToken());
    }

    @Test
    void minioReadsOneS3XmlPagePerCall() throws Exception {
        List<HttpUrl> requests = new CopyOnWriteArrayList<>();
        OkHttpClient http = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request request = chain.request();
            requests.add(request.url());
            boolean first = request.url().queryParameter("continuation-token") == null;
            String contents = first ? "<NextContinuationToken>" + TOKEN + "</NextContinuationToken>"
                    : "<Contents><Key>images%2Fcat%20photo.jpg</Key><Size>12</Size>"
                    + "<LastModified>2026-01-01T00:00:00.000Z</LastModified><ETag>abc</ETag><StorageClass>STANDARD</StorageClass></Contents>";
            String xml = "<ListBucketResult><Name>test-bucket</Name><Prefix>images%2F</Prefix><MaxKeys>1</MaxKeys>"
                    + "<KeyCount>1</KeyCount><EncodingType>url</EncodingType><IsTruncated>"
                    + first + "</IsTruncated>" + contents + "</ListBucketResult>";
            return new Response.Builder().request(request).protocol(Protocol.HTTP_1_1)
                    .code(200).message("OK").header("Content-Type", "application/xml")
                    .body(ResponseBody.create(xml, MediaType.get("application/xml"))).build();
        }).build();
        try (var client = MinioAsyncClient.builder().endpoint("https://storage.example.org")
                .region("us-east-1").credentials("test-access", "test-secret")
                .httpClient(http, true).build()) {
            var service = new MinioJefwOssService(null, null, client);
            ObjectPage first = service.listObjPage(BUCKET, PREFIX, 1, null);
            assertEquals(1, requests.size());
            assertEquals(TOKEN, first.nextContinuationToken());
            ObjectPage last = service.listObjPage(BUCKET, PREFIX, 1, first.nextContinuationToken());
            assertEquals(2, requests.size());
            assertEquals("2", requests.get(1).queryParameter("list-type"));
            assertEquals("1", requests.get(1).queryParameter("max-keys"));
            assertEquals(PREFIX, requests.get(1).queryParameter("prefix"));
            assertEquals(TOKEN, requests.get(1).queryParameter("continuation-token"));
            assertEquals("images/cat photo.jpg", last.items().get(0).getKey());
            assertNull(last.nextContinuationToken());
        }
    }

    @Test
    void rejectsOutOfRangePageSizesBeforeContactingAnySdk() {
        var services = List.of(new AliyunJefwOssService(null, null), new TencentJefwOssService(null, null),
                new QiniuJefwOssService(null, null), new MinioJefwOssService(null, null));
        for (var service : services) {
            assertThrows(IllegalArgumentException.class, () -> service.listObjPage(BUCKET, null, 0, null));
            assertThrows(IllegalArgumentException.class, () -> service.listObjPage(BUCKET, null, 1001, null));
        }
    }
}
