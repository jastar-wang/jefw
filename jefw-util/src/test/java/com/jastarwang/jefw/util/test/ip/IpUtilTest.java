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
package com.jastarwang.jefw.util.test.ip;

import com.jastarwang.jefw.util.extra.ip.IpInfo;
import com.jastarwang.jefw.util.extra.ip.IpUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * IpUtil单元测试
 *
 * @author Jastar Wang
 * @date 2020/9/18
 * @since 1.0
 */
public class IpUtilTest {

    @DisplayName("解析IP地址")
    @Test
    public void getIpInfo() {
        String ip = "abcdefg";
        IpInfo info = IpUtil.getIpInfo(ip);
        Assertions.assertNull(info);

        ip = "111.198.33.54";
        IpInfo secondInfo = IpUtil.getIpInfo(ip);
        System.out.println(secondInfo);
    }

}
