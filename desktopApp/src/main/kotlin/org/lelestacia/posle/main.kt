package org.lelestacia.posle

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "PosLe",
    ) {
        App()
    }
}