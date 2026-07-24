<h1 align="center" style="font-family:fantasy;">Jefw</h1>
<p align="center" style="color:darkgray;">让 天 下 没 有 难 写 的 代 码</p>
<p align="center">
  <a target="_blank"><img src="https://img.shields.io/badge/release-2.5.0-orange.svg" alt="release" /></a>
  <a target="_blank"><img src="https://img.shields.io/maven-central/v/com.jastar-wang/jefw-all.svg" alt="maven-central" /></a>
  <a target="_blank"><img src="https://img.shields.io/badge/jdk-17+-green.svg" alt="jdk" /></a>
  <a target="_blank"><img src="https://img.shields.io/badge/license-Apache%202.0-blue" alt="license" /></a>
  <a href="https://github.com/jastar-wang/jefw/actions/workflows/ci.yml"><img src="https://github.com/jastar-wang/jefw/actions/workflows/ci.yml/badge.svg" alt="GitHub Actions" /></a>
</p>

-----------------------------------------------

## 前言

作为一名从业多年的 <del style="color:gray;font-size:12px;">专业</del> 码农，我在工作中经常遇到散落在各处的工具类。虽说复制粘贴（`CV`）一时爽，但维护起来难免头大。于是几年前，我萌生了一个大胆的想法：把这些工具类整合起来。

功夫不负有心人。经过一番努力，我发现这个目标早已被前辈抢先完成了——没错，就是 `Hutool`。[狗头] 感谢前辈造好了一个又大又圆的轮子！

## 使命

（旁白声）你是否也遇到过这些烦恼？

- 项目中的工具类风格不一，同一种功能甚至有好几套名字不同、实现相近的代码。
- 前后端分离后，`JSON` 成了数据交互的主力。为了让前端统一处理响应，我们往往需要定义一套稳定的数据包装结构。
- `Redis` 几乎成了项目标配。虽然 `Spring` 提供了 `RedisTemplate`，但部分操作写起来仍比较冗长，多少有点不够丝滑。
- 对象存储、短信发送等能力已成为常见的基础服务，而不同厂商的 `SDK` 和接口各有脾气，学习与接入成本自然也跟着上涨。
- ……

于是，`jefw` 诞生了。

`jefw` 是 `Jastar Easy Framework` 的缩写，主要做三件事：

- 补充一些常用但 `Hutool` 尚未覆盖的工具类（提示：只补充，不重复造轮子）。
- 参考《阿里巴巴 Java 开发手册》，提供 `R` 等通用响应数据模型。
- 封装常用中间件并提供 `Starter` 化支持，尽量做到引入依赖即可使用，例如 `Redis`、`Redisson` 等。

`jefw` 的目标是：让天下没有难写的代码——嗯，这句话听起来似乎有点耳熟。

## 项目介绍

`jefw` 采用多模块设计，模块之间尽量保持独立，部分模块仅依赖 `core`。项目只保留必要的第三方依赖，尽可能保持轻量、清晰，避免依赖列表长得像购物清单。

