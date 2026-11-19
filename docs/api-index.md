# 公开 API 索引

统一包前缀：`io.github.zuijinbuzai.kmputilcode`。详细参数、默认值、异常与线程约束见各模块手册和源文件 KDoc。

| 子包 | 类型/入口 | 公开成员 |
|---|---|---|
| core | Text | isBlank、orDefault、truncate、mask、snakeCase、camelCase |
| core | Validation | email、mainlandMobile、ipv4、httpUrl、luhn、password |
| core | List/Iterable 扩展 | page、frequencies、duplicatesBy、move |
| core | Numbers | round、formatBytes、gcd、lerp |
| core | LruCache | capacity、size、get、put、remove、clear、snapshot |
| core | Logger | log、debug、info、warn、error；LogLevel、LogEntry、LogSink |
| codec | Codec | base64、decodeBase64、hex、decodeHex、sha256、sha512、hmacSha256、urlEncode、urlDecode |
| time | TimeSource/SystemTimeSource | now |
| time | TimeUtils 实例 | now、today、startOfToday、elapsedSince |
| time | TimeUtils companion | parseIso、fromEpochMillis、iso、date、startOfDay、daysBetween、plusDays、isLeapYear、weekday |
| coroutines | 顶层函数 | retry、parallelMap、tickerFlow |
| coroutines | TaskGate | run |
| serialization | JsonCodec | json、encode、decode、parse、pretty、mergePatch、defaultJson |
| storage | Settings | contains、getString/putString、getLong/putLong、getBoolean/putBoolean、remove、clear |
| storage | 实现/工厂 | MemorySettings、StringSettings、persistentSettings |
| file | FileStore | exists、size、readBytes、readText、writeBytes、writeText、appendText、list、delete、copy |
| network | 工厂/客户端 | platformHttpEngine、createHttpClient、NetworkClient.get/request/close |
| network | ApiResult | Success(value,status)、HttpFailure(status)、Failure(cause) |
| platform | 通用 | PlatformInfo、platformInfo、randomUuid |
| platform / androidMain | AndroidTools | dpToPx、pxToDp、hasPermission、copyText、hasValidatedInternet、openUrl、shareText |
| 根包 | UtilCode | VERSION |

## 按场景查找

- 表单校验/展示脱敏 → core/Validation、Text。
- 列表分页、去重组统计、拖动排序 → core 扩展。
- API 签名/URL 参数/二进制文本 → codec；HTTP query 优先用 Ktor parameter。
- 日期筛选/报表自然日 → time，显式选择 TimeZone。
- 多请求并发、退避/轮询 → coroutines；注意幂等和生命周期。
- 配置与响应数据 → serialization；偏好项 → storage；大文件 → file 之外的流式 API。
- HTTP/JSON → network；Android 系统操作 → platform/AndroidTools。

公共入口约百个（包括接口成员与扩展），以本索引和源码实际成员为准，不包含尚未实现的路线图项。
