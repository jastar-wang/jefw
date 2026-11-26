# 更新日志

## 2.6.0-SNAPSHOT - 开发中

### 特性

- 【mail】新增文本和 HTML 邮件发送，发送时向调用方报告配置或投递错误。可通过 `jefw.mail.from` 单独指定发件地址。
- 【redis】新增键过期事件订阅，默认关闭。启用时需在 Redis 服务端开启过期事件通知。
- 【webmvc】新增 MVC 请求日志，默认关闭。启用后记录控制器的入参、响应和耗时，默认脱敏字段包括 `password`、`accessToken`、`refreshToken` 和 `sessionKey`，支持按接口关闭日志。
- 【webmvc】新增 `@RequestRepeatLimit`，支持基于 Redis 的时间窗口防重复提交，重复请求的响应由应用决定。

### 依赖

- 【weixin】`jefw-weixin` 新增传递依赖 `hutool-http` 和 `hutool-json`，供默认微信服务发起 HTTP 请求和处理 JSON。已引入 `hutool-all` 的应用升级时请检查依赖树，避免聚合包与分包重复提供相同的类。
- `jefw-all` 新增 `jefw-mail` 依赖，并传递 `spring-boot-starter-mail`。也可单独引入 `jefw-mail`。组件使用 Boot 配置的 SMTP 账号时，其专用发送器默认要求 STARTTLS 并校验服务器身份，SMTPS 默认校验服务器身份。自定义发送器和 JNDI 会话沿用宿主的传输设置。
- `jefw-all` 新增 `jefw-webmvc` 依赖，并传递 `spring-boot-starter-aop`。也可单独引入 `jefw-webmvc`，仅使用请求日志时无需 Redis。

---

## 2.5.1 - 2026-11-24

### 从 2.5.0 升级

- **OSS 对象列举接口不兼容。** `JefwOssTemplate` 和 `JefwOssService` 已移除 `listObj`。调用方请改用 `listObjPage`，自定义 `JefwOssService` 实现也需实现该方法。每页可请求 1～1000 个对象，首页令牌传 `null`。后续请求须沿用同一渠道、桶和前缀，原样传回 `nextContinuationToken()`。即使本页为空，也要等令牌为 `null` 才结束。
- **MinIO 运行时依赖和客户端。** 使用 MinIO 的 Maven 应用需在运行时提供 `com.squareup.okhttp3:okhttp-jvm:5.3.2`。分页使用 `MinioAsyncClient`，默认按现有配置创建并复用。若应用为同步客户端自定义了端点、凭据提供器或 HTTP 客户端，请提供配置一致的 `MinioAsyncClient` Bean。直接调用或覆写 `MinioJefwOssAutoConfiguration.minioJefwOssService` 的代码还需适配新增的第三个参数 `ObjectProvider<MinioAsyncClient>`，常规自动装配由 Spring 注入。
- **OSS 和短信的重复渠道会导致启动失败。** 框架模板现在会在初始化时指出冲突渠道，避免默认调用与按渠道调用选中不同服务。请为每个启用渠道保留一个服务。替换内置实现时，可给自定义 Bean 使用对应的内置名称，如 `minioJefwOssService` 或 `dh3tongJefwSmsService`。

### 特性

- 【oss/sms】可用 `jefw.oss.enabled-channels` 和 `jefw.sms.enabled-channels` 分别选择渠道，只需配置所选厂商。例如，OSS 可设为 `minio,qiniu` 并用 `jefw.oss.default-channel=qiniu` 指定默认路由。短信可设为 `dh3tong`，或用 `tencent,dh3tong` 同时启用两者。省略渠道名单时，OSS 沿用原有 SDK 探测规则。省略 `default-channel` 时，默认使用 Spring 注入顺序中的第一个已启用服务。模块启用后，渠道名单为空或含空项、默认渠道不在名单内、所选渠道缺少服务，都会在启动时报错。
- 【sms】新增阿里云短信。引入可选依赖 `com.aliyun:dysmsapi20170525:4.6.0`，设置 `jefw.sms.enabled=true` 和 `jefw.sms.enabled-channels=aliyun`，并在 `jefw.sms.aliyun` 下配置 `access-key-id`、`access-key-secret` 和 `sign-name`。调用 `send` 时传入模板 CODE 和按名称填写的变量，非空变量值会转换成文本。省略渠道名单时，阿里云按短信 SDK 和非空白的 `access-key-id` 装配，腾讯云按 SDK 装配，大汉三通按 `api-url` 装配。阿里云与其他内置渠道同时启用且未指定默认渠道时，优先使用阿里云。
- 【redis】可用 `jefw.redis.cache.enabled=false` 关闭 jefw 的 `redisTemplate` 和 `RedisUtil`，用 `jefw.redis.lock.enabled=false` 独立关闭默认分布式锁。两项均默认开启。默认锁要求容器中已有 `RedissonClient` 且尚未注册 `DistributedLock`。Spring Boot 自身的 Redis 配置仍按原有条件生效。
- 【weixin】新增 `jefw.weixin.pay.enabled=false`。仅使用公众号或小程序的应用可关闭微信支付，省去商户私钥配置。省略此项时，微信模块已启用且支付 SDK 在场就会自动装配支付服务。

