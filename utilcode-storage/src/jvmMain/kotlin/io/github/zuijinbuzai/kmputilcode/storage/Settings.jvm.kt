package io.github.zuijinbuzai.kmputilcode.storage

import java.util.prefs.Preferences

actual fun persistentSettings(name: String, platformContext: Any?): Settings {
    validateStoreName(name)
    val prefs = Preferences.userRoot().node("io/github/zuijinbuzai/kmputilcode/$name")
    return object : StringSettings() {
        override fun read(key: String): String? = prefs.get(key, null)
        override fun write(key: String, value: String) { prefs.put(key, value); prefs.flush() }
        override fun remove(key: String) { prefs.remove(key); prefs.flush() }
        override fun clear() { prefs.clear(); prefs.flush() }
    }
}
