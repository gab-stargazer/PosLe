package org.lelestacia.posle

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.arkivanov.decompose.retainedComponent
import com.meticha.permissions_compose.AppPermission
import com.meticha.permissions_compose.rememberAppPermissionState
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.data.util.PdfExportInput
import org.lelestacia.posle.data.util.ProductExportInput
import org.lelestacia.posle.data.util.ProductImportInput
import org.lelestacia.posle.domain.state_event.TransactionRecapState
import org.lelestacia.posle.navigation.PosLeComponent
import org.lelestacia.posle.navigation.RootContent
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.onSurfaceLightHighContrast
import org.lelestacia.posle.ui.theme.surfaceContainerLowestLightHighContrast
import org.lelestacia.posle.worker.AndroidRunnableService
import org.lelestacia.posle.worker.PdfExportWorker
import org.lelestacia.posle.worker.ProductExportWorker
import org.lelestacia.posle.worker.ProductImportWorker
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_allow
import posle.shared.generated.resources.btn_later
import posle.shared.generated.resources.dialog_notification_permission_desc
import posle.shared.generated.resources.dialog_notification_permission_title
import posle.shared.generated.resources.notification_export_success
import posle.shared.generated.resources.notification_import_success

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = surfaceContainerLowestLightHighContrast.toArgb(),
                darkScrim = onSurfaceLightHighContrast.toArgb()
            )
        )
        super.onCreate(savedInstanceState)
        setContent {
            val androidContext = LocalContext.current

            val runnableService = remember {
                AndroidRunnableService(androidContext)
            }

            val notificationPermission = rememberAppPermissionState(
                listOf(
                    AppPermission(
                        permission = Manifest.permission.POST_NOTIFICATIONS,
                        description = "Izin dibutuhkan untuk menampilkan notifikasi saat laporan selesai",
                        isRequired = true
                    )
                )
            )

            var showPermissionRationale by remember { mutableStateOf(false) }
            var pendingState by remember { mutableStateOf<TransactionRecapState?>(null) }
            var pendingExport by remember { mutableStateOf(false) }
            var pendingImportPath by remember { mutableStateOf<String?>(null) }

            val exportSuccessTitle = stringResource(Res.string.notification_export_success)
            val importSuccessTitle = stringResource(Res.string.notification_import_success)

            LaunchedEffect(
                pendingState,
                pendingExport,
                pendingImportPath,
                notificationPermission.allRequiredGranted()
            ) {
                if (notificationPermission.allRequiredGranted()) {
                    pendingState?.let {
                        pendingState = null
                        enqueuePdfExport(runnableService, it)
                    }
                    if (pendingExport) {
                        pendingExport = false
                        enqueueProductExport(runnableService, exportSuccessTitle)
                    }
                    pendingImportPath?.let {
                        pendingImportPath = null
                        enqueueProductImport(runnableService, it, importSuccessTitle)
                    }
                }
            }

            if (showPermissionRationale) {
                AlertDialog(
                    onDismissRequest = { showPermissionRationale = false },
                    title = { Text(stringResource(Res.string.dialog_notification_permission_title)) },
                    text = {
                        Text(stringResource(Res.string.dialog_notification_permission_desc))
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            showPermissionRationale = false
                            notificationPermission.requestPermission()
                        }) {
                            Text(stringResource(Res.string.btn_allow))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPermissionRationale = false }) {
                            Text(stringResource(Res.string.btn_later))
                        }
                    }
                )
            }

            val rootComponent = remember {
                retainedComponent { context ->
                    PosLeComponent(
                        componentContext = context,
                        onPrintRecap = { state ->
                            if (notificationPermission.allRequiredGranted() || Build.VERSION.SDK_INT <= 32) {
                                enqueuePdfExport(runnableService, state)
                            } else {
                                pendingState = state
                                showPermissionRationale = true
                            }
                        },
                        onExportProducts = {
                            if (notificationPermission.allRequiredGranted() || Build.VERSION.SDK_INT <= 32) {
                                enqueueProductExport(runnableService, exportSuccessTitle)
                            } else {
                                pendingExport = true
                                showPermissionRationale = true
                            }
                        },
                        onImportProducts = { filePath ->
                            if (notificationPermission.allRequiredGranted() || Build.VERSION.SDK_INT <= 32) {
                                enqueueProductImport(runnableService, filePath, importSuccessTitle)
                            } else {
                                pendingImportPath = filePath
                                showPermissionRationale = true
                            }
                        },
                    )
                }
            }

            AppTheme(
                darkTheme = false,
                content = {
                    Surface {
                        RootContent(rootComponent)
                    }
                }
            )
        }
    }
}

private fun enqueuePdfExport(
    runnableService: AndroidRunnableService,
    state: TransactionRecapState
) {
    val input = PdfExportInput(
        storeName = state.settings.storeName.value,
        startDate = state.startDate,
        finishDate = state.finishDate
    )
    val json = Json.encodeToString(input)
    runnableService.enqueue("pdf_export_${state.startDate}", json)
}

private fun enqueueProductExport(
    runnableService: AndroidRunnableService,
    title: String = "Data Produk berhasil diekspor"
) {
    val input = ProductExportInput(title = title)
    val json = Json.encodeToString(input)
    runnableService.enqueue(
        id = "product_export",
        serializedData = json,
        workerClass = ProductExportWorker::class,
        inputKey = ProductExportWorker.INPUT_KEY
    )
}

private fun enqueueProductImport(
    runnableService: AndroidRunnableService,
    filePath: String,
    title: String = "Data Produk berhasil diimpor"
) {
    val input = ProductImportInput(filePath = filePath, title = title)
    val json = Json.encodeToString(input)
    runnableService.enqueue(
        id = "product_import",
        serializedData = json,
        workerClass = ProductImportWorker::class,
        inputKey = ProductImportWorker.INPUT_KEY
    )
}
