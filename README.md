<h1 align="center" style="font-family:fantasy;">Jefw</h1>
<p align="center" style="color:darkgray;">让 天 下 没 有 难 写 的 代 码</p>
<p align="center">
  <a target="_blank"><img src="https://img.shields.io/badge/release-2.1.1-orange.svg" alt="release" /></a>
  <a target="_blank"><img src="https://jitpack.io/v/com.gitee.jastar-wang/jefw.svg" alt="jitpack" /></a>
  <a target="_blank"><img src="https://img.shields.io/badge/jdk-17+-green.svg" alt="jdk" /></a>
  <a target="_blank"><img src="https://img.shields.io/badge/license-MulanPSL2-blue" alt="license" /></a>
  <a target="_blank"><img src="https://img.shields.io/badge/process-release-success.svg" alt="process" /></a>
  <a target="_blank"><img src="https://img.shields.io/badge/build-passing-inactive.svg" alt="build" /></a>
</p>

-----------------------------------------------

## 前言

作为一名混迹多年的 <del style="color:gray;font-size:12px;">专业</del> 码农，在工作中经常会遇到各种工具类，虽有`CV`大法但也甚是繁琐； so，几年前，吾有一个大胆的想法：整合工具类。

功夫不负有心人，经过不懈的努力，这个目标终于被前辈抢先完成了[狗头]——`hutool`，感谢前辈提供了一个又大又圆的轮子！

## 使命

（旁白声）你是否也有以下的烦恼？

- 项目中有参差不齐的工具类，甚至一个项目有多个名字不同但功能相同的工具类。
- 现在是前后端分离的时代，`json`是和前端交互的利器，我们经常需要定义一套固定的结构来包装响应数据，以便前端能统一处理请求（有规范者则会封装在内部二方库）。
- `Redis`可以说是当今项目中的必备组件了，虽然`春天`提供了`RedisTemplate`，但是方法写起来很长很费劲，不够丝滑。
- 对象云存储、发送短信等已然成为了开发中必备的基础服务，但是面对各大厂商的不同`SDK`，这无疑加大了我们的学习成本。
- ……

所以今天，`jefw` 诞生了。

`jefw` 来源于 `Jastar Easy Framework` 的简写，生而为了：

- 对一些常用的且 `hutool` 中没有的工具类进行补充（Tips：只做补充，不造轮子）
- 参考《阿里巴巴Java开发手册》的规范，定义了一套通用的响应数据包装模型`ResponseResult`等
- 对目前一些流行的中间件进行封装（或`starter`化），拎包入住，开箱即用。比如：`redis`、`redisson`等

`jefw` 的目标——让天下没有难写的代码（哪里怪怪的）。

## 介绍

`jefw` 采用多模块设计，各模块间互不依赖（部分模块依赖`core`包）。同时除了第三方的必要依赖，不会引用其他任何的无用库，以保证项目的独立和小巧的体积。

```
com.jastarwang.jefw（根包）
├─ core（核心）
├─ util（工具类）
├─────└─ extra（扩展工具类，依赖三方库）
├───────────├─ ip
├───────────└─ spring
├─ redis（缓存操作）
├─ lock（分布式锁）
├─ oss（对象存储）
├─ sms（短信发送）
├─ weixin（微信平台）
├─ ···
```

模块|状态|描述
---|---|---
jefw-all     |√|一键引入所有模块
jefw-core    |√|【核心模块】提供常用的数据模型定义；如：响应数据模型、枚举能力扩展接口、函数式接口
jefw-util    |√|【常用工具】只做补充，所以强烈推荐搭配 `hutool` 使用，药效更好
jefw-redis   |√|【缓存模块】提供对存取`redis`数据、分布式锁`redisson`的便捷支持
jefw-oss     |√|【对象存储】整合主流厂商对象存储并对外提供统一便捷的API调用 <br>（Tips：目前已支持`阿里云OSS`、`腾讯云COS`、`七牛云KODO`）
jefw-sms     |√|【短信发送】整合主流厂商短信服务并对外提供统一便捷的API调用 <br>（Tips：目前已支持`腾讯云`、`大汉三通`）
jefw-weixin  |√|【微信平台】集成微信平台相关的服务并对外提供统一便捷的API调用 <br>（Tips：目前已支持`微信开发平台（公众号/服务号、小程序）`、`微信支付`的接口调用）

## 快速开始

### 安装

（1）首先在项目的`pom.xml`中引入仓库源：

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

（2）如果你本地`maven`的`settings.xml`配置了阿里云的仓库源（没有请跳过）如下：

```xml

<mirror>
    <id>aliyunmaven</id>
    <mirrorOf>*</mirrorOf>
    <name>阿里云公共仓库</name>
    <url>https://maven.aliyun.com/repository/public</url>
</mirror>
```

