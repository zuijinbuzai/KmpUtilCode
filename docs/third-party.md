# 第三方依赖

本项目独立实现公共工具逻辑，基础设施复用以下库。具体版本见 `gradle/libs.versions.toml`。

| 依赖 | 用途 | 许可证/项目 |
|---|---|---|
| Kotlin / kotlinx.coroutines / kotlinx.serialization / kotlinx-datetime | 编译、协程、JSON、日历 | Apache-2.0；[Kotlin](https://github.com/JetBrains/kotlin) / [Kotlinx](https://github.com/Kotlin) |
| Okio | 多平台字节摘要和文件 I/O | Apache-2.0；[Okio](https://github.com/square/okio) |
| Ktor | HTTP 客户端/多平台引擎/MockEngine | Apache-2.0；[Ktor](https://github.com/ktorio/ktor) |
| OkHttp | Android/JVM HTTP 引擎 | Apache-2.0；[OkHttp](https://github.com/square/okhttp) |
| Gradle / Android Gradle Plugin | 工程构建 | Apache-2.0；各工具对应分发说明 |

AndroidUtilCode 仅作为产品组织方式参考，没有引入其 Android 源码或声明与其 API 兼容。发布应用前仍应审查最终解析的所有传递依赖及许可证。
