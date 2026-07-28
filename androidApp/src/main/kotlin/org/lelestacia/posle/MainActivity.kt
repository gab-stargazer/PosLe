package org.lelestacia.posle

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.arkivanov.decompose.retainedComponent
import com.meticha.permissions_compose.AppPermission
import com.meticha.permissions_compose.rememberAppPermissionState
import kotlinx.coroutines.launch
import org.lelestacia.posle.data.util.TransactionReportGenerator
import org.lelestacia.posle.navigation.PosLeComponent
import org.lelestacia.posle.navigation.RootContent
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.onSurfaceLightHighContrast
import org.lelestacia.posle.ui.theme.surfaceContainerLowestLightHighContrast
import org.lelestacia.posle.util.createPdfOutputStream
import org.lelestacia.posle.util.finalizePendingFile

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
            val scope = rememberCoroutineScope()
            val androidContext = LocalContext.current
            val notificationPermission = rememberAppPermissionState(
                listOf(
                    AppPermission(
                        permission = Manifest.permission.POST_NOTIFICATIONS,
                        description = "Izin dibutuhkan untuk pos notifikasi selesai",
                        isRequired = true
                    )
                )
            )

            val rootComponent = remember {
                retainedComponent { context ->
                    PosLeComponent(
                        componentContext = context,
                        onPrintRecap = { state ->

                            if (notificationPermission.allRequiredGranted() || Build.VERSION.SDK_INT <= 32) {
                                scope.launch {
                                    val target = createPdfOutputStream(
                                        androidContext,
                                        "Recap_${state.startDate}.pdf"
                                    ) ?: // couldn't even create the MediaStore entry
                                    return@launch

                                    val success = target.outputStream.use { os ->
                                        TransactionReportGenerator.generate(
                                            outputStream = os,
                                            storeName = state.settings.storeName.value,
                                            transactionId = "REKAP-SUMMARY",
                                            startDate = state.startDate,
                                            finishDate = state.finishDate,
                                            transactions = state.transactionHistory
                                        )
                                    }

                                    finalizePendingFile(androidContext, target.uri, success)
                                }
                            } else {
                                notificationPermission.requestPermission()
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