package org.lelestacia.posle.util

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
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

    actual fun saveToPublicPictures(fileName: String, data: ByteArray): String? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/PosLe")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: return null

        return try {
            resolver.openOutputStream(uri)?.use { it.write(data) }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }
            uri.toString()
        } catch (e: Exception) {
            resolver.delete(uri, null, null)
            null
        }
    }

    actual fun saveToPublicDocuments(
        fileName: String,
        subFolder: String,
        data: ByteArray
    ): String? {
        val mimeType = when {
            fileName.endsWith(".pdf") -> "application/pdf"
            fileName.endsWith(".xlsx") -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            else -> "*/*"
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents/PosLe/$subFolder")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Files.getContentUri("external")
            }

        val uri = resolver.insert(collection, contentValues) ?: return null

        return try {
            resolver.openOutputStream(uri)?.use { it.write(data) }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }
            uri.toString()
        } catch (e: Exception) {
            AppLogger.error(TAG, "Failed to save file '$fileName' to public documents", e)
            resolver.delete(uri, null, null)
            null
        }
    }

    private companion object {
        const val TAG = "FileStorage"
    }
}
