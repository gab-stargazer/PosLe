package org.lelestacia.posle.domain.component.product_inbound_outbound

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.data.entity.StockMovementType
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.StockMovement
import org.lelestacia.posle.util.Name

interface ProductInboundOutboundComponent {
    val priceMovement: Flow<PagingData<StockMovement>>
    val products: Flow<PagingData<Product>>
    val state: StateFlow<ProductInboundOutboundComponentState>
    fun onEvent(event: ProductInboundOutboundComponentEvent)
}

data class ProductInboundOutboundComponentState(
    val selectedTab: Int = 0,
    val isAddStockShown: Boolean = false,
    val addMovementState: ProductInboundOutboundAddStockState = ProductInboundOutboundAddStockState(),
    val settingState: PosLeSettings = PosLeSettings()
) {

    @Immutable
    data class ProductInboundOutboundAddStockState(
        val availableProducts: List<Product> = emptyList(),
        val selectedProduct: Product? = null,
        val productName: Name = Name(""),
        val selectedMovementType: StockMovementType = StockMovementType.Purchase,
        val amountAdded: TextFieldState = TextFieldState(),
        val buyPrice: TextFieldState = TextFieldState(),
    )
}

sealed interface ProductInboundOutboundComponentEvent {

    data class OnTabSelected(val index: Int) : ProductInboundOutboundComponentEvent

    sealed interface ProductInboundOutboundAddStockEvent : ProductInboundOutboundComponentEvent {
        data object OnToggleDialog : ProductInboundOutboundAddStockEvent

        data class OnProductNameChanged(val newProductName: Name) :
            ProductInboundOutboundAddStockEvent
        data class OnProductSelected(val product: Product) : ProductInboundOutboundAddStockEvent
        data class OnMovementTypeChanged(val movementType: StockMovementType): ProductInboundOutboundAddStockEvent
        data object OnAddMovementClicked : ProductInboundOutboundAddStockEvent
    }
}
