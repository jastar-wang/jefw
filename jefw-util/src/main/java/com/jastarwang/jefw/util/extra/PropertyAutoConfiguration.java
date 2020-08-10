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
package com.jastarwang.jefw.util.extra;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 默认配置文件自动配置类
 * <p>
 * 用来加载默认配置文件，当系统依赖了Spring的相关类库时，才会启用该配置
 * （这里用检测 {@link org.springframework.context.annotation.Configuration} 是否存在来表示）
 * </p>
 *
 * @author Jastar Wang
 * @date 2020/08/10
 * @since 1.0
 */
@Configuration
@PropertySource("classpath:default-config.properties")
@ConditionalOnClass(Configuration.class)
public class PropertyAutoConfiguration {
}