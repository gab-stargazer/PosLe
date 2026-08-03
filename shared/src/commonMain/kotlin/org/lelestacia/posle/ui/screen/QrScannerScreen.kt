package org.lelestacia.posle.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.lelestacia.posle.domain.component.qr_scanner.QrScannerComponent
import org.lelestacia.posle.ui.platform.PlatformUi
import org.lelestacia.posle.ui.screen.qr_scanner.QrScannerOverlay
import org.lelestacia.posle.ui.theme.AppTheme

@Composable
fun QrScannerScreen(
    component: QrScannerComponent,
    modifier: Modifier = Modifier
) {
    Scaffold(
        contentWindowInsets = WindowInsets(),
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PlatformUi.QrScanner(
                onScanned = { qrData ->
                    component.onScannedQr(qrData)
                }
            )

            QrScannerOverlay(
                onDismiss = {
                    component.onDismiss()
                }
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

                override fun onDismiss() {

                }
            }
        )
    }
}
