# 平台能力：utilcode-platform

包 `io.github.zuijinbuzai.kmputilcode.platform`。通用接口只覆盖语义一致的能力。

```kotlin
val info = platformInfo() // PlatformInfo(name, version, architecture)
val requestId = randomUuid() // 小写 UUID v4
```

Android 通过 Build 读取版本与首个支持 ABI；iOS 通过 UIDevice 读取版本，模拟器架构描述使用 SIMULATOR_MODEL_IDENTIFIER（如 iPhone17,3），真机返回 arm64；JVM 读取 os.name/os.version/os.arch。因此 architecture 是平台的描述字段，不保证跨平台可解析为同一套枚举。

UUID 每次随机，不访问 IMEI、MAC、IDFA 或 Android ID，不能用来识别同一台设备，也不是认证凭证。

## Android 专属 API

以下 API 只在 `androidMain` 可调用，Context 每次显式传入，不保存 Activity 引用。

```kotlin
AndroidTools.dpToPx(context, 16f)             // 四舍五入到 Int
AndroidTools.pxToDp(context, 48)
AndroidTools.hasPermission(context, Manifest.permission.CAMERA)
AndroidTools.copyText(context, "Hello KMP", label = "greeting")
AndroidTools.hasValidatedInternet(context)
AndroidTools.openUrl(context, "https://github.com/zuijinbuzai/AppUtilCode")
AndroidTools.shareText(context, "Hello KMP", title = "分享")
```

- dp/px 使用 Context 当前 resources density，非 sp 转换。
- hasPermission 只检查，不弹窗、不触发授权流程。运行时授权应由 Activity Result API/业务 UI 完成。
- 剪贴板写入可能由系统显示提示；方法不读取用户剪贴板。
- hasValidatedInternet 需要 Manifest 的 ACCESS_NETWORK_STATE，否则抛 IllegalStateException；返回 OS 最近一次网络验证状态，不保证下一次请求成功，不代替 HTTP 错误处理。
- openUrl 只接受带 host 的 http/https，调用系统浏览器；无处理器返回 false。不能作为网址信任判断。
- shareText 使用系统 chooser，发生的系统异常不被全部吞掉。浏览器/分享应从主线程触发。

没有把 Android 的权限状态/Intent、iOS 的授权域和 JVM 的桌面操作强行合并为同一 API。后续组件按能力接口逐步加入平台实现，见路线图。

`./gradlew :utilcode-platform:jvmTest` 验证 UUID 格式和运行时信息；AndroidTools 的系统交互需要设备环境人工验收。Demo id `platform`；Android Demo 同时用到了 dpToPx 和 copyText。
