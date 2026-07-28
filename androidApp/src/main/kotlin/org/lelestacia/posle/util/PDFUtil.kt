package org.lelestacia.posle.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.OutputStream

object PDFUtil {

    fun openPDF(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Buka Laporan PDF"))
    }

    /**
     * Handles the entire lifecycle of exporting a PDF to the public Documents folder.
     * 1. Creates a pending entry in MediaStore.
     * 2. Opens an OutputStream for the action to write into.
     * 3. Finalizes the file (makes it visible) on success, or cleans up on failure.
     *
     * @param context Android Context.
     * @param displayName The name of the file to be created.
     * @param action A suspend function that receives an [OutputStream] and returns true if writing was successful.
     * @return The [Uri] of the created file if successful, null otherwise.
     */
    suspend fun exportToPublicDocuments(
        context: Context,
        displayName: String,
        action: suspend (OutputStream) -> Boolean
    ): Uri? {
        println("PDFUtil: Initializing export for $displayName")
        val target = createPdfOutputStream(context, displayName) ?: run {
            println("PDFUtil: Failed to create PDF OutputStream")
            return null
        }

        var success = false
        try {
            success = target.outputStream.use { os ->
                println("PDFUtil: Writing content to OutputStream...")
                action(os)
            }
            return if (success) target.uri else null
        } catch (e: Exception) {
            println("PDFUtil: Exception during PDF generation: ${e.message}")
            e.printStackTrace()
            return null
        } finally {
            println("PDFUtil: Finalizing pending file (success=$success)")
            finalizePendingFile(context, target.uri, success)
        }
    }

    private data class PdfTarget(val uri: Uri, val outputStream: OutputStream)

    private fun createPdfOutputStream(context: Context, displayName: String): PdfTarget? {
        val resolver = context.contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Files.getContentUri("external")
        }

        val uri = resolver.insert(collection, contentValues) ?: return null
        val outputStream = resolver.openOutputStream(uri) ?: run {
            resolver.delete(uri, null, null) // clean up the row if stream can't be opened
            return null
        }

        return PdfTarget(uri, outputStream)
    }

    private fun finalizePendingFile(context: Context, uri: Uri, success: Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return
        if (success) {
            val values = ContentValues().apply { put(MediaStore.MediaColumns.IS_PENDING, 0) }
            context.contentResolver.update(uri, values, null, null)
        } else {
            context.contentResolver.delete(uri, null, null) // don't leave a broken pending row behind
        }
    }
}
