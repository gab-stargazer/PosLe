package org.lelestacia.posle.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.util.Name

/**
 * Desktop (JVM) implementation of [ImagePickHandler]. The UI is phone-only
 * on desktop, so image picking is a no-op.
 */
@Composable
actual fun rememberImagePickHandler(
    scope: CoroutineScope,
    onImagePicked: (uri: String, bytes: ByteArray) -> Unit,
): ImagePickHandler {
    return remember(scope, onImagePicked) {
        object : ImagePickHandler {
            override fun launchImagePicker() {
                // No-op on desktop.
            }
        }
    }
}

@Composable
internal actual fun platformBluetoothPermissionGranted(): Boolean = false

internal actual fun platformPrintTransaction(
    transaction: Transaction,
    storeNameValue: Name,
) {
    // No-op on desktop: no Bluetooth printer available.
}

@Composable
internal actual fun platformQrScanner(
    onScanned: (String) -> Unit,
) {
    // No-op on desktop: no camera available.
}
