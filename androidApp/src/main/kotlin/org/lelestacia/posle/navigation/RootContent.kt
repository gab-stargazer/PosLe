package org.lelestacia.posle.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import org.lelestacia.posle.screen.DashboardScreen
import org.lelestacia.posle.screen.QrScannerScreen
import org.lelestacia.posle.screen.bundle_add_edit.BundleAddEditScreen
import org.lelestacia.posle.screen.product_add.ProductAddEditScreen
import org.lelestacia.posle.screen.product_add.ProductAddVariantsViewScreen
import org.lelestacia.posle.screen.transaction_add.TransactionProductConfigScreen
import org.lelestacia.posle.screen.transaction_recap_product_view.TransactionRecapProductDetailScreen
import org.lelestacia.posle.screen.transaction_view.TransactionViewScreen

@Composable
fun RootContent(
    component: PosLeComponent,
    modifier: Modifier = Modifier
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(component.snackbarHostState)
        },
        contentWindowInsets = WindowInsets(),
        modifier = modifier
            .statusBarsPadding()
    ) { paddingValues ->
        Children(
            stack = component.children,
            animation = stackAnimation(fade()),
            modifier = modifier
                .statusBarsPadding()
                .padding(paddingValues = paddingValues)
        ) {
            when (val child = it.instance) {
                is Child.Dashboard -> {
                    DashboardScreen(component = child.component)
                }

                //  Transaction
                is Child.TransactionList -> {

                }



                is Child.TransactionView -> {
                    TransactionViewScreen(component = child.component)
                }
                is Child.TransactionProductConfig -> {
                    TransactionProductConfigScreen(component = child.component)
                }

                is Child.ProductAddEdit -> {
                    ProductAddEditScreen(component = child.component)
                }
                is Child.VariantView -> {
                    ProductAddVariantsViewScreen(component = child.component)
                }

                is Child.QrScanner -> {
                    QrScannerScreen(component = child.component)
                }

                is Child.BundleAddEdit -> {
                    BundleAddEditScreen(component = child.component)
                }

                is Child.TransactionRecapProductView -> {
                    TransactionRecapProductDetailScreen(component = child.component)
                }
            }
        }
    }
}
