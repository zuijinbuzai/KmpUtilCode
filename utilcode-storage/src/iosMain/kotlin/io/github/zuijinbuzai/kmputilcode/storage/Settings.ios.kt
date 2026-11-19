package io.github.zuijinbuzai.kmputilcode.storage

import platform.Foundation.NSUserDefaults

actual fun persistentSettings(name: String, platformContext: Any?): Settings {
    validateStoreName(name)
    val prefs = NSUserDefaults(suiteName = "io.github.zuijinbuzai.kmputilcode.$name")
    return object : StringSettings() {
        private val prefix = "kmputilcode:"
        override fun read(key: String): String? = prefs.stringForKey(prefix + key)
        override fun write(key: String, value: String) { prefs.setObject(value, forKey = prefix + key) }
        override fun remove(key: String) { prefs.removeObjectForKey(prefix + key) }
        override fun clear() {
            prefs.dictionaryRepresentation().keys.filterIsInstance<String>().filter { it.startsWith(prefix) }
                .forEach { prefs.removeObjectForKey(it) }
        }
    }
}
