package org.lelestacia.posle.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

/**
 * Platform notification (POST_NOTIFICATIONS) permission state.
 *
 * Implementations are provided per target:
 * - Android: backed by the OS permission flow (rationale dialog + prompt);
 *   on API < 33 the permission does not exist and is treated as granted.
 * - Desktop (JVM): always granted, request is a no-op
 */
class NotificationPermissionState internal constructor(
    val isGranted: State<Boolean>,
    internal val requestPermissionAction: () -> Unit,
) {
    /** Requests the notification permission (shows rationale + OS prompt on Android). */
    fun requestPermission() {
        requestPermissionAction()
    }
}

/**
 * Creates a platform notification permission state holder. Call from a composable
 * context. On Android the returned state tracks the real OS permission; on
 * desktop it is always granted.
 */
@Composable
expect fun rememberNotificationPermissionState(): NotificationPermissionState
