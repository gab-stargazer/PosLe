package org.lelestacia.posle

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.koin.java.KoinJavaComponent.get
import org.lelestacia.posle.ui.platform.PhoneOnlyContent
import org.lelestacia.posle.util.DesktopNotifier
import org.lelestacia.posle.util.FileStorage

fun main() = application {
    val windowState = rememberWindowState(
        size = DpSize(1280.dp, 800.dp),
        position = WindowPosition(Alignment.Center)
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "PosLe",
        state = windowState,
    ) {
        App(
            content = {
                PhoneOnlyContent {
                    // Desktop is not a phone-sized window, so the phone UI
                    // is intentionally not shown here.
                }
            }
        )
    }
}

suspend fun savePdfAndNotify(
    storeName: String,
    startDate: Long,
    finishDate: Long,
    transactionHistory: List<org.lelestacia.posle.domain.model.Transaction>
) {
    val bytes = org.lelestacia.posle.data.util.TransactionReportGenerator.generateAsBytes(
        storeName = storeName,
        transactionId = "REKAP-SUMMARY",
        startDate = startDate,
        finishDate = finishDate,
        transactions = transactionHistory
    ) ?: return

    val fileStorage = get<FileStorage>(FileStorage::class.java)
    val fileName = "Laporan_Transaksi_$startDate.pdf"
    val resultPath = fileStorage.saveToPublicDocuments(
        fileName = fileName,
        subFolder = "Rekap Transaksi",
        data = bytes
    )

    DesktopNotifier.notifyPdfGenerated(fileName, resultPath != null)
}
