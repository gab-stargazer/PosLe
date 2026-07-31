package org.lelestacia.posle.worker

import android.content.Context
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lelestacia.posle.data.util.PdfExportInput
import org.lelestacia.posle.data.util.TransactionReportGenerator
import org.lelestacia.posle.domain.repository.TransactionRepository
import org.lelestacia.posle.util.FileStorage
import org.lelestacia.posle.util.NotificationHelper

class PdfExportWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val transactionRepository: TransactionRepository by inject()
    private val fileStorage: FileStorage by inject()

    override suspend fun doWork(): Result {
        val inputJson = inputData.getString(INPUT_KEY) ?: return Result.failure()
        val input = Json.decodeFromString<PdfExportInput>(inputJson)
        val fileName = "Recap_${input.startDate}.pdf"
        val transactions = transactionRepository
            .getTransactionsInRange(input.startDate, input.finishDate)
            .first()

        println("PdfExportWorker: Starting work for $fileName")
        val bytes = TransactionReportGenerator.generateAsBytes(
            storeName = input.storeName,
            transactionId = "REKAP-SUMMARY",
            startDate = input.startDate,
            finishDate = input.finishDate,
            transactions = transactions
        )

        val resultUri = if (bytes != null) {
            fileStorage.saveToPublicDocuments(
                fileName = fileName,
                subFolder = "Rekap Transaksi",
                data = bytes
            )
        } else null

        return if (resultUri != null) {
            println("PdfExportWorker: Export successful, notifying user")
            NotificationHelper.notifySuccess(
                context = applicationContext,
                fileUri = resultUri.toUri(),
                fileName = fileName
            )
            Result.success()
        } else {
            println("PdfExportWorker: Export failed")
            Result.failure()
        }
    }

    companion object {
        const val INPUT_KEY = "pdf_export_input"
    }
}
