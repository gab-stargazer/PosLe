package org.lelestacia.posle.domain.component.qr_scanner

import com.arkivanov.decompose.ComponentContext

class QrScannerComponentImpl(
    private val componentContext: ComponentContext,
    private val onQrScanned: (String) -> Unit,
    private val onDismiss: () -> Unit,
) : QrScannerComponent, ComponentContext by componentContext {

    override fun onScannedQr(qrData: String) {
        onQrScanned(qrData)
    }

    override fun onDismiss() {
        onDismiss.invoke()
    }
}