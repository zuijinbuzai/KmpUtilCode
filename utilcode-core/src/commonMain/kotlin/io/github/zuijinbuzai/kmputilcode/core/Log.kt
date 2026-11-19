package io.github.zuijinbuzai.kmputilcode.core

enum class LogLevel { DEBUG, INFO, WARN, ERROR }
data class LogEntry(val level: LogLevel, val tag: String, val message: String, val cause: Throwable? = null)
fun interface LogSink { fun write(entry: LogEntry) }
/** Instance-based logger. Configure once; the sink must provide its own thread safety. */
class Logger(
    private val tag: String,
    private val minimum: LogLevel = LogLevel.INFO,
    private val sink: LogSink = LogSink { println("[${it.level}][${it.tag}] ${it.message}") },
) {
    fun log(level: LogLevel, cause: Throwable? = null, message: () -> String) {
        if (level >= minimum) sink.write(LogEntry(level, tag, message(), cause))
    }
    fun debug(message: () -> String) = log(LogLevel.DEBUG, message = message)
    fun info(message: () -> String) = log(LogLevel.INFO, message = message)
    fun warn(message: () -> String) = log(LogLevel.WARN, message = message)
    fun error(cause: Throwable? = null, message: () -> String) = log(LogLevel.ERROR, cause, message)
}
