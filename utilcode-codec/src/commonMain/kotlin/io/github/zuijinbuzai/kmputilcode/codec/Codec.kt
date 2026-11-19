package io.github.zuijinbuzai.kmputilcode.codec

import okio.ByteString.Companion.decodeBase64
import okio.ByteString.Companion.decodeHex
import okio.ByteString.Companion.toByteString

object Codec {
    fun base64(bytes: ByteArray): String = bytes.toByteString().base64()
    /** Returns null for invalid input; this follows Okio's whitespace-tolerant decoder. */
    fun decodeBase64(value: String): ByteArray? = value.decodeBase64()?.toByteArray()
    fun hex(bytes: ByteArray): String = bytes.toByteString().hex()
    fun decodeHex(value: String): ByteArray = value.decodeHex().toByteArray()
    fun sha256(bytes: ByteArray): String = bytes.toByteString().sha256().hex()
    fun sha512(bytes: ByteArray): String = bytes.toByteString().sha512().hex()
    fun hmacSha256(bytes: ByteArray, key: ByteArray): String = bytes.toByteString().hmacSha256(key.toByteString()).hex()
    /** Encodes one RFC 3986 query/path component. Spaces become %20, not '+'. */
    fun urlEncode(value: String): String = buildString {
        for (byte in value.encodeToByteArray()) {
            val n = byte.toInt() and 255
            val c = n.toChar()
            if (c in 'a'..'z' || c in 'A'..'Z' || c in '0'..'9' || c in "-._~") append(c)
            else { append('%'); append("0123456789ABCDEF"[n shr 4]); append("0123456789ABCDEF"[n and 15]) }
        }
    }
    /** Strict percent/UTF-8 decoding. '+' is preserved unless formEncoded is true. */
    fun urlDecode(value: String, formEncoded: Boolean = false): String {
        val bytes = ArrayList<Byte>()
        var index = 0
        while (index < value.length) {
            when (val c = value[index]) {
                '%' -> {
                    require(index + 2 < value.length) { "Incomplete percent escape at $index" }
                    val hi = value[index + 1].digitToIntOrNull(16)
                    val lo = value[index + 2].digitToIntOrNull(16)
                    require(hi != null && lo != null) { "Invalid percent escape at $index" }
                    bytes.add(((hi shl 4) or lo).toByte()); index += 3
                }
                else -> {
                    if (c == '+' && formEncoded) bytes.add(32)
                    else {
                        val end = if (c.isHighSurrogate() && index + 1 < value.length && value[index + 1].isLowSurrogate()) index + 2 else index + 1
                        bytes.addAll(value.substring(index, end).encodeToByteArray().toList())
                        index = end - 1
                    }
                    index++
                }
            }
        }
        return bytes.toByteArray().decodeToString(throwOnInvalidSequence = true)
    }
}
