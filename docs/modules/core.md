# 基础工具：utilcode-core

依赖 `project(":utilcode-core")`，包 `io.github.zuijinbuzai.kmputilcode.core`。没有第三方运行时依赖，Android/iOS/JVM 共用同一实现。

## 文本

```kotlin
Text.isBlank(null)                                  // true
Text.orDefault("  ", "访客")                         // 访客
Text.truncate("Kotlin Multiplatform", 10)             // Kotlin Mu…
Text.mask("13812345678")                             // 138****5678
Text.mask("hello@example.com", 2, 4, '#')
Text.snakeCase("HTTPServerPort")                     // http_server_port
Text.camelCase("http_server_port")                   // httpServerPort
```

`truncate` 的最大长度包括后缀；0 返回空串；后缀过长会截断后缀。`mask` 两端可见区域不能重叠；短字符串应主动指定范围，默认 3+4 不适用于少于 7 个字符的输入。索引按 Kotlin UTF-16 code unit 计数，可能切断 emoji/组合字符；需要字素簇处理时使用专用 Unicode 方案。

## 校验

| 方法 | 规则 |
|---|---|
| `email` | 常见 ASCII 邮箱形式，最大总长 254、本地部分 64，拒绝连续点/非法域标签；非完整 RFC 验证 |
| `mainlandMobile` | 中国大陆 11 位手机号格式：`1[3-9]` 开头；不验证号码归属/实际可用性 |
| `ipv4` | 4 段十进制 0..255，拒绝前导零、符号和空段 |
| `httpUrl` | 常见 `http/https` URL 形态与端口 1..65535；不支持 IPv6/用户信息；不做域名解析与地址信任判断 |
| `luhn` | 2..19 位 ASCII 数字，Luhn 校验位；不表示银行卡合法或有效 |
| `password` | 默认至少 8 字符，含大小写、数字和非空白特殊字符；可调整最小长度 |

输入不会自动 trim；建议 UI 层明确决定是否预处理。URL 校验仅用于输入提示，不作为 SSRF 或信任域检查。

## 集合

```kotlin
(1..12).toList().page(index = 1, size = 5)   // [6,7,8,9,10]
listOf("a", "b", "a").frequencies()         // {a=2,b=1}
listOf("a", "b", "a").duplicatesBy { it }   // [a,a]
listOf("a", "b", "c").move(0, 2)            // [b,c,a]
```

`page` 从 0 页开始，越界返回空列表，非法负页码/非正大小抛参数异常，乘法用 Long 避免 Int 溢出。`duplicatesBy` 返回所有属于重复组的原始元素，保留输入顺序，不只返回第二次出现的元素。`move` 返回副本，不改变原列表；索引按移动后的最终位置解释。

## 数字

```kotlin
Numbers.round(3.14159, 3)       // 3.142
Numbers.formatBytes(1536)      // 1.5 KiB
Numbers.gcd(-54, 24)           // 6L
Numbers.lerp(0.0, 100.0, 0.25) // 25.0
```

`round` 采用 Kotlin 的 ties-to-even 规则，不是十进制财务舍入；参数必须有限，decimals=0..9。`formatBytes` 接受非负 Long、使用 1024 进位、固定小数点和一位小数；小于 1024 显示整数 B。`gcd` 拒绝 Long.MIN_VALUE，`gcd(0,0)=0`。`lerp` 不钳制 fraction，允许外插。

## 缓存

```kotlin
val cache = LruCache<String, String>(capacity = 2)
cache.put("a", "A")
cache.put("b", "B")
cache["a"]                 // 访问更新 LRU 顺序
cache.put("c", "C")        // b 被淘汰
cache.snapshot()           // 从最旧到最新的副本
cache.remove("a")
cache.clear()
```

容量按条目，不按字节；值非空，不支持 TTL、磁盘和线程安全。`put` 返回被替换的旧值；snapshot 不更新访问顺序。

## 日志

```kotlin
val logger = Logger("Repository", minimum = LogLevel.INFO, sink = LogSink { entry ->
    println("[${entry.level}] ${entry.tag}: ${entry.message}")
    entry.cause?.printStackTrace()
})
logger.debug { "低于 INFO，不执行这个 lambda" }
logger.info { "loaded" }
logger.error(IllegalStateException("sample")) { "failed" }
```

日志是实例配置，默认输出 println，不自动转发到 Android Logcat/os_log。自定义 sink 可对接各平台系统日志，线程安全由 sink 保证。禁止在业务日志中输出密码、Token 或完整敏感数据。

## 验证与 Demo

运行 `./gradlew :utilcode-core:jvmTest`。Demo id：`text`、`validation`、`collections`、`numbers`、`cache`、`logger`。
