package org.lelestacia.posle

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.lelestacia.posle.util.DesktopNotifier
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

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
                // Desktop content
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
    val chooser = JFileChooser().apply {
        dialogTitle = "Simpan Laporan Transaksi"
        selectedFile = File("Laporan_Transaksi_$startDate.pdf")
        fileFilter = FileNameExtensionFilter("PDF files (*.pdf)", "pdf")
    }

    val result = withContext(Dispatchers.IO) {
        chooser.showSaveDialog(null)
    }

    if (result != JFileChooser.APPROVE_OPTION) return

    val file = chooser.selectedFile
    val fileName = file.name

    val success = withContext(Dispatchers.IO) {
        try {
            file.outputStream().use { os ->
                org.lelestacia.posle.data.util.TransactionReportGenerator.generate(
                    outputStream = os,
                    storeName = storeName,
                    transactionId = "REKAP-SUMMARY",
                    startDate = startDate,
                    finishDate = finishDate,
                    transactions = transactionHistory
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    DesktopNotifier.notifyPdfGenerated(fileName, success)
}
