package org.lelestacia.posle.util

import android.content.Context
import android.os.Environment
import java.io.File

actual class FileStorage(
    private val context: Context
) {
    actual fun saveImage(fileName: String, data: ByteArray): String {
        val directory = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "products"
        ).apply {
            if (!exists()) mkdirs()
        }

        val file = File(directory, fileName)
        file.writeBytes(data)

        return file.absolutePath
    }

    actual fun deleteImage(fileName: String): Boolean {
        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "products/$fileName"
        )
        return if (file.exists()) file.delete() else false
    }
}