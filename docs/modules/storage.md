# 键值存储：utilcode-storage

包 `io.github.zuijinbuzai.kmputilcode.storage`。统一 String/Long/Boolean 标量存储，适合偏好项，不是数据库或密钥库。

```kotlin
val settings: Settings = MemorySettings()
settings.putString("locale", "zh-CN")
settings.putLong("launches", 1)
settings.putBoolean("dark", true)
settings.getString("locale")                    // zh-CN
settings.getLong("missing", default = 42)        // 42
settings.getBoolean("dark")                     // true
settings.contains("locale")                     // true
settings.remove("locale")
settings.clear()                                // 仅当前 store
```

缺失与类型不匹配返回调用者给出的 default；默认值分别是 `""`、`0L`、`false`。null 不在存储接口中，用 remove 表达删除。

## 平台持久化

```kotlin
// androidMain，使用 applicationContext
val settings = persistentSettings("account-ui", applicationContext)
// iosMain / jvmMain
val settings = persistentSettings("account-ui")
```

| 平台 | 实现 | 写入语义 |
|---|---|---|
| Android | `kmputilcode.<name>` SharedPreferences | apply 内存立即更新，磁盘异步；不承诺完成时已落盘 |
| iOS | `io.github.zuijinbuzai.kmputilcode.<name>` NSUserDefaults suite | 系统持久化；clear 只删除该 suite 中本库前缀的 key |
| JVM | `io/github/zuijinbuzai/kmputilcode/<name>` Preferences node | 每次 write/remove/clear 调用 flush，错误向上传递 |

name 要求长度 1..64，只允许字母/数字/`.`/`_`/`-`。Android 必须传 Context，否则抛参数异常。JVM 的 key/value 受 Preferences 平台长度限制；超限异常不被吞掉。

持久化用 StringSettings 的 `s:`、`l:`、`b:` 类型标签保证统一读写语义，不应直接和既有 SharedPreferences 原生值混用。如果需要迁移旧值，应由业务层读取旧容器并写入新 store。

## 并发、清理与敏感数据

`MemorySettings` 单线程限定。平台 store 不提供复合操作事务；`getLong()+1` 在并发下会丢失更新，需要外部串行化。clear 不等于卸载数据；仅清当前命名空间，绝不清空整个 NSUserDefaults/Preferences root。

偏好存储不加密，不能存密码、长期 Token 或私钥。需要系统 Keychain/Keystore 时使用专门安全存储组件；此能力列入规划。

测试包括内存和标签实现的共同契约、JVM 重建对象后的持久化与命名空间隔离。`./gradlew :utilcode-storage:jvmTest`；Demo id：`memory`、`persistent`。演示只递增 `demo` 命名空间内的 launches 键，重新启动再次运行可检查持久化。
