package org.lelestacia.posle.util

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.ByteArrayOutputStream

actual fun ImageBitmap.encodeToPngBytes(): ByteArray {
    val bitmap = asAndroidBitmap()
    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
    return bos.toByteArray()
}
