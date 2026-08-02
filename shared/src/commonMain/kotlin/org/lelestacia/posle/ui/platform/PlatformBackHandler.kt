package org.lelestacia.posle.ui.platform

import androidx.compose.runtime.Composable

/**
 * A platform back handler. On Android this intercepts the system back
 * button; on desktop it is a no-op (there is no system back button).
 */
@Composable
expect fun PlatformBackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit,
)
