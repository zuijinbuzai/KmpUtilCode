package io.github.zuijinbuzai.kmputilcode.core

import kotlin.math.abs
import kotlin.math.round
import kotlin.math.pow

object Numbers {
    fun round(value: Double, decimals: Int = 2): Double {
        require(value.isFinite() && decimals in 0..9)
        val factor = 10.0.pow(decimals)
        require((value * factor).isFinite()) { "Value is too large" }
        return kotlin.math.round(value * factor) / factor
    }
    /** SI-independent binary units, deterministic dot decimal separator. */
    fun formatBytes(bytes: Long): String {
        require(bytes >= 0)
        if (bytes < 1024) return "$bytes B"
        val units = listOf("KiB", "MiB", "GiB", "TiB", "PiB", "EiB")
        var amount = bytes.toDouble() / 1024
        var unit = 0
        while (amount >= 1024 && unit < units.lastIndex) { amount /= 1024; unit++ }
        val tenths = kotlin.math.round(amount * 10).toLong()
        return "${tenths / 10}.${tenths % 10} ${units[unit]}"
    }
    fun gcd(a: Long, b: Long): Long {
        require(a != Long.MIN_VALUE && b != Long.MIN_VALUE)
        var x = abs(a); var y = abs(b)
        while (y != 0L) { val r = x % y; x = y; y = r }
        return x
    }
    fun lerp(start: Double, end: Double, fraction: Double): Double = start + (end - start) * fraction
}
