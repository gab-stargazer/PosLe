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
import org.lelestacia.posle.data.util.PdfExportInput
import org.lelestacia.posle.domain.state_event.TransactionRecapState
import org.lelestacia.posle.navigation.PosLeComponent
import org.lelestacia.posle.navigation.RootContent
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.onSurfaceLightHighContrast
import org.lelestacia.posle.ui.theme.surfaceContainerLowestLightHighContrast
import org.lelestacia.posle.worker.AndroidRunnableService

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
                        description = "Izin dibutuhkan untuk pos notifikasi selesai",
                        isRequired = true
                    )
                )
            )

            var showPermissionRationale by remember { mutableStateOf(false) }
            var pendingState by remember { mutableStateOf<TransactionRecapState?>(null) }

            LaunchedEffect(pendingState, notificationPermission.allRequiredGranted()) {
                if (pendingState != null && notificationPermission.allRequiredGranted()) {
                    val state = pendingState!!
                    pendingState = null
                    enqueuePdfExport(runnableService, state)
                }
            }

            if (showPermissionRationale) {
                AlertDialog(
                    onDismissRequest = { showPermissionRationale = false },
                    title = { Text("Izin Notifikasi") },
                    text = {
                        Text(
                            "Aplikasi membutuhkan izin notifikasi untuk " +
                                    "memberitahu Anda ketika laporan PDF selesai dibuat. " +
                                    "Laporan akan tetap dibuat meskipun izin tidak diberikan."
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            showPermissionRationale = false
                            notificationPermission.requestPermission()
                        }) {
                            Text("Izinkan")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPermissionRationale = false }) {
                            Text("Nanti")
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
