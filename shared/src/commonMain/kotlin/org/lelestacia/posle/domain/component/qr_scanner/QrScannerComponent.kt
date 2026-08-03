package org.lelestacia.posle.domain.component.qr_scanner

/**
 * Component interface for the QR/Barcode Scanner.
 */
interface QrScannerComponent {
    /**
     * Callback triggered when a QR code or Barcode is successfully scanned.
     *
     * @param qrData The raw string data decoded from the scan.
     */
    fun onScannedQr(qrData: String)

    /**
     * Dismisses the QR scanner screen.
     */
    fun onDismiss()
}