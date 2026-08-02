package org.lelestacia.posle.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
actual fun rememberCameraPermissionState(): CameraPermissionState {
    val isGranted: State<Boolean> = remember { mutableStateOf(true) }
    return remember {
        CameraPermissionState(
            isGranted = isGranted,
            requestPermissionAction = {}
        )
    }
}
