package org.lelestacia.posle.worker

import android.content.Context
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lelestacia.posle.data.util.ProductExportInput
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.util.AppLogger
import org.lelestacia.posle.util.ExcelManager
import org.lelestacia.posle.util.FileStorage
import org.lelestacia.posle.util.NotificationHelper

class ProductExportWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val productRepository: ProductRepository by inject()
    private val fileStorage: FileStorage by inject()

    override suspend fun doWork(): Result {
        val inputJson = inputData.getString(INPUT_KEY) ?: return Result.failure()
        val input = Json.decodeFromString<ProductExportInput>(inputJson)
        val fileName = "products.xlsx"

        AppLogger.info(TAG, "Starting work for $fileName")
        val products = productRepository.getAllProducts().first()
        val bytes = ExcelManager.exportProductsToExcel(products)

        val resultUri = fileStorage.saveToPublicDocuments(
            fileName = fileName,
            subFolder = "Daftar Produk",
            data = bytes
        )

        return if (resultUri != null) {
            AppLogger.info(TAG, "Export successful, notifying user")
            NotificationHelper.notifySuccess(
                context = applicationContext,
                fileUri = resultUri.toUri(),
                fileName = fileName,
                title = input.title
            )
            Result.success()
        } else {
            AppLogger.error(TAG, "Export failed")
            Result.failure()
        }
    }

    companion object {
        const val INPUT_KEY = "product_export_input"
        private const val TAG = "ProductExportWorker"
    }
}
