package org.lelestacia.posle.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo

actual fun ImageBitmap.encodeToPngBytes(): ByteArray {
    val pixelMap = toPixelMap()
    val width = pixelMap.width
    val height = pixelMap.height

    // PixelMap.buffer stores ARGB ints; Skia RGBA_8888 expects RGBA bytes.
    val rgba = ByteArray(width * height * 4)
    pixelMap.buffer.forEachIndexed { index, argb ->
        val offset = index * 4
        rgba[offset] = (argb shr 16 and 0xFF).toByte()       // R
        rgba[offset + 1] = (argb shr 8 and 0xFF).toByte()    // G
        rgba[offset + 2] = (argb and 0xFF).toByte()          // B
        rgba[offset + 3] = (argb shr 24 and 0xFF).toByte()   // A
    }

    val bitmap = Bitmap()
    bitmap.installPixels(
        info = ImageInfo(
            width = width,
            height = height,
            colorType = ColorType.RGBA_8888,
            alphaType = ColorAlphaType.PREMUL
        ),
        pixels = rgba,
        rowBytes = width * 4
    )

    val image = Image.makeFromBitmap(bitmap)
    return image.encodeToData(EncodedImageFormat.PNG)?.bytes ?: ByteArray(0)
}
