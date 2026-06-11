package org.lelestacia.posle.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.lelestacia.posle.domain.component.ProductListComponent
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.destination_list_product
import posle.shared.generated.resources.destination_settings
import posle.shared.generated.resources.destination_transaction_history

enum class NavDestination(
    val config: NavConfig,
    val icon: ImageVector,
    val title: StringResource
) {
    Transaction(
        NavConfig.Transaction,
        Icons.Default.History,
        Res.string.destination_transaction_history
    ),
    ProductList(
        NavConfig.ProductList,
        Icons.AutoMirrored.Filled.List,
        Res.string.destination_list_product
    ),
    Setting(
        NavConfig.Setting,
        Icons.Default.Settings,
        Res.string.destination_settings
    )
}

@Serializable
sealed interface NavConfig {

    @Serializable
    data object Transaction : NavConfig

    @Serializable
    data object ProductList : NavConfig

    @Serializable
    data object Setting : NavConfig
}

sealed class NavChild {
    data object Transaction : NavChild()
    data class ProductList(val component: ProductListComponent) : NavChild()
    data object Setting : NavChild()
}

