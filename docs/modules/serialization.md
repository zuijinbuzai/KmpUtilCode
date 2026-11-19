# JSON：utilcode-serialization

包 `io.github.zuijinbuzai.kmputilcode.serialization`。调用自定义数据类的 encode/decode 时，消费模块需启用 Kotlin serialization 插件。

```kotlin
@Serializable data class User(val name: String, val level: Int = 1)
val codec = JsonCodec()
val text = codec.encode(User("KMP", 2)) // {"name":"KMP","level":2}
val user = codec.decode<User>("""{"name":"KMP","extra":true}""") // level=1
val tree = codec.parse(text)
val readable = codec.pretty(text)
```

默认 Json：忽略未知字段、输出默认值、严格语法（isLenient=false）。未知字段兼容不意味着缺失必填字段、类型不匹配或损坏 JSON 会成功；这些仍抛序列化异常。

业务可注入独立配置，不修改全局对象：

```kotlin
val codec = JsonCodec(Json {
    ignoreUnknownKeys = false
    encodeDefaults = false
})
```

## RFC 7396 Merge Patch

```kotlin
val target = codec.parse("""{"profile":{"name":"A","age":20},"roles":[1,2]}""")
val patch = codec.parse("""{"profile":{"age":null,"name":"B"},"roles":[3]}""")
val merged = codec.mergePatch(target, patch)
// {"profile":{"name":"B"},"roles":[3]}
```

- patch 为对象时递归合并；null 删除该对象字段。
- patch 为数组或标量时直接替换整个目标；顶层 null 也会替换目标为 null。
- 原始 JsonElement 不修改，返回新树。
- 这是 Merge Patch，不是 RFC 6902 的 op/path JSON Patch，也不是 JSONPath 查询。

命令 `./gradlew :utilcode-serialization:jvmTest`；Demo id：`json`。网络模块复用默认 Json 策略，需要网络专属策略时可注入自定义 HttpClient。
