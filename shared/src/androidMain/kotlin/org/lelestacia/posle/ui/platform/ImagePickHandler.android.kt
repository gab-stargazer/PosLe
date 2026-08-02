package org.lelestacia.posle.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.dialogs.toAndroidUri
import android.content.ContentResolver
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val IMAGE_PICK_TAG = "ImagePick"

/**
 * Android implementation of [ImagePickHandler] backed by FileKit. Persists
 * the URI read permission so the image can be loaded later.
 */
@Composable
actual fun rememberImagePickHandler(
    scope: CoroutineScope,
    onImagePicked: (uri: String, bytes: ByteArray) -> Unit,
): ImagePickHandler {
    val context = LocalContext.current
    val launcher = rememberFilePickerLauncher(
        type = FileKitType.Image
    ) { file ->
        scope.launch {
            handleImagePick(
                context = context,
                file = file,
                onPicked = onImagePicked
            )
        }
    }

    return remember(launcher, onImagePicked) {
        object : ImagePickHandler {
            override fun launchImagePicker() {
                launcher.launch()
            }
        }
    }
}

private suspend fun handleImagePick(
    context: android.content.Context,
    file: PlatformFile?,
    onPicked: (uri: String, bytes: ByteArray) -> Unit,
) {
    file?.let { pickedFile ->
        val uri = pickedFile.toAndroidUri()
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            try {
                context.contentResolver.takePersistableUriPermission(
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
