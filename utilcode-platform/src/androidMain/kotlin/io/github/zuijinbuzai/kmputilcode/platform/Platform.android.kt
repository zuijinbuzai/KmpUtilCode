package io.github.zuijinbuzai.kmputilcode.platform

import android.os.Build
import java.util.UUID
actual fun platformInfo(): PlatformInfo = PlatformInfo("Android", Build.VERSION.RELEASE, Build.SUPPORTED_ABIS.firstOrNull() ?: "unknown")
actual fun randomUuid(): String = UUID.randomUUID().toString()
