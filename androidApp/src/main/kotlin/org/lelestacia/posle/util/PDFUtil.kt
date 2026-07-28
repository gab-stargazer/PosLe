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
}

data class PdfTarget(val uri: Uri, val outputStream: OutputStream)

fun createPdfOutputStream(context: Context, displayName: String): PdfTarget? {
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

fun finalizePendingFile(context: Context, uri: Uri, success: Boolean) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return
    if (success) {
        val values = ContentValues().apply { put(MediaStore.MediaColumns.IS_PENDING, 0) }
        context.contentResolver.update(uri, values, null, null)
    } else {
        context.contentResolver.delete(uri, null, null) // don't leave a broken pending row behind
    }
}