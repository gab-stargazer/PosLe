package org.lelestacia.posle.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lelestacia.posle.data.util.ProductImportInput
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.util.AppLogger
import org.lelestacia.posle.util.ExcelManager
import org.lelestacia.posle.util.NotificationHelper
import java.io.File
import java.net.URI

class ProductImportWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val productRepository: ProductRepository by inject()

    override suspend fun doWork(): Result {
        val inputJson = inputData.getString(INPUT_KEY) ?: return Result.failure()
        val input = Json.decodeFromString<ProductImportInput>(inputJson)

        val bytes = try {
            readPickedFileBytes(input.filePath)
        } catch (e: Exception) {
            AppLogger.error(TAG, "Import file could not be read: ${input.filePath}", e)
            return Result.failure()
        }

        AppLogger.info(TAG, "Starting import of ${bytes.size} bytes")
        return try {
            val products = ExcelManager.importProductsFromExcel(bytes)
            productRepository.importProducts(products)

            AppLogger.info(TAG, "Import successful, notifying user")
            NotificationHelper.notifyImportSuccess(
                context = applicationContext,
                title = input.title
            )
            Result.success()
        } catch (e: Exception) {
            AppLogger.error(TAG, "Import failed", e)
            Result.failure()
        }
    }

    /**
     * The picked file is a SAF [android.net.Uri] (content://) — it has no
     * filesystem path. Resolve it through the content resolver instead.
     * [java.io.File] is kept for unit-testability.
     */
    private fun readPickedFileBytes(filePath: String): ByteArray {
        val uri = URI.create(filePath)
        return if (uri.scheme == "content" || uri.scheme == "file") {
            applicationContext.contentResolver
                .openInputStream(android.net.Uri.parse(filePath))
                ?.use { it.readBytes() }
                ?: throw IllegalStateException("No stream for $filePath")
        } else {
            File(filePath).readBytes()
        }
    }

    companion object {
        const val INPUT_KEY = "product_import_input"
        private const val TAG = "ProductImportWorker"
    }
}
