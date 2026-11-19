package io.github.zuijinbuzai.kmputilcode.demo

import io.github.zuijinbuzai.kmputilcode.core.*
import io.github.zuijinbuzai.kmputilcode.codec.Codec
import io.github.zuijinbuzai.kmputilcode.time.*
import io.github.zuijinbuzai.kmputilcode.coroutines.*
import io.github.zuijinbuzai.kmputilcode.serialization.JsonCodec
import io.github.zuijinbuzai.kmputilcode.storage.*
import io.github.zuijinbuzai.kmputilcode.file.FileStore
import io.github.zuijinbuzai.kmputilcode.network.*
import io.github.zuijinbuzai.kmputilcode.platform.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import okio.Path.Companion.toPath
import kotlin.time.Duration.Companion.milliseconds

/** Metadata and executable business samples are shared by all three demo hosts. */
data class DemoCase(val id: String, val category: String, val title: String, val description: String, val code: String)
@Serializable
data class Profile(val name: String, val level: Int = 1)

object DemoCatalog {
    val cases: List<DemoCase> = listOf(
        DemoCase("text", "基础工具", "文本处理与脱敏", "截断、命名转换、手机号脱敏；下标按 UTF-16 计算。",
            "Text.mask(\"13812345678\")\nText.snakeCase(\"HTTPServerPort\")\nText.truncate(\"Kotlin Multiplatform\", 10)"),
        DemoCase("validation", "基础工具", "常用输入校验", "邮箱、手机号、IPv4、URL、Luhn 与密码规则。",
            "Validation.email(\"hello@example.com\")\nValidation.ipv4(\"192.168.1.1\")\nValidation.luhn(\"79927398713\")"),
        DemoCase("collections", "基础工具", "集合分页与统计", "分页索引从 0 开始；移动元素、重复项与频次统计。",
            "(1..12).toList().page(index = 1, size = 5)\nlistOf(\"a\", \"b\", \"a\").frequencies()"),
        DemoCase("numbers", "基础工具", "数字与容量", "二进制容量单位、四舍六入五成双、最大公约数与插值。",
            "Numbers.formatBytes(1_572_864)\nNumbers.round(3.14159, 3)\nNumbers.gcd(54, 24)"),
        DemoCase("cache", "基础工具", "LRU 内存缓存", "容量 2，访问 a 后插入 c，最久未使用的 b 被淘汰。",
            "val cache = LruCache<String, String>(2)\ncache.put(\"a\", \"A\"); cache.put(\"b\", \"B\")\ncache[\"a\"]; cache.put(\"c\", \"C\")\ncache.snapshot()"),
        DemoCase("logger", "基础工具", "可注入日志", "演示日志级别过滤、惰性消息与自定义输出。",
            "val log = Logger(\"Demo\", sink = LogSink { println(it) })\nlog.debug { \"filtered\" }\nlog.info { \"ready\" }"),
        DemoCase("codec", "编码与摘要", "Base64 / Hex / URL", "Unicode UTF-8 往返；URL 按组件编码，空格编码为 %20。",
            "Codec.base64(\"你好 KMP\".encodeToByteArray())\nCodec.urlEncode(\"你好 + KMP\")\nCodec.hex(byteArrayOf(0, 15, -1))"),
        DemoCase("hash", "编码与摘要", "SHA 与 HMAC", "成熟的 Okio 摘要实现；SHA 不是密码存储算法。",
            "Codec.sha256(\"abc\".encodeToByteArray())\nCodec.hmacSha256(\"message\".encodeToByteArray(), \"key\".encodeToByteArray())"),
        DemoCase("time", "时间日期", "时区与日历", "固定时间源、上海日期、闰年、跨年天数和 ISO 解析。",
            "val time = TimeUtils { Instant.parse(\"2026-11-19T16:30:00Z\") }\ntime.today(TimeZone.of(\"Asia/Shanghai\"))\nTimeUtils.daysBetween(LocalDate(2026, 12, 31), LocalDate(2027, 1, 1))"),
        DemoCase("retry", "协程任务", "重试与退避", "模拟前两次失败、第三次成功；取消异常直接向上传递。",
            "retry(maxAttempts = 3, initialDelay = 10.milliseconds) { attempt ->\n    if (attempt < 3) error(\"temporary\")\n    \"success on attempt ${'$'}attempt\"\n}"),
        DemoCase("parallel", "协程任务", "受限并发与定时流", "最多 2 个工作协程，返回值保持输入顺序。",
            "(1..5).parallelMap(concurrency = 2) { delay(10); it * it }\ntickerFlow(10.milliseconds, count = 3).toList()"),
        DemoCase("json", "JSON 数据", "类型序列化与合并", "忽略未知字段、格式化与 RFC 7396 Merge Patch。",
            "val codec = JsonCodec()\ncodec.encode(Profile(\"KMP\", 2))\ncodec.decode<Profile>(\"{\\\"name\\\":\\\"KMP\\\",\\\"extra\\\":1}\")"),
        DemoCase("memory", "本地数据", "内存键值存储", "同一接口读写 String、Long、Boolean；缺失值返回默认。",
            "val settings = MemorySettings()\nsettings.putLong(\"launches\", 1)\nsettings.getLong(\"launches\")"),
        DemoCase("persistent", "本地数据", "平台持久化", "命名空间为 demo，计数会跨应用重启保存；不清空其他数据。",
            "val settings = persistentSettings(\"demo\", platformContext)\nval count = settings.getLong(\"launches\") + 1\nsettings.putLong(\"launches\", count)"),
        DemoCase("file", "本地数据", "沙盒文件读写", "写入、追加、读取、列目录；示例文件仅位于宿主传入的目录。",
            "val files = FileStore(fileRoot.toPath())\nfiles.writeText(\"demo/hello.txt\", \"Hello\")\nfiles.appendText(\"demo/hello.txt\", \" KMP\")\nfiles.readText(\"demo/hello.txt\")"),
        DemoCase("platform", "平台能力", "系统信息与 UUID", "展示系统版本与架构；UUID 每次随机生成。",
            "platformInfo()\nrandomUuid()"),
        DemoCase("network", "网络请求", "HTTP 请求与结果", "用户主动运行时访问 https://example.com；超时 15 秒，不自动重试。",
            "val network = NetworkClient()\ntry { network.get<String>(\"https://example.com\") }\nfinally { network.close() }"),
    )

