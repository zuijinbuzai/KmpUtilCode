package io.github.zuijinbuzai.kmputilcode.platform

/** Information about the OS/runtime, never a persistent device identifier. */
data class PlatformInfo(val name: String, val version: String, val architecture: String)
expect fun platformInfo(): PlatformInfo
/** Random UUID v4, suitable for correlation IDs; not a secret or a user/device identity. */
expect fun randomUuid(): String
