# RongMC 旧版欢迎页

简体中文 · [English](./README.en.md)

建筑服旧客户端的首启欢迎页和可选模组开关。可显示欢迎文案、保存已完成状态，并按整合包提供的目录管理可选客户端 JAR。

## 下载与安装

- 版本 · [v1.0.0+26.3](https://github.com/OpenRongMC/RongMC-Welcome/releases/tag/v1.0.0%2B26.3) (已发布)
- 下载文件 · `rongmc-welcome-1.0.0.26.3.jar`
- 运行环境 · Minecraft Java 26.3, Fabric, Java 25
- Fabric Loader · `>=0.19.3`
- Fabric 模组 ID · `rongmc_welcome`
- JAR 声明的依赖 · [Fabric API](https://modrinth.com/mod/fabric-api)

把发行 JAR 放入对应实例的 `mods/` 目录。用于明确配套此旧欢迎模组的客户端实例。当前 RongMC 低配整合包的五档设备选择由 RongMC Client 提供。

## 如何验证

用空配置的测试实例检查首启文案，切换一个可选模组并重启，确认欢迎页状态和启用列表均保存。

## 从源码开始

```sh
./gradlew tasks
```

先用 Java 25 检查当前源码树提供的任务，再选择对应的构建任务。

## 文档与反馈

- [原 README（旧版指南、贡献者与署名）](https://github.com/OpenRongMC/RongMC-Welcome/blob/fc3c1160e3824082bdac9f8046025029f313e9f7/README.md)
- [RongMC 客户端与实时导览](https://github.com/OpenRongMC/RongMC-Client)
- [报告 RongMC 移植问题](https://github.com/OpenRongMC/RongMC-Welcome/issues) · 请附游戏版本、两端 JAR 文件名、复现步骤和相关日志。

## 许可与署名

请参阅本仓库的 [LICENSE](./LICENSE) 及原项目的许可、署名和第三方资源说明。
