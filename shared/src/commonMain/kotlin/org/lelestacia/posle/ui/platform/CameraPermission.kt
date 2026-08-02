package org.lelestacia.posle.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

/**
 * Platform camera permission state.
 *
 * Implementations are provided per target:
 * - Android: backed by the OS permission flow (rationale dialog + prompt)
 * - Desktop (JVM): always granted, request is a no-op
 */
class CameraPermissionState internal constructor(
    val isGranted: State<Boolean>,
    internal val requestPermissionAction: () -> Unit,
) {
    /** Requests the camera permission (shows rationale + OS prompt on Android). */
    fun requestPermission() {
        requestPermissionAction()
    }
}

/**
 * Creates a platform camera permission state holder. Call from a composable
 * context. On Android the returned state tracks the real OS permission; on
 * desktop it is always granted.
 */
@Composable
expect fun rememberCameraPermissionState(): CameraPermissionState
