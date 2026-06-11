package org.lelestacia.posle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.retainedComponent
import org.lelestacia.posle.navigation.PosLeComponent
import org.lelestacia.posle.navigation.RootContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val rootComponent = retainedComponent { PosLeComponent(componentContext = defaultComponentContext()) }
        setContent {
            App(
                content = {
                    RootContent(rootComponent)
                }
            )
        }
    }
}