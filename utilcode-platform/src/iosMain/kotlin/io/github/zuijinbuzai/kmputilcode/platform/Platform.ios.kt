package io.github.zuijinbuzai.kmputilcode.platform

import platform.Foundation.NSProcessInfo
import platform.Foundation.NSUUID
import platform.UIKit.UIDevice
actual fun platformInfo(): PlatformInfo = PlatformInfo("iOS", UIDevice.currentDevice.systemVersion, NSProcessInfo.processInfo.environment["SIMULATOR_MODEL_IDENTIFIER"] as? String ?: "arm64")
actual fun randomUuid(): String = NSUUID().UUIDString.lowercase()
