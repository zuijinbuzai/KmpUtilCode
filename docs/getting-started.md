# 接入指南

## 环境

| 项目 | 当前版本/要求 |
|---|---|
| Kotlin | 2.2.20 |
| Gradle Wrapper | 8.13，固定官方 SHA-256 |
| Android Gradle Plugin | 8.11.1 |
| JDK | 21（编译产物目标 Java 17） |
| Android | compileSdk/targetSdk 35，minSdk 23 |
| iOS | `iosArm64` + `iosSimulatorArm64`；Apple Silicon Mac + Xcode |
| 示例 iOS 最低版本 | 15.0 |

三方依赖集中在 `gradle/libs.versions.toml`。没有配置 JS、Wasm、Linux、Windows、macOS Native 和 Intel iOS 模拟器目标。JVM 可以运行在支持 Java 17+ 的桌面/服务端。

设置 `JAVA_HOME` 指向 JDK 21。设置 `ANDROID_HOME`，或在本机创建（不提交）：

```properties
# local.properties
sdk.dir=/your/path/to/Android/sdk
```

## 方式一：作为源码工程使用

克隆后在 Android Studio/IntelliJ 打开根目录。各模块的 `commonMain` 和平台 source set 已就绪。在此仓库内新建业务模块后，按需声明：

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":utilcode-core"))
            implementation(project(":utilcode-time"))
            implementation(project(":utilcode-coroutines"))
            // 或 implementation(project(":utilcode")) 获取所有能力
        }
    }
}
```

对于外部工程，推荐 Maven Local 方式；也可将此仓库的库模块复制到业务仓库并在 `settings.gradle.kts` 中 include。工程内的 `project(...)` 不是可直接从 Maven 下载的坐标。

## 方式二：Maven Local

**当前没有正式远程发布。** 在 Mac 上执行：

```bash
# 按需发布一个模块及其各平台变体
./gradlew :utilcode-core:publishToMavenLocal
# 发布聚合模块时必须同时发布全部依赖模块
./gradlew publishToMavenLocal
```

业务工程的依赖仓库增加 `mavenLocal()`，在 `commonMain` 声明：

```kotlin
implementation("io.github.zuijinbuzai:utilcode-core:0.1.0-SNAPSHOT")
// 或（需要先发布全部模块）
implementation("io.github.zuijinbuzai:utilcode:0.1.0-SNAPSHOT")
```

Gradle 会按目标选择 Android/JVM/iOS 的发布变体。在 Linux 上只能发布主机支持的目标；完整的 Apple 产物需要在 macOS 发布。生产发布的签名、凭据、Maven Central staging 流程尚未接入。

## 序列化

使用 `JsonCodec` 编解码自定义类型时，**消费方也需要 serialization 插件**，并标注 `@Serializable`：

```kotlin
plugins {
    kotlin("multiplatform") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
}
```

```kotlin
import kotlinx.serialization.Serializable
import io.github.zuijinbuzai.kmputilcode.serialization.JsonCodec

@Serializable data class User(val id: Long, val name: String)
val codec = JsonCodec()
val payload = codec.encode(User(7, "KMP"))
val user = codec.decode<User>(payload)
```

## 平台初始化

库本身不需要 `init()`。Android 持久化需要显式 Context：

```kotlin
val settings = persistentSettings("user-preferences", applicationContext)
```

iOS/JVM 不传 Context：`persistentSettings("user-preferences")`。`MemorySettings()` 在所有平台都不需要 Context。

Android 自己使用 `Dispatchers.Main` 时需依赖 `kotlinx-coroutines-android`（本项目的 Android Demo 已配置），并按业务生命周期取消作用域。网络请求需要应用声明 `INTERNET`；联网状态检查还需要 `ACCESS_NETWORK_STATE`。库不自动弹出授权界面。

## iOS 与 Swift

业务通常在共享 Kotlin 层调用工具库，再向 Swift 导出业务接口，而不是让 Swift 直接使用所有泛型/reified API。仓库的 `demo-shared` 就采用这种方式，只导出示例元数据和一个返回字符串的执行入口。

```bash
./gradlew :demo-shared:linkDebugFrameworkIosSimulatorArm64
open demo-ios/KmpUtilCodeDemo.xcodeproj
```

Xcode 的 `Build Kotlin Framework` 阶段会调用 `embedAndSignAppleFrameworkForXcode`，按模拟器/真机和 Debug/Release 构建 DemoKit。模拟器不需要开发证书；真机运行需要在 Xcode 选择自己的 Team。不提交个人签名信息。

## 验证接入

```bash
./gradlew verify
# Mac 上验证 Apple 共享代码
./gradlew :demo-shared:compileKotlinIosArm64 :demo-shared:compileKotlinIosSimulatorArm64
# 构建 SwiftUI 宿主，无需签名
xcodebuild -project demo-ios/KmpUtilCodeDemo.xcodeproj -scheme KmpUtilCodeDemo \
  -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' \
  -derivedDataPath demo-ios/build CODE_SIGNING_ALLOWED=NO build
```

若下载失败，先检查代理、DNS、JDK 证书与 Maven/Google/Gradle 官方仓库的连通性。不要通过关闭 TLS 证书校验来解决。
