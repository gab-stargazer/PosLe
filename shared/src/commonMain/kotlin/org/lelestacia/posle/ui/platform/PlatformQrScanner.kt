package org.lelestacia.posle.ui.platform

import androidx.compose.runtime.Composable
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.util.Name

/**
 * Renders the platform-specific QR scanner. Platforms without a camera
 * (e.g. desktop JVM) render nothing.
 */
@Composable
internal expect fun platformQrScanner(
    onScanned: (String) -> Unit,
)

/**
 * Whether the Bluetooth permission has been granted on the current platform.
 */
@Composable
internal expect fun platformBluetoothPermissionGranted(): Boolean

/**
 * Prints a transaction receipt. No-op on platforms without a printer.
 */
internal expect fun platformPrintTransaction(
    transaction: Transaction,
    storeNameValue: Name,
)
