# 网络：utilcode-network

包 `io.github.zuijinbuzai.kmputilcode.network`。Ktor 跨端 HTTP + JSON，Android/JVM 使用 OkHttp、iOS 使用 Darwin 引擎。

## 基本调用

```kotlin
val api = NetworkClient()
try {
    when (val result = api.get<Map<String, String>>("https://your-api.example/profile")) {
        is ApiResult.Success -> println(result.value)
        is ApiResult.HttpFailure -> println("HTTP ${result.status}")
        is ApiResult.Failure -> println(result.cause.message)
    }
} finally {
    api.close()
}
```

`Success<T>` 包含 value 与 status；非 2xx 返回 HttpFailure(status)；传输、超时或解码异常返回 Failure(cause)。取消异常向上传递，保留结构化并发。非 HTTP(S) URL 或构造参数错误会直接抛参数异常。错误结果不读取/暴露响应正文，避免默认泄露服务端内容。

对于 204/空响应可使用 `T=Unit`，对于原始文本使用 `T=String`。自定义数据类要 `@Serializable` 并启用消费方 serialization 插件。

## 查询、Header、POST

```kotlin
import io.ktor.client.request.*
import io.ktor.http.*

val result = api.get<User>("https://your-api.example/users/7") {
    parameter("locale", "zh-CN")
    header(HttpHeaders.Accept, ContentType.Application.Json)
}
val saved = api.request<User>("https://your-api.example/users", HttpMethod.Post) {
    contentType(ContentType.Application.Json)
    setBody(CreateUser("KMP"))
}
```

请求配置直接使用 Ktor builder，避免再造一套重复 HTTP DSL。对真实网络的域名、认证、重定向、证书与权限策略由业务决定；默认不添加请求日志、不注入凭据、不绕过 TLS 校验。查询参数应使用 `parameter()` 编码。

## 配置、注入与所有权

```kotlin
val client = createHttpClient(timeoutMillis = 30_000)
val api = NetworkClient(client)
```

默认 request/connect/socket timeout 均为 15 秒，expectSuccess=false，安装 ContentNegotiation 并复用 JsonCodec.defaultJson。各引擎可能有不同的底层超时语义。传入自定义 HttpClient 时其配置完全由调用者决定，建议保留 expectSuccess=false 来使用 HttpFailure 分支；设为 true 的自定义 client 会将非 2xx 异常归为 Failure。

NetworkClient **拥有包括注入 client 在内的生命周期**。不要将一个注入 client 交给多个互相独立关闭的所有者；推荐一个服务持有一个实例。库不会自动重试，也不会自动把 HttpFailure 变成异常。非幂等写操作的重试需业务设计幂等键。

## Android 权限

应用 Manifest 声明 `android.permission.INTERNET`。网络状态辅助 API 另需 `ACCESS_NETWORK_STATE`。默认不放开 cleartext 全局流量。例子和测试均使用 HTTPS URL。

## 测试

```kotlin
val engine = MockEngine {
    respond("""{"ok":true}""", HttpStatusCode.OK,
        headersOf(HttpHeaders.ContentType, "application/json"))
}
val api = NetworkClient(createHttpClient(engine))
try {
    val result = api.get<Map<String, Boolean>>("https://example.test")
} finally { api.close() }
```

`./gradlew :utilcode-network:jvmTest` 以 MockEngine 验证 JSON/查询参数、404、坏响应、取消和非法协议，不访问公网。

Demo id `network` 仅用户主动触发时访问 `https://example.com`，返回 HTTP 状态和文本长度；不发送业务数据。JVM 默认离线，只有 `--args="--network"` 才执行该条示例。
