package io.github.zuijinbuzai.kmputilcode.storage

import android.content.Context

actual fun persistentSettings(name: String, platformContext: Any?): Settings {
    validateStoreName(name)
    require(platformContext is Context) { "Pass an Android Context as platformContext" }
    val prefs = platformContext.applicationContext.getSharedPreferences("kmputilcode.$name", Context.MODE_PRIVATE)
    return object : StringSettings() {
        override fun read(key: String): String? = prefs.getString(key, null)
        override fun write(key: String, value: String) { prefs.edit().putString(key, value).apply() }
        override fun remove(key: String) { prefs.edit().remove(key).apply() }
        override fun clear() { prefs.edit().clear().apply() }
    }
}
