package io.github.zuijinbuzai.kmputilcode.core

/** Practical input checks, not identity verification or full RFC validators. */
object Validation {
    private val emailPattern = Regex("^[A-Za-z0-9.!#%&'*+/=?^_`{|}~-]+@[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?)+$")
    fun email(value: String): Boolean = value.length <= 254 && emailPattern.matches(value) &&
        value.substringBefore('@').let { it.length <= 64 && !it.startsWith('.') && !it.endsWith('.') && ".." !in it }
    fun mainlandMobile(value: String): Boolean = Regex("^1[3-9][0-9]{9}$").matches(value)
    fun ipv4(value: String): Boolean {
        val parts = value.split('.')
        return parts.size == 4 && parts.all { part ->
            part.isNotEmpty() && part.length <= 3 && part.all { it in '0'..'9' } &&
                (part.length == 1 || part[0] != '0') && (part.toIntOrNull() ?: 256) <= 255
        }
    }
    fun httpUrl(value: String): Boolean = Regex("^https?://[^\\s/?#@:]+(?::[0-9]{1,5})?(?:[/?#][^\\s]*)?$").matches(value) &&
        value.substringAfter("://").substringBefore('/').substringBefore('?').substringBefore('#')
            .substringAfter(':', "").let { it.isEmpty() || (it.toIntOrNull() ?: 0) in 1..65535 }
    fun luhn(value: String): Boolean {
        if (value.length !in 2..19 || value.any { it !in '0'..'9' }) return false
        return value.reversed().mapIndexed { index, c ->
            val n = (c - '0') * if (index % 2 == 0) 1 else 2
            if (n > 9) n - 9 else n
        }.sum() % 10 == 0
    }
    fun password(value: String, minLength: Int = 8): Boolean {
        require(minLength > 0)
        return value.length >= minLength && value.any { it.isUpperCase() } && value.any { it.isLowerCase() } &&
            value.any { it in '0'..'9' } && value.any { !it.isLetterOrDigit() && !it.isWhitespace() }
    }
}
