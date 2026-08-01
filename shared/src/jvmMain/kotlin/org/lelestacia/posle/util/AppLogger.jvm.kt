package org.lelestacia.posle.util

actual object AppLogger {

    actual fun debug(tag: String, message: String) {
        println("$tag: $message")
    }

    actual fun info(tag: String, message: String) {
        println("$tag: $message")
    }

    actual fun warn(tag: String, message: String, throwable: Throwable?) {
        System.err.println("$tag: $message")
        throwable?.printStackTrace()
    }

    actual fun error(tag: String, message: String, throwable: Throwable?) {
        System.err.println("$tag: $message")
        throwable?.printStackTrace()
    }
}
