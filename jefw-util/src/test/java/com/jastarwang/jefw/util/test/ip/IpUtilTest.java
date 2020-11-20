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
package com.jastarwang.jefw.util.test.ip;

import com.jastarwang.jefw.util.extra.ip.IpInfo;
import com.jastarwang.jefw.util.extra.ip.IpUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * IpUtil单元测试
 *
 * @author Jastar Wang
 * @date 2020/9/18
 * @since 1.0
 */
public class IpUtilTest {

    @Test
    public void getIpInfo() {
        String ip = "abcdefg";
        IpInfo info = IpUtil.getIpInfo(ip);
        Assert.assertNull(info);

        ip = "111.198.33.54";
        IpInfo secondInfo = IpUtil.getIpInfo(ip);
        System.out.println(secondInfo);
    }

}
