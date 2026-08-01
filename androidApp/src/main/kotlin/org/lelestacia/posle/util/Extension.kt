package org.lelestacia.posle.util

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.toAndroidUri
import io.github.vinceglb.filekit.readBytes
import kotlin.Unit

private const val IMAGE_PICK_TAG = "ImagePick"

suspend fun Context.handleImagePick(file: PlatformFile?, onPicked: (uri: String, bytes: ByteArray) -> Unit) {
    file?.let { pickedFile ->
        val uri = pickedFile.toAndroidUri()
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (securityException: SecurityException) {
                Log.w(IMAGE_PICK_TAG, "Failed to persist URI read permission", securityException)
            }
        }
        onPicked(uri.toString(), pickedFile.readBytes())
    }
}