package org.lelestacia.posle.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.util.DesktopNotifier
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.notification_import_success

@Composable
actual fun rememberImportNotifier(): () -> Unit {
    val successTitle = stringResource(Res.string.notification_import_success)
    return remember(successTitle) {
        { DesktopNotifier.notifyImportFinished(successTitle) }
    }
}
