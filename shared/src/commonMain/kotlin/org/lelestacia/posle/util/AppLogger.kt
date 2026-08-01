package org.lelestacia.posle.util

/**
 * Minimal cross-platform logger that delegates to the platform logging facility.
 * Use this instead of raw `println` so errors surface properly in production.
 */
expect object AppLogger {

    fun debug(tag: String, message: String)

    fun info(tag: String, message: String)

    fun warn(tag: String, message: String, throwable: Throwable? = null)

    fun error(tag: String, message: String, throwable: Throwable? = null)
}
