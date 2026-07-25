# 参与贡献

感谢你愿意为 `jefw` 添砖加瓦。`GitHub` 是本项目唯一的开发与发布上游，`Gitee` 仅作为只读镜像。

## 开发流程

1. Fork [GitHub 仓库](https://github.com/jastar-wang/jefw)。
2. 从 `develop` 分支创建功能分支。
3. 完成修改，并执行 `mvn verify`。
4. 向 `GitHub` 的 `develop` 分支提交 `Pull Request`。

请保持改动聚焦，并在 `Pull Request` 中说明修改原因、影响范围和验证方式。提交信息遵循 `README` 中约定的格式。

## 测试

默认验证不访问 `Redis` 或第三方供应商：

```bash
mvn verify
```

集成测试使用 `*IT` 命名，只有显式启用时才运行：

```bash
mvn -Pintegration-tests verify
```

运行集成测试前，请通过环境变量提供测试账号和服务地址。变量清单可直接查看各模块的 `src/test/resources/application.yml`。不要把真实密钥、手机号、证书或私钥提交到仓库。

## 许可证

提交代码即表示你有权提供这些内容，并同意其按照项目的 `Apache License 2.0` 许可证发布。
