# 参与贡献

感谢你愿意为 `jefw` 添砖加瓦。`GitHub` 是本项目唯一的开发与发布上游，`Gitee` 仅作为只读镜像。

## 开发流程

1. Fork [GitHub 仓库](https://github.com/jastar-wang/jefw)。
2. 从 `develop` 分支创建功能分支。
3. 完成修改，并执行 `mvn verify`。
4. 向 `GitHub` 的 `develop` 分支提交 `Pull Request`。

请保持改动聚焦，并在 `Pull Request` 中说明修改原因、影响范围和验证方式。`main` 用于发布版本。提交信息格式见下文的 [Git 提交规范](#git-提交规范)。

## 测试

默认验证不访问 `Redis` 或第三方服务：

```bash
mvn verify
```

### 用测试账号验证外部服务

集成测试代码和配置模板随模块一起维护。测试账号、手机号和证书路径通过本机环境变量传入，私钥文件保存在仓库之外。表中的链接列出每个模块读取的配置。

| 模块 | 启用条件 | 测试内容 |
| --- | --- | --- |
| [Redis](jefw-redis/src/test/resources/application.yml) | `JEFW_REDIS_IT=true` | 在独立的测试 Redis 中验证数据操作与分布式锁 |
| [对象存储](jefw-oss/src/test/resources/application.yml) | `JEFW_OSS_IT=true`，`JEFW_OSS_DEFAULT_CHANNEL` 选 `aliyun`、`tencent`、`qiniu` 或 `minio` | 在所选渠道验证分页。加设 `JEFW_OSS_WRITE_IT=true` 后另测写入、读取、预签名和删除 |
| [短信](jefw-sms/src/test/resources/application.yml) | `JEFW_SMS_IT=true`，`JEFW_SMS_DEFAULT_CHANNEL` 选 `tencent` 或 `dh3tong` | 验证所选渠道装配。加设 `JEFW_SMS_SEND=true` 会向测试号码实际发送短信，可能计费 |
| [微信](jefw-weixin/src/test/resources/application.yml) | `JEFW_WEIXIN_IT=true`，`JEFW_WEIXIN_SCENARIO` 选 `oa`、`mp` 或 `pay` | 公众号、小程序分别获取访问令牌。支付场景验证 SDK 装配，初始化时可能下载平台证书 |

以 MinIO 为例，先在测试桶中放入 `fixtures/list/a.txt`、`fixtures/list/b.txt` 和 `fixtures/list/c.txt`。该前缀下只保留这三个对象，创建文件时避开名称以 `/` 结尾的目录占位对象。配置好 MinIO 的端点、桶名和测试账号后，在 PowerShell 中运行：

```powershell
$env:JEFW_OSS_IT = 'true'
$env:JEFW_OSS_DEFAULT_CHANNEL = 'minio'
$env:JEFW_OSS_LIST_PREFIX = 'fixtures/list/'
mvn -pl jefw-oss -am -Pintegration-tests verify
```

每页上限为 2，三个已知对象可检验续页令牌、跨页重复和遗漏。分页用例保留这些对象供下次使用。写入用例另建随机名称的对象并清理。七牛的下载用例还需配置测试桶的 `JEFW_OSS_QINIU_URL_PREFIX`。

短信发送用例还需设置 `JEFW_SMS_PHONES`（单个测试号码），以及所选渠道的 `JEFW_SMS_TENCENT_TEMPLATE_ID` 或 `JEFW_SMS_DH3TONG_TEMPLATE`。腾讯云模板的两个参数依次为验证码和分钟数，大汉三通模板使用 `{code}` 和 `{time}` 占位。

微信支付场景会用商户私钥签署一段本地测试文本，核对商户证书序列号。将私钥放在仓库外，通过测试类路径提供给 SDK。例如，设置 `JEFW_WEIXIN_PAY_PRIVATE_KEY_PATH=apiclient_key.pem` 后，可用以下命令指定该文件所在的目录：

```powershell
mvn -pl jefw-weixin -am -Pintegration-tests "-Dmaven.test.additionalClasspath=D:\secrets\wechat" verify
```

验证其他服务时，将命令中的 `jefw-oss` 替换为对应模块。Maven 的 `integration-tests` Profile 负责发现 `*IT`，环境变量决定实际执行哪些用例。联调后查看模块的 `target/failsafe-reports`，确认目标用例已执行。`Skipped` 表示该用例跳过。发布前可对受影响的渠道分别运行并记录结果。

## 代码规范

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

## Git 提交规范

> 统一的编码、协作和提交规范，可以减少沟通成本，也能让问题追溯更高效。多花几秒写清楚提交信息，往往能让未来的自己少翻半小时日志。相关说明见：[为什么要规范 Git Commit](https://mp.weixin.qq.com/s/vzgST0ko-HZVkFFiSZ2xGg)。

### `Commit Message` 格式

```
<type>(<scope>):<subject>
```

### `type`（必填）

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

### `scope`（可选）

`scope` 用于说明本次提交影响的范围，例如数据层、控制层或具体模块，可根据实际情况填写。

在本项目中，可以使用 `core`、`util`、`redis`、`oss` 等模块名。修改影响多个 `scope` 时，可以使用 `*`。

### `subject`（必填）

`subject` 是对本次提交目的的简短描述，建议不超过 50 个字符。

- 建议使用中文，表达清楚比堆砌术语更重要。
- 结尾不加句号或其他标点符号。

### 示例

```
fix(util): 修复XXX的问题
```

## 许可证

提交代码即表示你有权提供这些内容，并同意其按照项目的 `Apache License 2.0` 许可证发布。
