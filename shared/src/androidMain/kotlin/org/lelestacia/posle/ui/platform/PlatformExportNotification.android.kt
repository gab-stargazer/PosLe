package org.lelestacia.posle.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.util.NotificationHelper
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.notification_export_success

@Composable
actual fun rememberExportNotifier(): (fileName: String, savedPath: String?) -> Unit {
    val context = LocalContext.current
    val successTitle = stringResource(Res.string.notification_export_success)
    return remember(context, successTitle) {
        { fileName: String, savedPath: String? ->
            if (savedPath != null) {
                NotificationHelper.notifySuccess(
                    context = context,
                    fileUri = savedPath.toUri(),
                    fileName = fileName,
                    title = successTitle
                )
            }
        }
    }
}
