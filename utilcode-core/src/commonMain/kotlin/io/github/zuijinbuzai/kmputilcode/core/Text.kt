package io.github.zuijinbuzai.kmputilcode.core

/** Small, locale-independent string operations. Indices follow Kotlin UTF-16 semantics. */
object Text {
    fun isBlank(value: CharSequence?): Boolean = value.isNullOrBlank()
    fun orDefault(value: String?, default: String = ""): String = value?.takeIf { it.isNotBlank() } ?: default
    fun truncate(value: String, maxLength: Int, suffix: String = "…"): String {
        require(maxLength >= 0) { "maxLength must be non-negative" }
        if (value.length <= maxLength) return value
        return if (suffix.length >= maxLength) suffix.take(maxLength)
        else value.take(maxLength - suffix.length) + suffix
    }
    fun mask(value: String, visibleStart: Int = 3, visibleEnd: Int = 4, mask: Char = '*'): String {
        require(visibleStart >= 0 && visibleEnd >= 0)
        require(visibleStart <= value.length && visibleEnd <= value.length - visibleStart) {
            "Visible ranges must not overlap or exceed the string"
        }
        return value.take(visibleStart) + mask.toString().repeat(value.length - visibleStart - visibleEnd) + value.takeLast(visibleEnd)
    }
    fun snakeCase(value: String): String = value.trim()
        .replace(Regex("([A-Z]+)([A-Z][a-z])"), "$1_$2")
        .replace(Regex("([a-z0-9])([A-Z])"), "$1_$2")
        .replace(Regex("[\\s-]+"), "_").lowercase()
    fun camelCase(value: String): String = snakeCase(value).split('_').filter { it.isNotEmpty() }
        .mapIndexed { index, word -> if (index == 0) word else word.replaceFirstChar { it.uppercaseChar() } }.joinToString("")
}