### 修复

- 【sms】按渠道发送短信时，若渠道不可用，抛出 `IllegalArgumentException` 并列出请求渠道及当前可用渠道。
- 【redis】多个 Spring 上下文共用 `RedisUtil` 时，使用最近初始化且仍存活的模板。关闭子上下文后可继续使用父上下文的模板，关闭最后一个上下文时释放引用。
- 【redis】第三方自动配置稍晚注册 `RedissonClient` 时也可获得默认锁。若下游配置依赖 `@ConditionalOnBean(DistributedLock.class)`，建议客户端提供方通过 `@AutoConfiguration(before = DistributedLockAutoConfiguration.class)` 提前注册。第三方提供自定义锁时，也可通过相同方式提前注册，或关闭 jefw 默认锁以避免两个锁并存。
- 【weixin】支付和退款回调验签时忽略签名请求头名称的大小写。
- 【weixin】公众号、小程序预支付金额必须大于零、可精确换算为整数分，且不超过 `21474836.47` 元。金额不符合条件时，在发起预支付请求前抛出 `IllegalArgumentException`。

### 依赖

- `jefw-oss` 传递 `hutool-core`，`jefw-sms` 传递 `hutool-core`、`hutool-http` 和 `hutool-json`。应用无需再为这两个模块单独补充 Hutool。
- `jefw-redis` 传递默认缓存序列化所需的 `jackson-databind`、`jackson-datatype-jsr310`、`jackson-datatype-jdk8` 和 `jackson-module-parameter-names`。这些依赖也随 `jefw-all` 引入。使用 Redis 缓存时仍需提供 Spring Data Redis，使用默认分布式锁时仍需提供 Redisson 和 `RedissonClient`。

| 依赖 | 2.5.0 | 2.5.1 |
| --- | --- | --- |
| Hutool | `5.8.47` | `5.8.48` |
| Jackson（通过 `jackson-bom` 统一管理 Jackson 2 各模块） | `2.21.4` | `2.21.8` |
| 腾讯云 COS SDK | `5.6.273` | `5.6.283` |
| 腾讯云短信 SDK | `3.1.1451` | `3.2.18` |
| 阿里云短信 SDK（可选） | — | `4.6.0` |
| MinIO | `8.6.0` | `9.0.3` |
| OkHttp | `4.12.0` | `5.3.2` |

### 测试

