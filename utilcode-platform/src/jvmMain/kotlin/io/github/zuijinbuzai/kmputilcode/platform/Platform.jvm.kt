package io.github.zuijinbuzai.kmputilcode.platform

import java.util.UUID
actual fun platformInfo(): PlatformInfo = PlatformInfo(System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch"))
actual fun randomUuid(): String = UUID.randomUUID().toString()