需要把`<mirrorOf>*</mirrorOf>`修改为`<mirrorOf>*,!jitpack.io</mirrorOf>`或者`<mirrorOf>central</mirrorOf>`
，否则所有依赖会始终从阿里云仓库加载，导致加载失败。

（3）引入本项目，可按需依赖。当然，也支持一键引入所有模块：

```xml

<dependency>
    <groupId>com.jastar-wang.jefw</groupId>
    <artifactId>jefw-all</artifactId>
    <version>${jefw.latestVersion}</version>
</dependency>
```

### 使用

详细使用文档请移步本项目 [Wiki](https://gitee.com/jastar-wang/jefw/wikis)

## 参与贡献

### 分支说明

分支|描述|是否接受`PR`
---|---|---
`master`|主分支，也是 `release` 分支|×
`develop`|开发分支|√

### 贡献步骤

1. `fork` 本项目到你的远程仓库
2. `clone` 你的远程仓库到本地
3. 本地切换到 `develop` 分支
4. 修改代码
5. `commit` 并 `push` 到你的远程仓库
6. 登录 `Gitee` 或 `Github`，新建 `Pull Request`，并填写必要说明
7. 搬上板凳，吃上西瓜，坐等合并

### 代码规范

1. 开发工具使用 `IDEA`
2. 设置在新建类的最顶部添加 `copyright`（注意：要在 `package` 前，没有空行）

```
Copyright (c) 2020 Jastar Wang
jefw is licensed under Mulan PSL v2.
You can use this software according to the terms and conditions of the Mulan PSL v2.
You may obtain a copy of Mulan PSL v2 at:
         http://license.coscl.org.cn/MulanPSL2
THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
See the Mulan PSL v2 for more details.
```

3. 设置在类上添加文档注释

```
/**
  * TODO
  * @author [your niubility name]
  * @date ${DATE}
  * @since [current version]
  */
```

4. 设置一个 `tab` 为4个空格
5. 设置单行注释不在行首，且与描述之间插入空格
6. 设置 `import` 导包 不使用 `*` 模式导入
7. 其他规范参考《阿里巴巴Java开发手册》最新版

> 小声bb：不要问我为什么这么多规矩，作为一个有强迫症级别代码洁癖的人，就是这么`gang` [狗头]

### Git规范

> 编码规范、流程规范在软件开发过程中是至关重要的，它可以使我们在开发过程中少走很多弯路。Git `commit` 规范也是如此，确实也是很有必要的，几乎不花费额外精力和时间，但在之后查找问题的效率却很高。作为一名程序员，我们更应注重代码和流程的规范性，永远不要在质量上将就。详见 https://mp.weixin.qq.com/s/vzgST0ko-HZVkFFiSZ2xGg

#### commit message 格式

```
<type>(<scope>):<subject>
```

#### type（必须）

用于说明git commit的类别，只允许使用下面的标识。

- `feat`：新功能（feature）
- `fix/to`：修复bug，可以是QA发现的BUG，也可以是研发自己发现的BUG
-
    - `fix`：产生diff并自动修复此问题。适合于一次提交直接修复问题
-
    - `to`：只产生diff不自动修复此问题。适合于多次提交。最终修复问题提交时使用fix
- `docs`：文档（documentation）
- `style`：格式（不影响代码运行的变动）
- `refactor`：重构（即不是新增功能，也不是修改bug的代码变动）
- `perf`：优化相关，比如提升性能、体验
- `test`：增加测试
- `chore`：构建过程或辅助工具的变动
- `revert`：回滚到上一个版本
- `merge`：代码合并
- `sync`：同步主线或分支的Bug

#### scope（可选）

`scope` 用于说明 `commit` 影响的范围，比如数据层、控制层、视图层等等，视项目不同而不同。

例如在`Angular`，可以是`location`，`browser`，`compile`，`compile`，`rootScope`，`ngHref`，`ngClick`，`ngView`等。如果你的修改影响了不止一个`scope`
，你可以使用 `*` 代替。

#### subject（必须）

`subject`是 `commit` 目的的简短描述，不超过50个字符。

- 建议使用中文（感觉中国人用中文描述问题能更清楚一些）
- 结尾不加句号或其他标点符号

#### e.g.

```
fix(DAO):用户查询缺少username属性 
feat(Controller):用户查询接口开发
```

## 反馈建议

如果你在项目使用中遇到了任何问题，或者有一些更好的建议和想法，可以及时向我反馈，机票：[Gitee Issue](https://gitee.com/jastar-wang/jefw/issues)

## 许可证

`jefw` 采用“木兰宽松许可证第2版”开源协议，机票：[Mulan PSL v2](http://license.coscl.org.cn/MulanPSL2)

---

愣着干嘛？点`star`啊！！！