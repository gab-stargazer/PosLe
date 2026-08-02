package org.lelestacia.posle.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun platformAppVersion(): String {
    val context = LocalContext.current
    return rememberVersionName(context)
}

@Composable
private fun rememberVersionName(context: android.content.Context): String {
    return androidx.compose.runtime.remember(context) {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: ""
    }
}
