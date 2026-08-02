package org.lelestacia.posle.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Desktop (JVM) app version.
 *
 * The Compose Desktop Gradle plugin writes `packageVersion` into the jar
 * manifest as `Implementation-Version` when a native distribution is built.
 * When running from an IDE the manifest may be absent, so fall back to the
 * version declared in the desktop build file.
 */
@Composable
actual fun platformAppVersion(): String {
    return remember {
        val implementationVersion = PlatformAppVersionActual::class.java
            .`package`
            ?.implementationVersion
        implementationVersion ?: DESKTOP_FALLBACK_VERSION
    }
}

private object PlatformAppVersionActual

private const val DESKTOP_FALLBACK_VERSION = "1.0.0"