项目以 [GitHub](https://github.com/jastar-wang/jefw) 作为开发、发布和 Maven Central 部署的唯一上游仓库，并同步到 [Gitee](https://gitee.com/jastar-wang/jefw) 作为只读镜像。两个仓库的代码、分支和标签内容一致，可按网络情况任选其一下载。

```
com.jastarwang.jefw（根包）
├─ core（核心）
├─ util（工具类）
├─────└─ extra（扩展工具类，依赖第三方库）
├─ redis（缓存操作）
├─ lock（分布式锁）
├─ oss（对象存储）
├─ sms（短信发送）
├─ weixin（微信平台）
├─ ···
```

模块 | 状态 | 描述
---|---|---
`jefw-all` | √ | 一键引入所有模块
`jefw-core` | √ | 【核心模块】提供响应数据模型、枚举扩展接口、函数式接口等基础能力
`jefw-util` | √ | 【常用工具】专注补位，推荐搭配 `Hutool` 使用，效果更佳
`jefw-redis` | √ | 【缓存模块】提供 `Redis` 数据访问和基于 `Redisson` 的分布式锁支持
`jefw-oss` | √ | 【对象存储】整合主流厂商的对象存储服务，对外提供统一、便捷的 `API` <br>（当前支持：阿里云 `OSS`、腾讯云 `COS`、七牛云 `KODO`、`MinIO`）
`jefw-sms` | √ | 【短信发送】整合主流厂商的短信服务，对外提供统一、便捷的 `API` <br>（当前支持：腾讯云、大汉三通）
`jefw-weixin` | √ | 【微信平台】集成微信平台相关服务，对外提供统一、便捷的 `API` <br>（当前支持：微信公众号、服务号、小程序及微信支付相关接口）

## 快速开始

### 安装

项目发布在 `Maven Central`，无需额外配置仓库。确认本地 `Maven` 可以正常访问中央仓库后，在项目的 `pom.xml` 中添加所需依赖即可。

如果想一次引入全部模块，可以使用 `jefw-all`：

```xml
<dependency>
    <groupId>com.jastar-wang</groupId>
    <artifactId>jefw-all</artifactId>
    <version>${latestVersion}</version>
</dependency>
```

如果只需要部分功能，可将 `artifactId` 替换为对应模块，例如 `jefw-core`、`jefw-redis` 等。

### 使用

详细使用方式请参阅项目 [Gitee Wiki](https://gitee.com/jastar-wang/jefw/wikis)。

## 参与贡献

### 分支说明

分支 | 描述 | 是否接受 `PR`
---|---|---
`main` | 主分支，也是 `release` 分支 | ×
`develop` | 开发分支 | √

### 贡献步骤

1. 将本项目 `Fork` 到你的远程仓库。
2. 将 `Fork` 后的仓库 `Clone` 到本地。
3. 切换到 `develop` 分支。
4. 完成代码修改并进行必要的验证。
5. 提交（`Commit`）修改并推送（`Push`）到你的远程仓库。
6. 在 `GitHub` 发起 `Pull Request`，并说明修改内容与原因。
7. 搬好板凳，备好西瓜，等待 `Review`；如果收到修改建议，再优雅地补两刀。

### 测试

默认构建只运行不依赖外部服务的单元测试：

```bash
mvn verify
```

需要运行依赖 `Redis` 等外部服务的集成测试时，请先准备好对应服务和测试配置，再启用 `integration-tests`：

```bash
mvn -Pintegration-tests verify
```

### 代码规范

1. 推荐使用 `IntelliJ IDEA` 进行开发。
2. 在 `IDEA` 中配置版权模板，新建类时将以下声明添加到文件顶部（位于 `package` 之前）：

```
Copyright 2020 Jastar Wang

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

3. 为类添加文档注释：

```
/**
 * TODO
 *
 * @author [your name]
 * @date ${DATE}
 * @since [current version]
 */
```

4. 统一使用 4 个空格缩进。
5. 单行注释的 `//` 与正文之间保留一个空格。
6. 其他规范参考最新版《阿里巴巴 Java 开发手册》。

> 小声说一句：规矩看着不少，其实都是为了少在深夜排查问题。代码洁癖不是病，格式乱了才要命。[狗头]

### `Git` 规范

> 统一的编码、协作和提交规范，可以减少沟通成本，也能让问题追溯更高效。多花几秒写清楚提交信息，往往能让未来的自己少翻半小时日志。相关说明见：[为什么要规范 Git Commit](https://mp.weixin.qq.com/s/vzgST0ko-HZVkFFiSZ2xGg)。

#### `Commit Message` 格式

```
<type>(<scope>):<subject>
```

#### `type`（必填）

`type` 用于说明本次提交的类别，可使用以下标识：

- `feat`：新增功能
- `fix`：修复 `Bug`，可用于一次提交直接解决问题
- `to`：阶段性修改，问题尚未完全解决；最终修复时应使用 `fix`
- `docs`：文档（documentation）
- `style`：格式调整，不影响代码逻辑
- `refactor`：重构，既不新增功能，也不修复 `Bug`
- `perf`：性能或体验优化
- `test`：新增或调整测试
- `chore`：构建流程或辅助工具变动
- `revert`：回滚已有提交
- `merge`：代码合并
- `sync`：同步主线或其他分支的修改

#### `scope`（可选）

`scope` 用于说明本次提交影响的范围，例如数据层、控制层或具体模块，可根据实际情况填写。

在本项目中，可以使用 `core`、`util`、`redis`、`oss` 等模块名。修改影响多个 `scope` 时，可以使用 `*`。

#### `subject`（必填）

`subject` 是对本次提交目的的简短描述，建议不超过 50 个字符。

- 建议使用中文，表达清楚比堆砌术语更重要。
- 结尾不加句号或其他标点符号。

#### 示例

```
fix(util): 修复XXX的问题
```

## 反馈建议

如果你在使用过程中遇到问题，或有更好的建议与想法，欢迎通过 [GitHub Issue](https://github.com/jastar-wang/jefw/issues) 反馈。Gitee 仅同步代码，不单独接收 `PR` 或发布版本。问题尽管提，建议更欢迎。

## 许可证

`jefw` 采用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 开源协议。

---

既然都看到这了，那还愣着干嘛？点个 `star` 啊！！！