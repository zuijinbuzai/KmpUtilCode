# KmpUtilCode

面向 **Kotlin Multiplatform** 的模块化通用工具库，支持 **Android、iOS、JVM**。

借鉴 [AndroidUtilCode](https://github.com/Blankj/AndroidUtilCode) 对常用开发能力统一封装、配套文档与 Demo 的组织方式，采用全新 KMP 实现。无需全局初始化；通用算法放入 `commonMain`，系统能力通过 `expect/actual` 或 Android 专属 API 暴露。

> 当前为 **0.1.0-SNAPSHOT 首版**，已实现下表中的 9 个能力模块和 17 组跨端示例。不是 AndroidUtilCode 的完整 API 移植；相机、蓝牙、定位、数据库等扩展见 [路线图](docs/roadmap.md)。当前未发布到 Maven Central，请按 [接入指南](docs/getting-started.md) 使用源码或 Maven Local。

## 能力导航

所有通用 API 的包前缀：`io.github.zuijinbuzai.kmputilcode`。

| 模块 | 已实现能力 | 文档 |
|---|---|---|
| `utilcode-core` | 文本、脱敏、邮箱/手机号/IP/URL/Luhn/密码校验、集合分页/统计/移动、数字/容量、LRU、可注入日志 | [基础工具](docs/modules/core.md) |
| `utilcode-codec` | Base64、Hex、URL 组件编解码、SHA-256/512、HMAC-SHA256 | [编码与摘要](docs/modules/codec.md) |
| `utilcode-time` | 可注入时钟、ISO 时间、时区日期、一天起点、日历运算、闰年/星期 | [时间日期](docs/modules/time.md) |
| `utilcode-coroutines` | 取消友好的退避重试、固定工作协程池、冷定时流、串行任务门 | [协程任务](docs/modules/coroutines.md) |
| `utilcode-serialization` | 类型 JSON、树解析、格式化、RFC 7396 Merge Patch | [JSON 数据](docs/modules/serialization.md) |
| `utilcode-storage` | 统一类型化键值接口、内存实现、SharedPreferences / NSUserDefaults / JVM Preferences | [键值存储](docs/modules/storage.md) |
| `utilcode-file` | 根目录约束、路径检查、限量读取、写入/追加/复制/列举/删除 | [文件](docs/modules/file.md) |
| `utilcode-network` | Ktor 多平台引擎、JSON、超时、类型响应、HTTP/传输/解码错误建模 | [网络](docs/modules/network.md) |
| `utilcode-platform` | 系统信息、随机 UUID；Android dp/px、权限检查、剪贴板、网络状态、浏览器与分享 | [平台能力](docs/modules/platform.md) |
| `utilcode` | 聚合上面 9 个模块，也可按需单独依赖 | [接入指南](docs/getting-started.md) |

## 五分钟运行

环境：JDK 21、Android SDK 35；iOS 另需 macOS + Xcode 和 Apple Silicon。库的 JVM/Android 字节码目标为 Java 17，Android `minSdk=23`，iOS 示例最低版本 15。

```bash
git clone https://github.com/zuijinbuzai/AppUtilCode.git
cd AppUtilCode
# 配置 Android SDK：ANDROID_HOME，或 local.properties 中的 sdk.dir
./gradlew verify
```

`verify` 会运行所有模块的 JVM 测试、构建 Android Debug APK 并执行 Lint、运行默认离线的 JVM 示例。首次构建需要联网下载 Gradle、Kotlin、Android 工具和 Maven 依赖。

```bash
# 单独运行 JVM 示例：默认不访问外网
./gradlew :demo-jvm:run
# 主动启用真实网络请求示例
./gradlew :demo-jvm:run --args="--network"
# Android：Android Studio 选择 demo-android 运行，或
./gradlew :demo-android:installDebug
# iOS：Xcode 打开下面的工程，选择模拟器运行
open demo-ios/KmpUtilCodeDemo.xcodeproj
```

Android APK 输出：`demo-android/build/outputs/apk/debug/demo-android-debug.apk`。

## API 预览

```kotlin
import io.github.zuijinbuzai.kmputilcode.core.*
import io.github.zuijinbuzai.kmputilcode.codec.Codec
import io.github.zuijinbuzai.kmputilcode.coroutines.parallelMap
import io.github.zuijinbuzai.kmputilcode.storage.MemorySettings

Text.mask("13812345678")                        // 138****5678
Validation.email("hello@example.com")           // true
(1..12).toList().page(index = 1, size = 5)        // [6, 7, 8, 9, 10]
Numbers.formatBytes(1_572_864)                   // 1.5 MiB
Codec.urlEncode("你好 + KMP")                    // %E4%BD%A0%E5%A5%BD%20%2B%20KMP
val settings = MemorySettings()
settings.putBoolean("darkMode", true)
// 在 suspend 函数内：
// val results = users.parallelMap(concurrency = 4) { repository.load(it) }
```

## 文档与示例

- [文档索引](docs/README.md)：从接入到逐模块说明。
- [三端 Demo 指南](docs/demo.md)：17 个示例、源码位置、运行步骤、预期输出和错误排查。
- [API 索引](docs/api-index.md)：按类/函数定位。
- [架构与平台矩阵](docs/architecture.md)：模块依赖、线程与生命周期、可替换边界。
- [从 AndroidUtilCode 迁移](docs/migration.md)：常见 Android 工具在 KMP 中的对应方式。
- [测试与验证](docs/testing.md)：命令、覆盖范围和验证记录。
- [贡献约定](CONTRIBUTING.md)、[变更记录](CHANGELOG.md)、[后续规划](docs/roadmap.md)。

## 设计约定

1. **按需依赖**：纯算法模块不依赖 Android Context；平台差异不伪装成完全一致的能力。
2. **可测试**：时间源、文件系统、HTTP 引擎、日志输出都可替换；示例与库共享真实代码。
3. **生命周期明确**：库不创建常驻全局协程；HTTP 客户端由所有者关闭，任务由调用者取消。
4. **行为可预期**：参数错误抛 `IllegalArgumentException`；协程取消向上传递；格式与时区规则明确记录。

本项目采用 [Apache-2.0](LICENSE) 许可，与 Blankj/AndroidUtilCode 无隶属关系。
