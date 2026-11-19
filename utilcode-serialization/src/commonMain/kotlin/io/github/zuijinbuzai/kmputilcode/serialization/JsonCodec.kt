package io.github.zuijinbuzai.kmputilcode.serialization

import kotlinx.serialization.json.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

/** No global mutable configuration; pass your own Json for different policies. */
class JsonCodec(val json: Json = defaultJson) {
    inline fun <reified T> encode(value: T): String = json.encodeToString(value)
    inline fun <reified T> decode(value: String): T = json.decodeFromString(value)
    fun parse(value: String): JsonElement = json.parseToJsonElement(value)
    fun pretty(value: String): String = Json(json) { prettyPrint = true }.encodeToString(JsonElement.serializer(), parse(value))
    /** RFC 7396 merge patch: null deletes object members, arrays/scalars replace their target. */
    fun mergePatch(target: JsonElement, patch: JsonElement): JsonElement {
        if (patch !is JsonObject) return patch
        val result = (target as? JsonObject)?.toMutableMap() ?: mutableMapOf()
        for ((key, value) in patch) {
            if (value == JsonNull) result.remove(key)
            else result[key] = mergePatch(result[key] ?: JsonNull, value)
        }
        return JsonObject(result)
    }
    companion object {
        val defaultJson: Json = Json { ignoreUnknownKeys = true; encodeDefaults = true; isLenient = false }
    }
}
