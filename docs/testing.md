# 测试与验证

## 统一入口

```bash
./gradlew verify
```

依次由任务图执行各库和共享 Demo 的 JVM 测试、Android Debug 构建与 Lint、默认离线 JVM 示例。`verify` 不部署设备，也不在 Linux 上编译 Apple 目标。

## 测试范围

| 模块 | 当前重点 |
|---|---|
| core | 文本边界、非法输入、分页乘法溢出、LRU 淘汰、数字、惰性日志 |
| codec | SHA/HMAC 已知向量、Unicode/emoji 往返、坏百分号/UTF-8/Base64/Hex |
| time | 注入时钟、上海跨日、纽约 DST 23 小时日、闰年、日历运算 |
| coroutines | 虚拟时间退避、失败耗尽、取消传播、固定并发上限/顺序、定时流、gate 释放 |
| serialization | 集合往返、坏 JSON、递归合并/删除/数组与标量替换 |
| storage | 内存/标签共同契约、类型不匹配、JVM 跨实例持久化、命名空间隔离 |
| file | 实际临时文件的读写/读取上限、空文件、路径遍历与符号链接拒绝 |
| network | MockEngine JSON/查询、404、坏响应、取消、非 HTTP 协议拒绝 |
| platform | UUID v4 格式/随机性、运行时信息 |
| demo-shared | 示例 ID 唯一、说明/代码齐全、离线共享执行入口 |

HTTP 单元测试不访问外网。文件/持久化平台测试使用临时目录/独立命名节点并清理。单独命令：`./gradlew :utilcode-network:jvmTest`，其他模块类推。

## Apple 构建

```bash
./gradlew :demo-shared:compileKotlinIosArm64 :demo-shared:compileKotlinIosSimulatorArm64
xcodebuild -project demo-ios/KmpUtilCodeDemo.xcodeproj -scheme KmpUtilCodeDemo \
  -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' \
  -derivedDataPath demo-ios/build CODE_SIGNING_ALLOWED=NO build
```

Xcode 工程明确使用 arm64，与当前 KMP target 一致，不提供 Intel iOS Simulator 产物。SwiftUI 构建同时验证 Framework 实际链接和 Swift 调用签名。

commonTest 可以通过对应 Native Test task 进一步运行；本次交付记录中的 30 个测试执行于 JVM，没有声称它们已在每个设备平台运行。

## 2026-11-19 本机验证记录

环境：Apple Silicon macOS，JDK 21，Xcode 26.6，Android SDK 35。

| 检查 | 结果 |
|---|---|
| 全部 JVM 测试 | **30 个通过，0 失败、0 错误** |
| JVM 默认示例 | 16 个非网络示例运行完成 |
| Android Debug APK | 构建成功，路径为 demo-android/build/outputs/apk/debug/ |
| Android Lint | 通过，0 错误（保留目标 SDK 更新建议） |
| iOS arm64 共享代码 | 所有库及 demo-shared 编译成功 |
| iOS Simulator arm64 共享代码 | 所有库及 demo-shared 编译成功 |
| iOS SwiftUI 宿主 | Xcode Debug 模拟器构建成功，Framework 已链接 |
| core Maven Local 发布 | metadata、JVM、Android release、iOS 两个变体发布成功 |

没有执行 Android/iOS 真机安装与系统交互验收，也未把剪贴板/分享/浏览器的设备行为计入单元测试通过范围。公开网络示例由用户主动触发；自动验证依赖 MockEngine。

## CI

`.github/workflows/ci.yml` 包含 Linux 上的 verify 和 macOS 上的 Apple 目标/SwiftUI 构建。推送 main 或创建 PR 自动触发。工作流文件随源码提交，远端运行结果以 GitHub Actions 页面为准。本机结果不代表远端 CI 已运行通过。

## 报告位置

- JVM：各模块 `build/reports/tests/jvmTest/index.html`。
- JVM XML：各模块 `build/test-results/jvmTest/TEST-*.xml`。
- Android Lint：`demo-android/build/reports/lint-results-debug.html`。
- APK：`demo-android/build/outputs/apk/debug/demo-android-debug.apk`。
- SwiftUI App：`demo-ios/build/Build/Products/Debug-iphonesimulator/KmpUtilCodeDemo.app`。

报告和产物不提交到 Git，CI 会上传 JVM 测试报告和 Android APK 作为构建附件。
