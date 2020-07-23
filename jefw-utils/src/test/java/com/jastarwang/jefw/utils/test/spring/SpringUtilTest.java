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
package com.jastarwang.jefw.utils.test.spring;

import com.jastarwang.jefw.utils.extra.spring.SpringUtil;
import com.jastarwang.jefw.utils.test.bean.Demo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * SpringUtil单元测试
 *
 * @author Jastar Wang
 * @date 2020/7/23
 * @since 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {SpringUtil.class, Demo.class})
public class SpringUtilTest {

    @Test
    public void getApplicationContext() {
        ApplicationContext applicationContext = SpringUtil.getApplicationContext();
        Assert.assertNotNull(applicationContext);
    }

    @Test
    public void getBeanByName() {
        Demo demo1 = SpringUtil.getBean("demo");
        Demo demo2 = SpringUtil.getBean("demo2");
        Assert.assertNotNull(demo1);
        Assert.assertNotNull(demo2);
    }

    @Test
    public void getBeanByType() {
        Assert.assertThrows(NoUniqueBeanDefinitionException.class, () -> {
            SpringUtil.getBean(Demo.class);
        });
    }

    @Test
    public void getBeanByNameType() {
        Demo demo2 = SpringUtil.getBean("demo2", Demo.class);
        Assert.assertNotNull(demo2);
        Assert.assertEquals(demo2.getAge().longValue(), 10);
        Assert.assertEquals(demo2.getName(), "臭弟弟");
    }

}
