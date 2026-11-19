# 贡献约定

## 开发

1. 安装 JDK 21、Android SDK 35；Apple 目标需 Apple Silicon macOS + Xcode。
2. 在已有能力模块内增加 API，只有依赖/职责需要独立时才拆新模块。
3. commonMain 不依赖 Android/Java 平台类；actual 与专属 API 写在对应 source set。
4. 行为变更必须同时更新测试、DemoCatalog 和相应模块文档。
5. 提交前运行 `./gradlew verify`；Apple 变更另外编译 iOS 两个目标与 Xcode 宿主。

## API 设计

- 公共类型、参数、单位和默认值明确，不把异常吞成隐含成功。
- suspend API 依附调用者作用域，CancellationException 直接传播。
- 对时间源、HTTP 引擎、日志/文件系统使用可注入接口。
- 写明线程安全、阻塞性、所有权、权限与生命周期。
- 优先成熟跨平台库，避免重新实现密码学、HTTP、时区数据库。
- 不把尚未实现的功能写成已支持，不用无效占位 API 凑数量。

## 测试与文档

单元测试验证可观察契约与失败分支，避免只验证实现细节。公共算法尽量放 commonTest，以便同一测试运行 JVM/Native；平台系统接口补充对应平台验证。文档示例需要与真实签名一致。

## 提交

使用能描述行为的提交消息，检查不包含 local.properties、构建产物、密钥、个人签名或 IDE 状态。新依赖要说明用途和许可。项目采用 Apache-2.0；提交贡献表示允许按同一许可证发布。