    suspend fun run(id: String, platformContext: Any? = null, fileRoot: String? = null): String = when (id) {
        "text" -> "脱敏: ${Text.mask("13812345678")}\n命名: ${Text.snakeCase("HTTPServerPort")}\n截断: ${Text.truncate("Kotlin Multiplatform", 10)}"
        "validation" -> "邮箱: ${Validation.email("hello@example.com")}\n手机号: ${Validation.mainlandMobile("13812345678")}\nIPv4: ${Validation.ipv4("192.168.1.1")}\nURL: ${Validation.httpUrl("https://example.com/docs")}\nLuhn: ${Validation.luhn("79927398713")}\n密码规则: ${Validation.password("HelloKmp!9")}"
        "collections" -> "第 2 页: ${(1..12).toList().page(1, 5)}\n频次: ${listOf("a", "b", "a").frequencies()}\n重复: ${listOf(1, 2, 1).duplicatesBy { it }}\n移动: ${listOf("a", "b", "c").move(0, 2)}"
        "numbers" -> "容量: ${Numbers.formatBytes(1_572_864)}\n小数: ${Numbers.round(3.14159, 3)}\nGCD: ${Numbers.gcd(54, 24)}\n插值: ${Numbers.lerp(0.0, 100.0, 0.25)}"
        "cache" -> { val c = LruCache<String, String>(2); c.put("a", "A"); c.put("b", "B"); c["a"]; c.put("c", "C"); "缓存: ${c.snapshot()}\nb 已淘汰: ${c["b"] == null}" }
        "logger" -> { val lines = mutableListOf<String>(); val logger = Logger("Demo", sink = LogSink { lines += "[${it.level}] ${it.message}" }); logger.debug { "filtered" }; logger.info { "ready" }; logger.warn { "sample warning" }; lines.joinToString("\n") }
        "codec" -> { val encoded = Codec.urlEncode("你好 + KMP"); "Base64: ${Codec.base64("你好 KMP".encodeToByteArray())}\nHex: ${Codec.hex(byteArrayOf(0, 15, -1))}\nURL: $encoded\n还原: ${Codec.urlDecode(encoded)}" }
        "hash" -> "SHA-256(abc):\n${Codec.sha256("abc".encodeToByteArray())}\nHMAC-SHA256:\n${Codec.hmacSha256("message".encodeToByteArray(), "key".encodeToByteArray())}"
        "time" -> { val time = TimeUtils { Instant.parse("2026-11-19T16:30:00Z") }; "上海日期: ${time.today(TimeZone.of("Asia/Shanghai"))}\n跨年天数: ${TimeUtils.daysBetween(LocalDate(2026, 12, 31), LocalDate(2027, 1, 1))}\n2028 闰年: ${TimeUtils.isLeapYear(2028)}" }
        "retry" -> retry(maxAttempts = 3, initialDelay = 10.milliseconds) { attempt -> if (attempt < 3) error("temporary"); "第 $attempt 次尝试成功" }
        "parallel" -> "平方: ${(1..5).parallelMap(2) { delay(10); it * it }}\n定时流: ${tickerFlow(10.milliseconds, 3).toList()}"
        "json" -> { val codec = JsonCodec(); val value = codec.encode(Profile("KMP", 2)); val merged = codec.mergePatch(codec.parse(value), codec.parse("""{"level":3}""")); "编码: $value\n解码: ${codec.decode<Profile>(value)}\n合并: ${codec.pretty(merged.toString())}" }
        "memory" -> { val s = MemorySettings(); s.putLong("launches", 1); s.putBoolean("enabled", true); "计数: ${s.getLong("launches")}\n开关: ${s.getBoolean("enabled")}\n默认: ${s.getString("missing", "fallback")}" }
        "persistent" -> { val s = persistentSettings("demo", platformContext); val count = s.getLong("launches") + 1; s.putLong("launches", count); "持久化计数: $count\n重新启动后再运行以检查保存结果。" }
        "file" -> { requireNotNull(fileRoot) { "宿主需要提供 fileRoot" }; val f = FileStore(fileRoot.toPath()); f.writeText("demo/hello.txt", "Hello"); f.appendText("demo/hello.txt", " KMP"); "内容: ${f.readText("demo/hello.txt")}\n字节: ${f.size("demo/hello.txt")}\n目录: ${f.list("demo")}" }
        "platform" -> "系统: ${platformInfo()}\n随机 UUID: ${randomUuid()}"
        "network" -> { val n = NetworkClient(); try { when (val r = n.get<String>("https://example.com")) { is ApiResult.Success -> "HTTP ${r.status}\n响应长度: ${r.value.length}"; is ApiResult.HttpFailure -> "HTTP 错误: ${r.status}"; is ApiResult.Failure -> "请求失败: ${r.cause.message}" } } finally { n.close() } }
        else -> error("Unknown demo: $id")
    }
}
