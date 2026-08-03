package org.lelestacia.posle.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.lelestacia.posle.domain.component.SettingComponent
import org.lelestacia.posle.domain.component.TransactionHistoryComponent
import org.lelestacia.posle.domain.component.analytics.AnalyticsComponent
import org.lelestacia.posle.domain.component.product_inbound_outbound.ProductInboundOutboundComponent
import org.lelestacia.posle.domain.component.product_list.ProductListComponent
import org.lelestacia.posle.domain.component.transaction_add.TransactionAddComponent
import org.lelestacia.posle.domain.component.transaction_recap.TransactionRecapComponent
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.destination_analytics
import posle.shared.generated.resources.destination_list_inbound_outbound
import posle.shared.generated.resources.destination_list_product
import posle.shared.generated.resources.destination_settings
import posle.shared.generated.resources.destination_transaction_add
import posle.shared.generated.resources.destination_transaction_recap

enum class NavDestination(
    val config: NavConfig,
    val icon: ImageVector,
    val title: StringResource
) {
    Transaction(
        NavConfig.TransactionAdd,
        Icons.Default.ShoppingCart,
        Res.string.destination_transaction_add
    ),
//    TransactionHistory(
//        NavConfig.TransactionHistory,
//        Icons.Default.History,
//        Res.string.destination_transaction_history
//    ),
    TransactionRecap(
        NavConfig.TransactionRecap,
        Icons.Default.History,
        Res.string.destination_transaction_recap
    ),
    Analytics(
        NavConfig.Analytics,
        Icons.Default.Insights,
        Res.string.destination_analytics
    ),
    ProductList(
        NavConfig.ProductList,
        Icons.AutoMirrored.Filled.List,
        Res.string.destination_list_product
    ),
    ProductInboundOutbound(
        NavConfig.ProductInboundOutbound,
        Icons.Default.Inventory,
        Res.string.destination_list_inbound_outbound
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
    data object TransactionAdd : NavConfig

    @Serializable
    data object TransactionHistory : NavConfig

    @Serializable
    data object TransactionRecap : NavConfig

    @Serializable
    data object Analytics : NavConfig

    @Serializable
    data object ProductList : NavConfig

    @Serializable
    data object ProductInboundOutbound : NavConfig

    @Serializable
    data object Setting : NavConfig
}

sealed class NavChild {
    data class TransactionAdd(val component: TransactionAddComponent) : NavChild()
    data class TransactionHistory(val component: TransactionHistoryComponent) : NavChild()
    data class TransactionRecap(val component: TransactionRecapComponent) : NavChild()
    data class Analytics(val component: AnalyticsComponent) : NavChild()
    data class ProductList(val component: ProductListComponent) : NavChild()
    data class ProductInboundOutbound(val component: ProductInboundOutboundComponent) : NavChild()
    data class Setting(val component: SettingComponent) : NavChild()
}

