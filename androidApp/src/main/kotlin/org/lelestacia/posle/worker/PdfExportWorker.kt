package org.lelestacia.posle.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lelestacia.posle.data.util.PdfExportInput
import org.lelestacia.posle.data.util.TransactionReportGenerator
import org.lelestacia.posle.domain.repository.TransactionRepository
import org.lelestacia.posle.util.NotificationHelper
import org.lelestacia.posle.util.PDFUtil

class PdfExportWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val transactionRepository: TransactionRepository by inject()

    override suspend fun doWork(): Result {
        val inputJson = inputData.getString(INPUT_KEY) ?: return Result.failure()
        val input = Json.decodeFromString<PdfExportInput>(inputJson)
        val fileName = "Recap_${input.startDate}.pdf"
        val transactions = transactionRepository
            .getTransactionsInRange(input.startDate, input.finishDate)
            .first()

        println("PdfExportWorker: Starting work for $fileName")
        val resultUri = PDFUtil.exportToPublicDocuments(applicationContext, fileName) { os ->
            TransactionReportGenerator.generate(
                outputStream = os,
                storeName = input.storeName,
                transactionId = "REKAP-SUMMARY",
                startDate = input.startDate,
                finishDate = input.finishDate,
                transactions = transactions
            )
        }

        return if (resultUri != null) {
            println("PdfExportWorker: Export successful, notifying user")
            NotificationHelper.notifySuccess(
                context = applicationContext,
                fileUri = resultUri,
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
