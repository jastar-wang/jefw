# 更新日志

## 2.5.1 - 未发布

### 特性

- 【redis】新增：`jefw.redis.cache.enabled=false` 可关闭 jefw 的 `redisTemplate` 与 `RedisUtil` 自动配置；`jefw.redis.lock.enabled=false` 可独立关闭默认分布式锁。两项均默认开启。装配默认锁时需要已有 `RedissonClient` Bean，且容器中尚未注册 `DistributedLock`。若自定义锁由第三方自动配置提供，可用 `@AutoConfiguration(before = DistributedLockAutoConfiguration.class)` 提前注册；若无法调整第三方顺序，可设置 `jefw.redis.lock.enabled=false` 以避免出现两个锁。Spring Boot 自身的 Redis 配置仍按其原有条件生效
- 【weixin】新增：微信支付开关 `jefw.weixin.pay.enabled`。设为 `false` 时，仅使用公众号或小程序的应用无需配置商户私钥。省略此项时沿用原有规则，微信模块已启用且存在支付 `SDK` 时自动装配支付服务
- 【sms】新增：可选渠道名单 `jefw.sms.enabled-channels`。例如设为 `dh3tong` 时，只需配置大汉三通，即使存在腾讯云 `SDK` 也无需腾讯云凭据。设为 `tencent,dh3tong` 可同时启用两者，`jefw.sms.default-channel` 选择默认路由。省略名单时沿用腾讯云 `SDK` 与大汉三通 `api-url` 的原有装配条件。启用模块后，若名单为空或含空项、默认渠道不在名单中，或所选渠道缺少对应服务，应用会在启动时报错
- 【oss】新增：可选渠道名单 `jefw.oss.enabled-channels`。例如设为 `minio,qiniu` 时，仅装配这两个渠道，可通过 `jefw.oss.default-channel` 指定其中一个作为默认渠道。省略名单时沿用原有 `SDK` 探测规则。启用模块后，若名单为空或含空项、默认渠道不在名单内，或所选渠道既缺少 `SDK` 又无自定义服务，应用会在启动时报错
- 【oss】新增：对象分页查询方法 `listObjPage`。每页可请求 1～1000 个对象，首页传 `null`，下一页在相同渠道、桶和前缀下原样传入上一页的 `nextContinuationToken()`，令牌为 `null` 时结束
- 【oss】新增：MinIO 分页查询支持复用应用提供的 `MinioAsyncClient`，默认按现有配置创建并复用。自行配置了 MinIO 端点、凭据提供器或 HTTP 客户端的应用，需提供配置一致的 `MinioAsyncClient` Bean

### 优化

- 【redis/oss/sms/weixin】优化：外部服务集成测试按服务和渠道启用。Redis 需设置 `JEFW_REDIS_IT=true`，OSS 写入和真实短信发送分别使用独立开关。已有联调脚本请按[集成测试指南](CONTRIBUTING.md#测试)调整环境变量

### 修复

- 【redis】修复：多个 Spring 上下文共用 `RedisUtil` 时，静态方法使用最近初始化且仍存活的模板。关闭子上下文后可继续使用父上下文的模板，最后一个上下文关闭时释放引用
- 【redis】修复：第三方自动配置稍晚注册 `RedissonClient` 时也可获得默认分布式锁。若下游自动配置通过 `@ConditionalOnBean(DistributedLock.class)` 启用，建议客户端提供方声明 `@AutoConfiguration(before = DistributedLockAutoConfiguration.class)`，使锁在下游判断前可见
- 【weixin】修复：微信支付和退款回调在签名请求头大小写变化时验签失败的问题
- 【weixin】修复：公众号、小程序预支付金额转为整数分时可能被舍入或溢出。金额必须大于零、可精确换算为整数分且不超过 `21474836.47` 元，否则在发起预支付请求前抛出 `IllegalArgumentException`

### 删除

- 【oss】删除：`JefwOssService` 和 `JefwOssTemplate` 的 `listObj` 方法（不兼容）。调用方需改用 `listObjPage` 逐页处理，自定义 `JefwOssService` 实现也需实现新方法

### 依赖

- 升级`hutool`版本（`5.8.47` => `5.8.48`）
- 升级`jackson`版本（`2.21.4` => `2.21.8`），通过`jackson-bom`统一管理 Jackson 2 各模块版本
- 升级`tencent.cos`版本（`5.6.273` => `5.6.283`）
- 升级`tencent.sms`版本（`3.1.1451` => `3.2.18`）
- 升级`minio`版本（`8.6.0` => `9.0.3`）
- 升级`okhttp`版本（`4.12.0` => `5.3.2`），使用 MinIO 的 Maven 应用需在运行时提供 JVM 构件 `com.squareup.okhttp3:okhttp-jvm:5.3.2`

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
