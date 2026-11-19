# 编码与摘要：utilcode-codec

包 `io.github.zuijinbuzai.kmputilcode.codec`；依赖 Okio 的多平台 ByteString 实现，不自制密码学原语。

```kotlin
val input = "你好 KMP".encodeToByteArray()
val encoded = Codec.base64(input)
val restored = Codec.decodeBase64(encoded)?.decodeToString()
Codec.hex(byteArrayOf(0, 15, -1))          // 000fff
Codec.decodeHex("000fff")                // [0,15,-1]
Codec.sha256("abc".encodeToByteArray())   // ba7816bf...15ad
Codec.sha512(input)                      // 128 个十六进制字符
Codec.hmacSha256(input, "key".encodeToByteArray())
```

`decodeBase64` 非法时返回 null，沿用 Okio 对空白和 URL-safe 字符的兼容解码行为。`decodeHex` 对奇数长度或非法字符抛 `IllegalArgumentException`。摘要返回小写 hex。

## URL 组件

```kotlin
val encoded = Codec.urlEncode("你好 + KMP")
// %E4%BD%A0%E5%A5%BD%20%2B%20KMP
Codec.urlDecode(encoded)                  // 你好 + KMP
Codec.urlDecode("a+b")                    // a+b
Codec.urlDecode("a+b", formEncoded = true)// a b
```

只编码单个路径/查询组件，不能把完整 URL 整体编码后直接请求。不保留 `/?:&=` 等分隔符。采用 UTF-8 与 RFC 3986 unreserved 集合，空格编码为 `%20`。百分号序列不完整、非 hex 或结果不是合法 UTF-8 时抛异常，避免无声替换坏数据。普通原始 Unicode 可以和百分号序列混合解码。

SHA 用于摘要/完整性比较，不能直接用于密码存储；HMAC 要求业务安全管理密钥。这里不提供加密、安全随机密钥管理、证书绕过或旧式 MD5/DES 封装。

测试包含 SHA-256/HMAC 官方风格已知向量、Unicode/emoji 往返和坏输入。命令：`./gradlew :utilcode-codec:jvmTest`；Demo id：`codec`、`hash`。
