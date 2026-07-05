package org.lelestacia.posle.domain.component.qr_scanner

interface QrScannerComponent {
    fun onScannedQr(qrData: String)
}