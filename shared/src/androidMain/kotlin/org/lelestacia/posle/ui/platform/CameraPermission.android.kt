package org.lelestacia.posle.ui.platform

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import com.meticha.permissions_compose.AppPermission
import com.meticha.permissions_compose.rememberAppPermissionState

@Composable
actual fun rememberCameraPermissionState(): CameraPermissionState {
    val permissionState = rememberAppPermissionState(
        permissions = listOf(
            AppPermission(
                permission = Manifest.permission.CAMERA,
                description = "",
                isRequired = true
            )
        )
    )
    val isGranted: State<Boolean> = remember(permissionState) {
        derivedStateOf { permissionState.allRequiredGranted() }
    }
    return remember(permissionState) {
        CameraPermissionState(
            isGranted = isGranted,
            requestPermissionAction = permissionState::requestPermission
        )
    }
}
