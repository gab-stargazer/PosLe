package org.lelestacia.posle.ui.platform

import androidx.compose.runtime.Composable

/**
 * The current application version string, as reported by the platform
 * (e.g. Android PackageManager versionName or the desktop package version).
 */
@Composable
expect fun platformAppVersion(): String
