package org.lelestacia.posle.util

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Encodes an [ImageBitmap] to PNG bytes.
 *
 * Implementation differs per platform:
 * - Android: android.graphics.Bitmap compress
 * - JVM (desktop): Skia encode
 */
expect fun ImageBitmap.encodeToPngBytes(): ByteArray
