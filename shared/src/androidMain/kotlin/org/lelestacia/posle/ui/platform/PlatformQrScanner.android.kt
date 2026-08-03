package org.lelestacia.posle.ui.platform

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.Manifest
import com.meticha.permissions_compose.AppPermission
import com.meticha.permissions_compose.rememberAppPermissionState
import org.ncgroup.kscan.BarcodeFormat
import org.ncgroup.kscan.BarcodeResult
import org.ncgroup.kscan.ScannerView

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

    ScannerView(
        modifier = Modifier.fillMaxSize(),
        // Product barcodes only. UPC-A reads back (and is a subset of) EAN-13,
        // so accept all retail product formats to avoid infinite auto-zoom.
        codeTypes = listOf(
            BarcodeFormat.FORMAT_UPC_A,
            BarcodeFormat.FORMAT_UPC_E,
            BarcodeFormat.FORMAT_EAN_13,
            BarcodeFormat.FORMAT_EAN_8,
            BarcodeFormat.FORMAT_QR_CODE
        ),
        scannerUiOptions = null
    ) { result ->
        when (result) {
            is BarcodeResult.OnSuccess -> {
                onScanned(result.barcode.data)
            }
            is BarcodeResult.OnFailed -> {
                // Ignore transient scan failures; keep scanning.
            }
            BarcodeResult.OnCanceled -> {
                // User dismissed the scanner.
            }
        }
    }
}
