# 文档索引

按以下顺序可从零接入、浏览示例并扩展组件。

1. [接入指南](getting-started.md)：工具链、源码依赖、按需依赖、Maven Local、iOS Framework。
2. [三端 Demo](demo.md)：Android/iOS/JVM 的运行、界面和所有示例。
3. [架构](architecture.md)：模块图、平台矩阵、线程模型、发布与替换。
4. [API 索引](api-index.md)：公开类与函数入口。
5. 模块手册：[基础工具](modules/core.md)、[编码](modules/codec.md)、[时间](modules/time.md)、[协程](modules/coroutines.md)、[JSON](modules/serialization.md)、[存储](modules/storage.md)、[文件](modules/file.md)、[网络](modules/network.md)、[平台](modules/platform.md)。
6. [迁移参考](migration.md)、[测试](testing.md)、[路线图](roadmap.md)。

文档中的 Kotlin 示例默认写在 KMP 的 `commonMain`，平台专属调用会注明 source set。运行时有副作用的示例会说明目录、命名空间或网络地址。
