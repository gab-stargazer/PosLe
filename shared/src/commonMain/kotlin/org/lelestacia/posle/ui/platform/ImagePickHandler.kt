package org.lelestacia.posle.ui.platform

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope

/**
 * Platform image picker abstraction.
 *
 * - Android: backed by FileKit, persists the picked URI read permission.
 * - Desktop (JVM): no-op (the UI is phone-only on desktop).
 */
interface ImagePickHandler {
    /** Opens the platform's image picker. */
    fun launchImagePicker()
}

/**
 * Creates an [ImagePickHandler] for the current platform.
 */
@Composable
expect fun rememberImagePickHandler(
    scope: CoroutineScope,
    onImagePicked: (uri: String, bytes: ByteArray) -> Unit,
): ImagePickHandler