- 外部服务集成测试改为按服务和渠道启用。Redis 使用 `JEFW_REDIS_IT=true`，OSS 写入和真实短信发送分别使用独立开关。已有联调脚本的环境变量见[贡献指南](CONTRIBUTING.md#测试)。

---

## 2.5.0 - 2026-07-25（Maven正式版）

📢这是 Maven Central 的首个正式版本，各模块的 `groupId` 为 `com.jastar-wang`。

### 特性

- 【all】新增：Maven Central 发布所需的项目元数据、源码包、Javadoc 包和签名发布配置
- 【all】新增：GitHub Actions 持续集成，使用 JDK 17 执行构建与单元测试
- 【oss/sms/weixin】新增：由 Spring Boot 配置处理器自动生成配置元数据

### 优化

- 【all】优化：统一 Apache License 2.0 许可证声明，并将许可证文件打包至各模块 JAR
- 【all】优化：外部服务集成测试与普通单元测试分离，默认构建不再访问真实第三方服务
- 【oss/sms/weixin】优化：供应商实现仅捕获 `Exception`，不再拦截 JVM `Error`

### 删除

- 【util】删除：`SerializeUtil` 及相关测试
- 【util】删除：`IpUtil`、`IpInfo` 及相关测试

### 依赖

- 升级`hutool`版本（`5.8.44` => `5.8.47`）
- 升级`jackson`版本（`2.20.0` => `2.21.4`）
- 升级`spring-boot`版本（`3.5.3` => `3.5.16`）
- 升级`tencent.cos`版本（`5.6.269` => `5.6.273`）

---

## 2.3.0 - 2026-04-27

### 特性

- 【core】新增：语义更为简洁的 `R` 包装类，推荐使用此类，`ResponseResult` 可能在未来删除
- 【oss】新增：支持 `MinIO` 平台

### 优化

- 【all】优化：自动装配时的空校验
- 【all】优化：对关键配置信息 `toString()` 时脱敏处理

### 依赖

- 升级`hutool`版本（`5.8.39` => `5.8.44`）
- 升级`lombok`版本（`1.18.38` => `1.18.46`）
- 升级`aliyun.oss`版本（`3.18.2` => `3.18.5`）
- 升级`tencent.cos`版本（`5.6.247` => `5.6.269`）
- 升级`tencent.sms`版本（`3.1.1179` => `3.1.1451`）
- 升级`redisson`版本（`3.50.0` => `3.52.0`）
- 升级`jackson`版本（`2.19.1` => `2.20.0`）

---

## 2.2.0 - 2025-07-04

### 特性

- 【weixin】新增：首次集成微信平台服务，支持微信开发(公众号、服务号、小程序)、微信支付的相关接口便捷调用
- 【redis】新增：`incrementAndExpire`方法，保证自增及设置过期时间操作的原子性

### 优化

- 【sms】优化：发送短信接口的参数强制使用有序的`LinkedHashMap`，防止调用方疏漏顺序（不兼容）

### 依赖

- 升级`hutool`版本（`5.8.36` => `5.8.39`）
- 升级`lombok`版本（`1.18.34` => `1.18.38`）
- 升级`spring-boot`版本（`3.4.3` => `3.5.3`）
- 升级`aliyun.oss`版本（`3.18.1` => `3.18.2`）
- 升级`tencent.cos`版本（`5.6.244` => `5.6.247`）
- 升级`qiniu.kodo`版本（`7.18.0` => `7.19.0`）
- 升级`tencent.wechat-pay`版本（`0.2.15` => `0.2.17`）
- 升级`redisson`版本（`3.36.0` => `3.50.0`）
- 升级`jackson`版本（`2.14.3` => `2.19.1`）

---

## 2.1.1 - 2025-04-23

### 特性

- 【redis】新增：`setIfAbsent`等相关方法

### 优化

- 【all】优化：对象存储模块的配置信息提示

---

## 2.1.0 - 2025-04-22

### 特性

- 【oss】新增：支持七牛云平台
- 【sms】优化：短信调用异常类的命名（不兼容）

---

## 2.0.0 - 2025-03-11（jdk 17+）

### 特性

- 【all】重要：从`2.0.0`开始，全面升级至`SpringBoot 3.x`，`JDK`最低要求`17+`；支持`JDK 8`的老版本已归档至`1.x`
  分支，仅作紧急`bug`修复，不再添加新特性。

---

## 1.3.3 - 2025-02-26

### 依赖

- 【all】升级三方库依赖版本

---

## 1.3.2 - 2024-09-28

### 依赖

- 【all】升级三方库依赖版本，以解决部分CVE漏洞

---

## 1.3.1 - 2023-05-10

### 特性

- 【oss】新增：方法`getConfig`，更便捷、动态的获取存储配置
- 【oss】新增：方法`listObj`，支持根据前缀查询对象列表的功能
- 【oss】新增：方法`getPrivateUrl`重载，支持预签名地址使用官方域名或自定义域名
- 【oss】新增：操作模板类`JefwOssTemplate`中加入装饰方法，支持使用默认服务及其配置调用方法

### 优化

- 【oss】优化：方法`prototype`重命名为`getClient`，更能见名知意（不兼容）

---

## 1.3.0 - 2023-04-09（JitPack正式版）

> Tips：重大更新，建议基于此版本开始使用。

### 特性

- 【core】新增：`ResponseResult`支持修改默认响应信息
- 【core】新增：`ResponseResult`支持格式化输出的方法
- 【sms】新增：短信服务模块，支持同时存在多个实现，并提供统一的操作模板类
- 【oss】重构：支持同时存在多个对象存储实现，并提供统一的操作模板类（不兼容）

### 优化

- 【all】优化：类、变量、方法等定义（部分不兼容）

### 依赖

- 【all】升级`spring-boot`版本（`2.7.6` => `2.7.10`）
- 【all】升级`jackson`版本（`2.14.1` => `2.14.2`）
- 【oss】升级`aliyun.sdk.oss`（`3.13.2` => `3.16.2`）
- 【oss】升级`tencent.sdk.cos`（`5.6.123` => `5.6.139`）
- 【util】升级`hutool`版本（`5.8.10` => `5.8.16`）
- 【sms】新增`tencent.cloud.sdk`依赖（`3.1.728`）

---

## [1.2.4] - 2023-02-20

### 特性

- 【oss】重构：以bean的形式重新实现对象存储服务，而非工具类，并添加腾讯云厂商实现（不兼容）
- 【redis】重构：添加core包依赖，调整分布式锁包结构，修改锁接口函数参数（不兼容），并新增无返回值接口

---

## [1.2.3] - 2023-01-07

### 特性

- 【core】新增：无参数无返回值的函数式接口`Execute`
- 【redis】新增：分布式锁不阻塞等待的接口

### 优化

- 【redis】优化：加锁失败异常类的命名（不兼容）
- 【redis】优化：分布式锁参数的详细注释
- 【redis】优化：`RedissonClient`注入方式改为构造方法
- 【redis】优化：去除`RedissonClient`的自动配置，因为大多数情况下都需要自主配置，或直接引入`redisson-spring-boot-starter`

### 删除

- 【jwt】删除`JwtUtil`工具类（`hutool`已在5.7.0引入实现）
- 【redis】删除`jedis`客户端依赖，因为`spring-boot`默认自带了`lettuce`

### 依赖

- 【all】升级`spring-boot`版本（`2.7.5` => `2.7.6`）

---

## [1.2.2] - 2022-11-24

### 特性

- 【core】新增：枚举工具类`Enums`重载方法
- 【util】新增：请求工具类`WebUtil`重载方法
- 【redis】新增：`RedisUtil`重载方法

### 修复

- 【redis】修复：Jackson序列化默认不支持Java8时间类型的问题

### 优化

- 【core】枚举接口`Enumable`加入泛型以解决实现方法时出现警告的问题（不兼容）
- 【core】枚举工具`Enums`优化方法命名以达到见名知意（不兼容）

### 依赖

- 【all】升级`spring-boot`版本（`2.5.6` => `2.7.5`）
- 【all】升级`jackson`版本（`2.13.0` => `2.14.1`）
- 【redis】升级`jedis`版本（`3.6.3` => `4.3.1`）
- 【redis】升级`redisson`版本（`3.16.4` => `3.18.0`）
- 【util】升级`hutool`版本（`5.7.16` => `5.8.10`）

---

## [1.2.1] - 2022-04-15（JitPack预览版）

### 修复

- 【core】修复部分文档及注释描述错误
- 【oss】修复阿里云客户端销毁时未关闭的问题

### 优化

- 【all】全面升级`Junit4`至`Junit5`的单元测试

### 依赖

- 【all】升级`spring-boot`版本（`2.5.3` => `2.5.6`）
- 【oss】升级`aliyun-sdk-oss`版本（`3.13.1` => `3.13.2`）
- 【redis】升级`jedis`版本（`3.3.0` => `3.6.3`）
- 【redis】升级`redisson`版本（`3.16.1` => `3.16.4`）
- 【util】升级`hutool`版本（`5.7.5` => `5.7.16`）

---

## [1.2.0] - 2021-09-16

### 特性

- 【oss】新增:`AliyunOssUtil`工具类，目前仅支持阿里云
- 【jwt】新增：拆分出`jwt`独立模块，以规范设计扩展

### 优化

- 【all】规范`optional`和`provided`的使用场景

### 删除

- 【util】删除`jwt`的相关信息，修复错误设计

---

## [1.1.3-beta] - 2021-09-07

### 特性

- 【core】`Enums`工具类支持通过`value`反射获取

### 修复

- 【util】修复因删除`SpringUtil`工具类而导致自动注入失败的问题

---

## [1.1.2-beta] - 2021-08-23

### 优化

- 【core】枚举增强功能拆分出独立工具类`Enums`
- 【redis】优化并补充部分封装的方法

### 删除

- 【util】删除`SpringUtil`工具类（`hutool`已在5.1引入实现）

---

## [1.1.1-beta] - 2021-08-10

### 特性

- 【core】新增：`ResponseResult`重载方法，放开自定义参数限制

### 优化

- 【core】优化：`ResponseCode`重命名，符合接口命名规范
- 【core】优化：`ResponseCode`默认状态码及返回信息重新定义

---

## [1.1.0-beta] - 2021-07-28

### 特性

- 【core】新增：枚举扩展能力接口

### 依赖

- 【all】升级`spring-boot`版本（`2.3.1.RELEASE` => `2.5.3`）
- 【all】升级`hutool`版本（`5.4.2` => `5.7.5`）
- 【all】升级`redisson`版本（`3.13.6` => `3.16.1`）

---

## [1.0.0-alpha] - 2020-11-20

问世！

---

```
## [version] - yyyy-MM-dd
### 特性
- 【模块】新增XXX
### 修复
- 【模块】修复XXX（issue/pr#编号@Git平台）
### 优化
- 【模块】优化XXX
### 依赖
- 【模块】升级XXX到XXX版本
### 过期
- 【模块】过期XXX
### 删除
- 【模块】删除XXX
### 贡献者
感谢以下所有为这个版本作出贡献的人：
- 张三
- 李四
```
