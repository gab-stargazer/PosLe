package org.lelestacia.posle.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.util.DesktopNotifier
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.notification_export_success

@Composable
actual fun rememberExportNotifier(): (fileName: String, savedPath: String?) -> Unit {
    val successTitle = stringResource(Res.string.notification_export_success)
    return remember(successTitle) {
        { fileName: String, savedPath: String? ->
            DesktopNotifier.notifyExportFinished(
                fileName = fileName,
                success = savedPath != null,
                successTitle = successTitle
            )
        }
    }
}
