package org.lelestacia.posle.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import org.lelestacia.posle.util.RunnableService

class AndroidRunnableService(
    private val context: Context
) : RunnableService {

    override fun enqueue(id: String, serializedData: String) {
        val workRequest = OneTimeWorkRequestBuilder<PdfExportWorker>()
            .setInputData(workDataOf(PdfExportWorker.INPUT_KEY to serializedData))
            .addTag(id)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(id, ExistingWorkPolicy.REPLACE, workRequest)
    }
}
