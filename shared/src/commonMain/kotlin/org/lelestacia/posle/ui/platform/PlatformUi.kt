package org.lelestacia.posle.ui.platform

import androidx.compose.runtime.Composable

/**
 * Central place for platform-specific behavior that the shared UI depends on.
 *
 * Implementations are provided per target:
 * - Android: real camera permission, QR scanning, image picking
 * - Desktop (JVM): no-op / stub implementations (UI is phone-only anyway)
 */
object PlatformUi {
    /**
     * Creates a composable QR scanner. On platforms without a camera this
     * renders nothing.
     */
    @Composable
    fun QrScanner(
        onScanned: (String) -> Unit,
    ) {
        platformQrScanner(onScanned = onScanned)
    }

    /**
     * Whether the Bluetooth permission has been granted on the current
     * platform. Always true on platforms without Bluetooth.
     */
    @Composable
    fun isBluetoothPermissionGranted(): Boolean = platformBluetoothPermissionGranted()

    /**
     * Prints a transaction receipt to a physical printer. On platforms
     * without a printer (e.g. desktop) this is a no-op.
     */
    fun printTransaction(
        transaction: org.lelestacia.posle.domain.model.Transaction,
        storeNameValue: org.lelestacia.posle.util.Name,
    ) {
        platformPrintTransaction(transaction = transaction, storeNameValue = storeNameValue)
    }
}
