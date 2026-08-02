package org.lelestacia.posle.ui.platform

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    // No-op on desktop: there is no system back button.
}
