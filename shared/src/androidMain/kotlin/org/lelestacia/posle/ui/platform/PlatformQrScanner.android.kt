package org.lelestacia.posle.ui.platform

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.Manifest
import com.meticha.permissions_compose.AppPermission
import com.meticha.permissions_compose.rememberAppPermissionState
import qrscanner.CameraLens
import qrscanner.OverlayShape
import qrscanner.QrCodeScanner

@Composable
internal actual fun platformQrScanner(
    onScanned: (String) -> Unit,
) {
    val cameraPermission = rememberAppPermissionState(
        permissions = listOf(
            AppPermission(
                permission = Manifest.permission.CAMERA,
                description = "",
                isRequired = true
            )
        )
    )

    QrCodeScanner(
        flashlightOn = false,
        cameraLens = CameraLens.Back,
        onCompletion = { qrData ->
            onScanned(qrData)
        },
        overlayShape = OverlayShape.Rectangle,
        overlayColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f),
        overlayBorderColor = androidx.compose.ui.graphics.Color.White,
        zoomLevel = 1f,
        maxZoomLevel = 10f,
        permissionDeniedView = {
            // Permission prompt handled by the app-level rationale dialog.
        },
        customOverlay = null,
        modifier = Modifier.fillMaxSize()
    )
}
