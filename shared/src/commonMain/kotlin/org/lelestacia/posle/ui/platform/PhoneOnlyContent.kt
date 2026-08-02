package org.lelestacia.posle.ui.platform

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/**
 * A composable that renders [content] only when the available screen space
 * matches a phone-like form factor (portrait phone dimensions).
 *
 * The gate uses both width and height thresholds so that large desktop
 * windows never show the phone UI.
 */
@Composable
fun PhoneOnlyContent(
    content: @Composable () -> Unit
) {
    BoxWithConstraints {
        val isPhoneSized = maxWidth <= PHONE_MAX_WIDTH_DP && maxHeight <= PHONE_MAX_HEIGHT_DP
        if (isPhoneSized) {
            content()
        }
    }
}

private val PHONE_MAX_WIDTH_DP = 600.dp
private val PHONE_MAX_HEIGHT_DP = 1000.dp
