package org.lelestacia.posle.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.lelestacia.posle.domain.component.qr_scanner.QrScannerComponent
import org.lelestacia.posle.ui.platform.PlatformUi
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PlatformUi.QrScanner(
                onScanned = { qrData ->
                    component.onScannedQr(qrData)
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
            }
        )
    }
}
