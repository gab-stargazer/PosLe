package org.lelestacia.posle.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.lelestacia.posle.domain.component.qr_scanner.QrScannerComponent
import org.lelestacia.posle.ui.theme.AppTheme
import qrscanner.CameraLens
import qrscanner.OverlayShape
import qrscanner.QrCodeScanner

@Composable
fun QrScannerScreen(
    component: QrScannerComponent,
    modifier: Modifier = Modifier
) {
    Scaffold(
        contentWindowInsets = WindowInsets(),
        modifier = modifier
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            QrCodeScanner(
                flashlightOn = false,
                cameraLens = CameraLens.Back,
                onCompletion = { qrData ->
                    component.onScannedQr(qrData)
                },
                overlayShape = OverlayShape.Square,
                overlayColor = MaterialTheme.colorScheme.onSurface.copy(0.5F),
                overlayBorderColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                zoomLevel = 1F,
                maxZoomLevel = 10F,
                permissionDeniedView = {

                },
                customOverlay = null,
                modifier = Modifier
                    .fillMaxSize()

            )
        }
    }
}

@Preview
@Composable
private fun PreviewQrScannerScreen() {
    AppTheme {
        QrScannerScreen(
            component = object : QrScannerComponent {

                override fun onScannedQr(qrData: String) {

                }
            }
        )
    }
}