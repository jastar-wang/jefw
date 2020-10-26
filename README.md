<h1 align="center" style="font-family:fantasy;">Jefw</h1>
<p align="center" style="color:darkgray;">让 天 下 没 有 难 写 的 代 码</p>
<p align="center">
  <a target="_blank"><img src="https://img.shields.io/badge/release-1.0%20SNAPSHOT-orange.svg" alt="release" /></a>
  <a target="_blank"><img src="https://img.shields.io/badge/jdk-1.8+-green.svg" alt="jdk" /></a>
  <a target="_blank"><img src="https://img.shields.io/badge/license-MulanPSL2-blue" alt="license" /></a>
  <a target="_blank"><img src="https://img.shields.io/badge/build-passing-success.svg" alt="build" /></a>
  <a target="_blank"><img src="https://img.shields.io/badge/process-dev-inactive.svg" alt="process" /></a>
</p>

-----------------------------------------------

## 前言
作为一个深耕一线多年的码农本农，在工作中经常会遇到各种各样的工具类；久而久之，做的项目多了，cp 来 cp 去的感觉很繁琐，所以几年前，我就有了一个大胆的想法——整合工具类。功夫不负有心人，经过这么久的努力，我的梦想，最终还是被前辈实现了——`hutool`（笑哭）。不过话说回来，换做是我，可能没有前辈做的这么好，感谢前辈提供了这么一个又大又圆的轮子。

在流行的 `MVC` 模式中，`M` 中的持久层可以用很多优秀的开源框架（比如 `MyBatisPlus` ）来实现，业务层同时配合着 `hutool` 来让我们更专注于业务，而 `V` 层也有对应的解决方案，比如“干脆的 `rest` 前后端分离”、“经典的 `thymeleaf` 等模板引擎”；对于 `C` 层，其实也有一定的抽象和优化空间……

## 使命
笔者经历过很多的项目，每个项目中都有参差不齐的工具类，甚至一个项目有多个名字不同但功能一致的工具类。另外对于 `Controller` 层返回数据（`Ajax`请求），每个项目都会粘贴复制一套数据响应的基本结构，规范点的公司可能会统一封装在内部二方库中。还有对于一些中间件的封装，每个地方都会有不同的实现……这些问题，都大大降低了我们的代码规范性和可维护性。

所以今天，`jefw` 诞生了。

`jefw` 来源于 `Jastar Easy Framework` 的简写，生而为了三个使命：
- 对一些常用的而 `hutool` 中没有的  `util`  工具类进行补充（注意只做补充，不造轮子）
- 对 `controller` 层常用的数据模型进行封装，比如：`rest` 或 `ajax` 向前端返回的数据包装结果
- 对目前一些流行的中间件进行封装，拿过来就能用，比如：`redis`、`redisson`

`jefw` 的初衷——让天下没有难写的代码。

## 介绍
`jefw` 采用多模块的形式开发，以达到按需依赖的目的；同时除了第三方的必要依赖，不会引用其他任何的无用类库，以保证项目独立和小巧的体积。

```
com.jastarwang.jefw（根目录）
├─ core（核心）
├─ util（工具类）
├─────└─ extra（扩展工具类，依赖三方库）
├───────────├─ ip
├───────────├─ jwt
├───────────└─ spring
├─ redis（Redis模块）
├─ xxx（其他组件）
├─ ···
```

模块|状态|描述
---|---|---
jefw-all  |√|一键引入所有模块
jefw-core |0%|核心模块；包含常用的 `ResponseResult`、`Page等`
jefw-util |√|常用工具；因为只做补充，所以推荐搭配 `hutool` 使用，药效更好
jefw-redis|√|`Redis`模块；提供对存取数据、分布式锁的便捷支持
jefw-xxx  |待调研|其他流行组件，逐步完善……

## 快速开始

### 安装
前面提到，`jefw` 采用多模块开发，各模块间互不依赖，所以根据自己的需求引入对应模块即可，当然，我们也提供了便捷引入所有模块的方法：
```xml
<dependency>
    <groupId>com.jastarwang</groupId>
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
  * @author your niubility name
  * @date ${DATE}
  * @since 1.0
  */
```
4. 设置一个 `tab` 为4个空格
5. 设置单行注释不在行首，且与描述之间插入空格
6. 设置对于多个参数的方法文档注释，参数的描述不进行右对齐
7. 设置 `import` 导包 不使用 `*` 模式导入
8. 其他规范参考《阿里巴巴Java开发手册》最新版

> 小声bb：不要问我为什么这么多规矩，作为一个有强迫症级别代码洁癖的人，就是这么`gang` [狗头]

### Git规范（阿里）
> 编码规范、流程规范在软件开发过程中是至关重要的，它可以使我们在开发过程中少走很多弯路。Git `commit` 规范也是如此，确实也是很有必要的，几乎不花费额外精力和时间，但在之后查找问题的效率却很高。作为一名程序员，我们更应注重代码和流程的规范性，永远不要在质量上将就。详见 https://mp.weixin.qq.com/s/vzgST0ko-HZVkFFiSZ2xGg

#### commit message 格式
```
<type>(<scope>):<subject>
```

#### type（必须）
用于说明git commit的类别，只允许使用下面的标识。
- `feat`：新功能（feature）
- `fix/to`：修复bug，可以是QA发现的BUG，也可以是研发自己发现的BUG
- - `fix`：产生diff并自动修复此问题。适合于一次提交直接修复问题
- - `to`：只产生diff不自动修复此问题。适合于多次提交。最终修复问题提交时使用fix
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

例如在`Angular`，可以是`location`，`browser`，`compile`，`compile`，`rootScope`，`ngHref`，`ngClick`，`ngView`等。如果你的修改影响了不止一个`scope`，你可以使用 `*` 代替。

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
如果您在项目使用中遇到了任何问题，或者有一些更好的建议和想法，可以及时向我们反馈，点击 [Gitee Issue](https://gitee.com/jastar-wang/jefw/issues)

还望各位老板，动动您的小手，点下 `star`~

## 许可证
`jefw` 采用“木兰宽松许可证第2版”开源协议，详见 [Mulan PSL v2](https://license.coscl.org.cn/MulanPSL2/index.html) 

## 友情链接
- [代码与酒](http://www.jastar-wang.com)
- [Jeek导航](http://jeeknav.jastar-wang.com)
- [北创工作室](http://studio.jastar-wang.com)
- [Resume](http://resume.jastar-wang.com)