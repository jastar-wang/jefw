<h1 align="center" style="font-family:fantasy;">Jefw</h1>
<p align="center" style="color:darkgray;">让 天 下 没 有 难 写 的 代 码</p>
<p align="center">
  <a href="https://github.com/jastar-wang/jefw/releases"><img src="https://img.shields.io/badge/release-2.5.1-orange.svg" alt="release" /></a>
  <a href="https://central.sonatype.com/artifact/com.jastar-wang/jefw-all"><img src="https://img.shields.io/maven-central/v/com.jastar-wang/jefw-all.svg" alt="maven-central" /></a>
  <img src="https://img.shields.io/badge/jdk-17+-green.svg" alt="jdk" />
  <a href="LICENSE"><img src="https://img.shields.io/badge/license-Apache%202.0-blue" alt="license" /></a>
  <a href="https://github.com/jastar-wang/jefw/actions/workflows/ci.yml"><img src="https://github.com/jastar-wang/jefw/actions/workflows/ci.yml/badge.svg" alt="GitHub Actions" /></a>
</p>

## 前言

你是否也遇到过这些烦恼？

- 项目内工具类杂乱，相同能力多套实现、命名不统一，维护成本高；
- 接口返回 `JSON` 结构五花八门，有的用 `code`，有的用 `success`，前端适配麻烦；
- `Redis` 是项目标配，`Spring` 自带的 `RedisTemplate` 写法繁琐，不够顺手；
- 对象存储、短信等基础服务厂商众多，各家 `SDK` 接口差异大，接入学习成本高；
- ……

于是，`jefw` 诞生了。`jefw` 是 `Jastar Easy Framework` 的缩写，主要能力：

- 补充一些常用但 `Hutool` 尚未覆盖的工具类（只作补充，不重复造轮子）；
- 提供基于《阿里巴巴 Java 开发手册》规范的通用响应模型 `R`；
- 封装 `Redis` 常用操作、基于 `Redisson` 的分布式锁；
- 对对象存储、短信等第三方服务做门面封装，屏蔽厂商差异，提供统一 `API`。

> 作者注：很早之前我就想整理一套自用工具类，后来发现这个方向早就有优秀前辈完成了——没错，就是 `Hutool`🐶，感谢前辈造出这么好用的轮子！

`jefw` 的目标是：让天下没有难写的代码——嗯，这句话听起来似乎有点耳熟。

## 项目介绍

`jefw` 面向 `Java 17`，由一系列工具库 + `Spring Boot` 集成模块组成。开发和发布都在 [GitHub](https://github.com/jastar-wang/jefw) 进行，[Gitee](https://gitee.com/jastar-wang/jefw) 仅作为只读镜像，不接受 `Issue` 和 `PR`。

## 快速开始

在项目 `pom.xml` 引入依赖：

```xml
<dependency>
    <groupId>com.jastar-wang</groupId>
    <artifactId>jefw-all</artifactId>
    <version>2.5.1</version>
</dependency>
```

`jefw-all` 会引入下表中的全部模块及其传递依赖。也可以按需替换 `artifactId`，例如只使用响应模型 `R` 时，引入 `jefw-core` 即可。

> 💡提示：厂商 `SDK` 为可选依赖，框架不会自动传递。例如接入腾讯云 `COS` 时，还需要在应用中添加 `com.qcloud:cos_api` 的依赖并配置相关信息。

### 统一响应包装业务结果

引入 `jefw-core` 或 `jefw-all` 即可运行这个示例，无需外部服务。

```java
import com.jastarwang.jefw.core.response.R;

class GreetingService {
    R<String> greet(String name) {
        return R.ok("你好，" + name);
    }
}
```

## 模块

| 模块            | 描述                                                                                     | 默认行为                                                                                                         | 开关配置                                                                                |
|---------------|----------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------|
| `jefw-all`    | 一键引入所有模块                                                                               | 引入全部模块，各模块按自身默认配置生效                                                                                          | 按模块分别配置                                                                             |
| `jefw-core`   | 【核心模块】提供响应数据模型、枚举扩展接口、函数式接口等基础能力                                                       | 引入即可使用                                                                                                       | 无需配置                                                                                |
| `jefw-util`   | 【常用工具】专注补充，推荐搭配 `Hutool` 使用，效果更佳                                                       | 引入即可使用                                                                                                       | 无需配置                                                                                |
| `jefw-redis`  | 【缓存模块】提供 `Redis` 数据访问和基于 `Redisson` 的分布式锁支持                                            | 缓存和分布式锁默认开启。默认锁需要 `RedissonClient` 实例                                                                        | `jefw.redis.cache.enabled=false` 关闭缓存支持<br>`jefw.redis.lock.enabled=false` 关闭默认分布式锁 |
| `jefw-oss`    | 【对象存储】整合主流厂商的对象存储服务，对外提供统一、便捷的 `API` <br>（当前支持：阿里云 `OSS`、腾讯云 `COS`、七牛云 `KODO`、`MinIO`） | 默认关闭。开启后按厂商 `SDK` 装配                                                                                         | `jefw.oss.enabled=true` 开启模块<br>`jefw.oss.enabled-channels` 限定渠道                    |
| `jefw-sms`    | 【短信服务】整合主流厂商的短信服务，对外提供统一、便捷的 `API` <br>（当前支持：阿里云、腾讯云、大汉三通）                             | 默认关闭。开启后，阿里云按短信 `SDK` 和 `jefw.sms.aliyun.access-key-id` 装配，腾讯云按 `SDK` 装配，大汉三通按 `jefw.sms.dh3tong.api-url` 装配 | `jefw.sms.enabled=true` 开启模块<br>`jefw.sms.enabled-channels` 限定渠道                    |
| `jefw-weixin` | 【微信平台】集成微信平台相关服务，对外提供统一、便捷的 `API` <br>（当前支持：微信公众号、服务号、小程序及微信支付相关接口）                    | 默认关闭。开启后提供公众号和小程序服务，有支付 `SDK` 时一并启用微信支付                                                                      | `jefw.weixin.enabled=true` 开启模块<br>`jefw.weixin.pay.enabled=false` 单独关闭微信支付         |
| `jefw-webmvc` | 【WebMVC】提供 `Servlet MVC` 控制器请求日志打印以及防重提交的功能                                            | 请求日志默认关闭，防重由方法注解启用                                                                                           | `jefw.webmvc.request-log.enabled=true` 开启日志                                         |

各模块的详细用法请查阅 [GitHub Wiki](https://github.com/jastar-wang/jefw/wiki)。版本变更记录详见 [更新日志](CHANGELOG.md)。

## 反馈与贡献

问题和建议请提交到 [GitHub Issues](https://github.com/jastar-wang/jefw/issues)。贡献代码请向 `develop` 分支提交 `PR`，开发流程与测试方式见 [贡献指南](CONTRIBUTING.md)。报告安全问题请参阅 [安全策略](SECURITY.md)。

## 许可证

`jefw` 使用 [Apache License 2.0](LICENSE)。
