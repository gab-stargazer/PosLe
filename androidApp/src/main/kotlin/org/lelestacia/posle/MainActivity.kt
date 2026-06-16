package org.lelestacia.posle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import com.arkivanov.decompose.retainedComponent
import org.lelestacia.posle.navigation.PosLeComponent
import org.lelestacia.posle.navigation.RootContent
import org.lelestacia.posle.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT
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