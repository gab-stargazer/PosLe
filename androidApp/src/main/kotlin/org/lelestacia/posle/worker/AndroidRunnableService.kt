package org.lelestacia.posle.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlin.reflect.KClass
import org.lelestacia.posle.util.RunnableService

class AndroidRunnableService(
    private val context: Context
) : RunnableService {

    override fun enqueue(id: String, serializedData: String) {
        enqueue(id, serializedData, PdfExportWorker::class, PdfExportWorker.INPUT_KEY)
    }

    fun enqueue(
        id: String,
        serializedData: String,
        workerClass: KClass<out CoroutineWorker>,
        inputKey: String
    ) {
        val workRequest = OneTimeWorkRequest.Builder(
            workerClass.java
        )
            .setInputData(workDataOf(inputKey to serializedData))
            .addTag(id)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(id, ExistingWorkPolicy.REPLACE, workRequest)
    }
}
