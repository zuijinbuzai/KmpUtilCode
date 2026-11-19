# 文件：utilcode-file

包 `io.github.zuijinbuzai.kmputilcode.file`，基于 Okio FileSystem。调用者选择应用拥有的根目录，后续路径都相对该目录。

```kotlin
import okio.Path.Companion.toPath
val files = FileStore("/your/app/owned/directory".toPath())
files.writeText("notes/hello.txt", "你好")
files.appendText("notes/hello.txt", " KMP")
files.readText("notes/hello.txt") // 你好 KMP
files.size("notes/hello.txt")     // 10L，UTF-8 字节数
files.list("notes")              // [notes/hello.txt]，排序后的相对路径
files.copy("notes/hello.txt", "backup/hello.txt")
files.exists("backup/hello.txt")
files.delete("backup/hello.txt")
```

构造时创建根目录并 canonicalize。可以通过第二个参数传入自定义 FileSystem。Android 传 `context.filesDir` 下的专用子目录；iOS 用 Application Support/Cache 的业务子目录；JVM 传业务配置目录或临时目录。

## 读取与写入

- `readBytes/readText` 默认最多读取 16 MiB；可配置 `maxBytes`（0 到 Int.MAX_VALUE-1）。最多多探测 1 字节来判断超限，超限抛 `IllegalArgumentException`，不会将任意大文件一次性读入内存。
- `readText` 严格 UTF-8 解码，坏数据抛异常。其他编码请自行读 bytes 转换。
- write 覆盖原文件，append 追加，两者都创建父目录。不是原子更新；中断时可能留下部分文件。并发写需要外部协调。
- list 返回当前目录的直接子项，不递归，并排除符号链接；`list()` 表示根目录。
- delete 不递归，只删除文件或空目录。默认文件不存在不报错；可 `mustExist=true`。
- copy 不流式报告进度，不保证目标已存在时的跨平台覆盖行为，建议业务先明确处理目标文件。

## 路径约束

拒绝绝对路径、`..`、反斜杠、冒号、NUL、根目录本身，以及路径链中存在的 symlink。不能把此检查当成对抗恶意并发替换的 OS 沙盒；根目录必须由应用控制。外部存储、Android SAF/content URI、iOS document picker 与文件分享不属于这个路径 API。

所有方法都是阻塞 I/O。Android/JVM UI 中放在 `withContext(Dispatchers.IO)`；iOS 由业务后台队列/协程调度，不在主线程读大文件。

## 测试与示例

`./gradlew :utilcode-file:jvmTest` 验证 Unicode/空文件、精确读取上限、复制/删除、目录遍历和符号链接拒绝。文件测试当前在 JVM 实际文件系统运行；iOS 的 Okio 调用另由 Demo 构建/手动运行覆盖。

Demo id `file`，只写宿主提供目录下的 `demo/hello.txt`。JVM Demo 使用临时目录，完成后删除临时文件；Android 使用 `filesDir/showcase`，iOS 使用 Application Support/KmpUtilCodeDemo。
