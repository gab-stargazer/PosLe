package org.lelestacia.posle.ui.platform

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.meticha.permissions_compose.AppPermission
import com.meticha.permissions_compose.rememberAppPermissionState

@Composable
actual fun rememberNotificationPermissionState(): NotificationPermissionState {
    // POST_NOTIFICATIONS only exists on API 33+; on older versions it is
    // implicitly granted, so skip the permission flow entirely.
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        val isGranted: State<Boolean> = remember { mutableStateOf(true) }
        return remember {
            NotificationPermissionState(
                isGranted = isGranted,
                requestPermissionAction = {}
            )
        }
    }
    val permissionState = rememberAppPermissionState(
        permissions = listOf(
            AppPermission(
                permission = Manifest.permission.POST_NOTIFICATIONS,
                description = "Izin dibutuhkan untuk menampilkan notifikasi saat laporan selesai",
                isRequired = true
            )
        )
    )
    val isGranted: State<Boolean> = remember(permissionState) {
        derivedStateOf { permissionState.allRequiredGranted() }
    }
    return remember(permissionState) {
        NotificationPermissionState(
            isGranted = isGranted,
            requestPermissionAction = permissionState::requestPermission
        )
    }
}
