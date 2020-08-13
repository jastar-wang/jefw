# jefw


## 前言
作为一个叱咤“搬砖”行业多年的码农本农，在工作中经常会遇到各种各样的工具类；久而久之，做的项目多了，cp 来 cp 去的感觉很繁琐，所以几年前，我就有了一个大胆的想法——整合工具类。功夫不负有心人，经过这么久的努力，我的梦想，最终还是被前辈实现了——`hutool`（笑哭）。不过话说回来，换做是我，可能没有前辈做的这么好，感谢前辈提供了这么一个又大又圆的轮子。

在流行的 `MVC` 模式中，`M` 中的持久层可以用很多优秀的开源框架（比如`MyBatisPlus`）来实现，业务层同时配合着 `hutool` 来让我们更专注于业务，而 `V` 层也有对应的解决方案，比如“干脆的 `rest` 前后端分离”、“经典的 `thymeleaf` 等模板引擎”；对于 `C` 层，其实也有一定的抽象空间……

## 介绍
所以今天，`jefw` 诞生了。

`jefw` 来源于 `Jastar Easy Framework` 的简写，生而为了三个使命：
- 对一些常用的而 `hutool` 中没有的  `util`  工具类进行补充
- 对 `controller` 层常用的一些数据模型进行封装，比如：`rest` 或 `ajax` 向前端返回的数据包装结果
- 对目前一些流行的中间件进行封装，拿过来就能用，比如：`redis`

`jefw` 的初衷——让天下没有难写的代码。

## 模块
`jefw` 采用多模块的形式开发，以达到按需依赖的目的；同时除了第三方的必要依赖，不会引用其他任何的无用类库，以保证项目独立和小巧的体积。

`jefw` 的根目录为 `com.jastarwang.jefw`

模块|状态|描述
---|---|---
jefw-all  |待开发|便捷引入所有模块
jefw-core |待开发|核心模块，包含常用的`ResponseResult`、`Page等`
jefw-util |待完善|常用的工具类，一般我配合着 `hutool` 使用，药效更好
jefw-redis|待开发|`Redis`相关，如 `RedisUtil`、`Redis`锁等等
jefw-xxx  |待发掘|其他流行组件，逐步完善……

## 快速开始

### 安装
> TODO

### 使用
> TODO

## 参与贡献

### 分支说明
分支|描述|是否接受`PR`
---|---|---
master|主分支，也是 `release` 分支|×
develop|开发分支|√

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
2. 在新建类的最顶部添加 `copyright`（注意：要在 `package` 前，没有空行）
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
3. 在类上添加文档注释
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
8. 其他规范参考《阿里巴巴Java开发规范》最新版

> 小声bb：不要问我为什么这么多规矩，作为一个有强迫症级别代码洁癖的人，就是这么`gang` [狗头]


## 反馈建议
如果您在项目使用中遇到了任何问题，或者有一些好的建议，可以及时向我们反馈，点击 [Gitee Issue](https://gitee.com/jastar-wang/jefw/issues)

## 许可证
`jefw` 采用“木兰宽松许可证第2版”开源协议，详见 [Mulan PSL v2](https://license.coscl.org.cn/MulanPSL2/index.html) 

## 友情链接
- [代码与酒](http://www.jastar-wang.com)
- [Jeek导航](http://jeeknav.jastar-wang.com)
- [北创工作室](http://studio.jastar-wang.com)
- [Resume](http://resume.jastar-wang.com)