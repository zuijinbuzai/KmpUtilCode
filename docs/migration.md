# 从 AndroidUtilCode 的使用习惯迁移

KmpUtilCode 借鉴按场景组织 API 的方式，使用新的多平台实现，并非同名包替换。AndroidUtilCode 的 Android Framework 能力不能机械搬到 commonMain。

| AndroidUtilCode 常见分类 | 当前对应 | 迁移说明 |
|---|---|---|
| StringUtils | Text | 空值、默认、截断、脱敏、命名 |
| RegexUtils | Validation | 常用输入规则；不是所有旧正则合集 |
| ConvertUtils / EncodeUtils | Numbers / Codec | 容量、Base64/Hex/URL |
| EncryptUtils | Codec | 只提供 SHA/HMAC 摘要；未提供通用加密 |
| TimeUtils | time/TimeUtils | 显式时区与可注入时钟，使用 kotlinx-datetime |
| CollectionUtils | List/Iterable 扩展 | 分页、频次、重复组、移动 |
| CacheMemoryUtils | LruCache | 条目容量，单线程，不带 TTL |
| LogUtils | Logger + LogSink | 实例化配置，按需对接平台日志 |
| GsonUtils | JsonCodec | kotlinx.serialization，需要编译器插件 |
| SPUtils | Settings / persistentSettings | Android 显式 Context；多平台同一标量接口 |
| FileUtils / FileIOUtils | FileStore | app-owned 路径、限量读取；非 SAF URI |
| ThreadUtils / Utils.Task | coroutines | 结构化并发，不使用全局线程池单例 |
| NetworkUtils | NetworkClient / AndroidTools | HTTP 与系统网络状态分开 |
| DeviceUtils | platformInfo / randomUuid | 不暴露永久设备标识 |
| SizeUtils / PermissionUtils / ClipboardUtils / IntentUtils | AndroidTools 的部分能力 | Android 专属，权限仅检查、分享/浏览器显式调用 |
| Bitmap/Camera/Bluetooth/Location/Service/Activity 等 | 尚未实现 | 需要单独定义平台能力与生命周期，见路线图 |

## 典型重构

旧式全局初始化：把 Context 作为全局单例长期保存。新的方式：纯工具不需要 Context；持久化构造时传 applicationContext，系统操作每次传 Context。

旧式后台回调：任务可能在页面退出后继续更新 UI。新的方式：在页面/业务 Scope 启动 suspend 操作，页面销毁时取消，重试工具和 HTTP 都向上传递 CancellationException。

旧式时间字符串：默认时区不明确。新的方式：Instant 存绝对时间、LocalDate 存自然日、业务展示/筛选时显式传 TimeZone。

旧式文件绝对路径工具：可以误操作任意目录。新的方式：宿主先选定拥有的根目录，业务只传相对路径，并明确存储/读取上限。
