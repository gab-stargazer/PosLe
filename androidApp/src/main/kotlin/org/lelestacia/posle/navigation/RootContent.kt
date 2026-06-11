package org.lelestacia.posle.navigation

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import org.lelestacia.posle.screen.AddTransactionScreen
import org.lelestacia.posle.screen.DashboardScreen
import org.lelestacia.posle.screen.product_add.ProductAddEditScreen

@Composable
fun RootContent(
    component: PosLeComponent,
    modifier: Modifier = Modifier
) {
    Children(
        stack = component.children,
        animation = stackAnimation(fade()),
        modifier = modifier.statusBarsPadding()
    ) {
        when(val child = it.instance) {
            is Child.Dashboard -> DashboardScreen(component = child.component)
            is Child.AddEditTransaction -> AddTransactionScreen()
            is Child.AddProduct -> ProductAddEditScreen(component = child.component)
        }
    }
}
