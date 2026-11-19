package io.github.zuijinbuzai.kmputilcode.storage

/** Non-sensitive scalar preferences. Compound read/modify/write operations are not atomic. */
interface Settings {
    fun contains(key: String): Boolean
    fun getString(key: String, default: String = ""): String
    fun putString(key: String, value: String)
    fun getLong(key: String, default: Long = 0): Long
    fun putLong(key: String, value: Long)
    fun getBoolean(key: String, default: Boolean = false): Boolean
    fun putBoolean(key: String, value: Boolean)
    fun remove(key: String)
    /** Removes keys from this named store only. */
    fun clear()
}

/** Thread confined test/session storage. Type mismatches return the supplied default. */
class MemorySettings : Settings {
    private val values = mutableMapOf<String, Any>()
    override fun contains(key: String): Boolean = key in values
    override fun getString(key: String, default: String): String = values[key] as? String ?: default
    override fun putString(key: String, value: String) { values[key] = value }
    override fun getLong(key: String, default: Long): Long = values[key] as? Long ?: default
    override fun putLong(key: String, value: Long) { values[key] = value }
    override fun getBoolean(key: String, default: Boolean): Boolean = values[key] as? Boolean ?: default
    override fun putBoolean(key: String, value: Boolean) { values[key] = value }
    override fun remove(key: String) { values.remove(key) }
    override fun clear() = values.clear()
}

/** Android requires a Context; iOS uses NSUserDefaults, JVM uses Preferences. */
expect fun persistentSettings(name: String = "kmputilcode", platformContext: Any? = null): Settings

/** Encodes typed values with a tag so all persistent backends share mismatch semantics. */
abstract class StringSettings : Settings {
    protected abstract fun read(key: String): String?
    protected abstract fun write(key: String, value: String)
    override fun contains(key: String): Boolean = read(key) != null
    override fun getString(key: String, default: String): String = read(key)?.takeIf { it.startsWith("s:") }?.drop(2) ?: default
    override fun putString(key: String, value: String) = write(key, "s:$value")
    override fun getLong(key: String, default: Long): Long = read(key)?.takeIf { it.startsWith("l:") }?.drop(2)?.toLongOrNull() ?: default
    override fun putLong(key: String, value: Long) = write(key, "l:$value")
    override fun getBoolean(key: String, default: Boolean): Boolean = when (read(key)) { "b:true" -> true; "b:false" -> false; else -> default }
    override fun putBoolean(key: String, value: Boolean) = write(key, "b:$value")
}
internal fun validateStoreName(name: String) {
    require(name.length in 1..64 && name.all { it.isLetterOrDigit() || it in "._-" }) { "Store name: 1..64 letters, digits, '.', '_' or '-'" }
}
