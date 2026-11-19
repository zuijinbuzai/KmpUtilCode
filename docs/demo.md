# 三端 Demo 运行指南

17 组示例元数据、可复制代码和实际执行逻辑全部位于 `demo-shared/.../DemoCatalog.kt`。Android 与 iOS 从同一目录构建界面，JVM 在控制台运行相同的执行入口。

## Android

1. 设置 JDK 21 和 Android SDK 35，Android Studio 打开根目录。
2. 等待同步后选择 `demo-android`，运行到 API 23+ 设备/模拟器。
3. 首页按类别浏览，搜索标题、描述、代码关键词。
4. 点击卡片查看说明和代码；点击“运行”展示结果；“复制代码”写入系统剪贴板。
5. 关闭弹窗取消当前运行任务，Activity 销毁时取消作用域。

命令行：`./gradlew :demo-android:installDebug`。APK 位于 `demo-android/build/outputs/apk/debug/`。UI 没有 Compose 依赖，使用原生 Activity/布局，减少工具库 Demo 的额外依赖。

## iOS

1. Apple Silicon Mac 安装 Xcode 并完成首次启动配置，设置 JDK 21。
2. `open demo-ios/KmpUtilCodeDemo.xcodeproj`。
3. 选择 KmpUtilCodeDemo scheme 和 arm64 iPhone 模拟器运行。Build Kotlin Framework 阶段会自动构建 DemoKit。
4. 列表支持搜索；详情展示说明、源码和“运行示例”。结果更新到主线程。
5. 真机使用自己的 Team/签名；没有硬编码证书或开发者账户。

原生 SwiftUI 工程已随仓库提交，不需要 XcodeGen 或 CocoaPods。完整构建命令见 [接入指南](getting-started.md)。当前 SwiftUI callback 示例没有暴露取消句柄，退出详情页不会立刻取消底层任务；网络由 15 秒超时限制。

## JVM

```bash
./gradlew :demo-jvm:run
./gradlew :demo-jvm:run --args="--network"
```

默认执行 16 条非网络示例，无需联网 API。文件示例在临时目录运行并自动清理；持久化示例递增本机独立偏好节点，保留以演示跨运行保存。启用 --network 才访问 https://example.com。

## 示例与预期结果

| id | 场景 | 关键结果 |
|---|---|---|
| text | 手机号脱敏、命名、截断 | `138****5678`、`http_server_port` |
| validation | 常见表单校验 | 示例合法输入均为 true |
| collections | 分页、频次、重复组、移动 | 第 2 页 `[6,7,8,9,10]` |
| numbers | 容量、小数、GCD、插值 | `1.5 MiB`、`3.142`、`6`、`25.0` |
| cache | LRU 访问后淘汰 | a/c 保留，b 被淘汰 |
| logger | 日志级别/惰性消息 | INFO/WARN 可见，DEBUG 不执行 |
| codec | Unicode 编码往返 | URL 解码回 `你好 + KMP` |
| hash | SHA-256/HMAC | abc 的 SHA-256 为 `ba7816bf...15ad` |
| time | 上海跨日、跨年、闰年 | `2026-11-20`、`1`、`true` |
| retry | 前 2 次失败后重试成功 | 第 3 次成功 |
| parallel | 并发映射/定时流 | `[1,4,9,16,25]`、`[0,1,2]` |
| json | 类型 JSON/Merge Patch | level 从 2 更新至 3 |
| memory | 缺失默认/标量读写 | 计数 1、开关 true、fallback |
| persistent | 平台持久化计数 | 重启后继续递增 |
| file | 写入/追加/读取/列举 | `Hello KMP`，9 UTF-8 字节 |
| platform | 系统/UUID | 设备运行时信息和新 UUID |
| network | 真实 HTTPS 请求 | HTTP 状态和响应长度，或清晰错误 |

## 排错

- Android 出现 Main dispatcher missing：确认消费应用有 kotlinx-coroutines-android；仓库 Demo 已包含。
- Android 文件读写失败：确认宿主传自己的 filesDir，不要传共享存储/content URI。
- Xcode 找不到 DemoKit：检查 Gradle 脚本阶段是否执行、JAVA_HOME 是否可用；先手动运行 linkDebugFrameworkIosSimulatorArm64 查看错误。
- iOS 真机签名失败：选择自己的 Team；模拟器可 `CODE_SIGNING_ALLOWED=NO` 构建。
- 持久化默认值异常：检查 name 是否一致、类型是否正确、Android 是否传 Context。
- 网络请求失败：示例允许真实失败，先检查系统网络和官方 HTTPS 证书；离线测试使用 MockEngine，不依赖公网。

默认自动验证关注编译与 JVM 行为。系统剪贴板、分享、浏览器、真机存储及 UI 交互仍需要按设备场景验收，不将一次构建成功表述为所有平台所有交互均通过。
