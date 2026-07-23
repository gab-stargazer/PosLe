package org.lelestacia.posle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.toArgb
import com.arkivanov.decompose.retainedComponent
import org.lelestacia.posle.navigation.PosLeComponent
import org.lelestacia.posle.navigation.RootContent
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.onSurfaceLightHighContrast
import org.lelestacia.posle.ui.theme.surfaceContainerLowestLightHighContrast

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = surfaceContainerLowestLightHighContrast.toArgb(),
                darkScrim = onSurfaceLightHighContrast.toArgb()
            )
        )
        super.onCreate(savedInstanceState)
        val rootComponent = retainedComponent { context -> PosLeComponent(componentContext = context) }
        setContent {
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