package org.lelestacia.posle.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.serialization.json.Json
import org.lelestacia.posle.data.util.PdfExportInput
import org.lelestacia.posle.data.util.TransactionReportGenerator
import org.lelestacia.posle.util.NotificationHelper
import org.lelestacia.posle.util.createPdfOutputStream
import org.lelestacia.posle.util.finalizePendingFile

class PdfExportWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val inputJson = inputData.getString(INPUT_KEY) ?: return Result.failure()
        val input = Json.decodeFromString<PdfExportInput>(inputJson)

        return try {
            val fileName = "Recap_${input.startDate}.pdf"
            val target = createPdfOutputStream(applicationContext, fileName)
                ?: return Result.failure()

            val success = target.outputStream.use { os ->
                TransactionReportGenerator.generate(
                    outputStream = os,
                    storeName = input.storeName,
                    transactionId = "REKAP-SUMMARY",
                    startDate = input.startDate,
                    finishDate = input.finishDate,
                    transactions = input.transactions
                )
            }

            finalizePendingFile(applicationContext, target.uri, success)

            if (success) {
                NotificationHelper.notifySuccess(
                    context = applicationContext,
                    fileUri = target.uri,
                    fileName = fileName
                )
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    companion object {
        const val INPUT_KEY = "pdf_export_input"
    }
}
