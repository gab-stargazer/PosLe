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

/**
 * Component interface for managing stock Inbound (Purchases/Returns) and Outbound (Sales/Adjustments).
 */
interface ProductInboundOutboundComponent {
    /**
     * Paginated flow of all stock movements.
     */
    val priceMovement: Flow<PagingData<StockMovement>>

    /**
     * Paginated flow of all products for selection.
     */
    val products: Flow<PagingData<Product>>

    /**
     * Current state of the Inbound/Outbound screen.
     */
    val state: StateFlow<ProductInboundOutboundComponentState>

    /**
     * Processes stock movement events.
     */
    fun onEvent(event: ProductInboundOutboundComponentEvent)
}

/**
 * UI State for the Inbound/Outbound screen.
 */
data class ProductInboundOutboundComponentState(
    val selectedTab: Int = 0,
    val isAddStockShown: Boolean = false,
    val addMovementState: ProductInboundOutboundAddStockState = ProductInboundOutboundAddStockState(),
    val settingState: PosLeSettings = PosLeSettings()
) {

    /**
     * State for the dialog used to record a new stock movement.
     */
    @Immutable
    data class ProductInboundOutboundAddStockState(
        val availableProducts: List<Product> = emptyList(),
        val selectedProduct: Product? = null,
        val productName: Name = Name(""),
        val selectedMovementType: StockMovementType = StockMovementType.Purchase,
        val amountAdded: TextFieldState = TextFieldState(),
    )
}

/**
 * Events for managing stock movements.
 */
sealed interface ProductInboundOutboundComponentEvent {

    /**
     * Switch between movement list and selection tabs.
     */
    data class OnTabSelected(val index: Int) : ProductInboundOutboundComponentEvent

    /**
     * Events specifically for adding a new stock movement.
     */
    sealed interface ProductInboundOutboundAddStockEvent : ProductInboundOutboundComponentEvent {
        /**
         * Toggles the visibility of the "Add Movement" dialog.
         */
        data object OnToggleDialog : ProductInboundOutboundAddStockEvent

        /**
         * Filters available products by name.
         */
        data class OnProductNameChanged(val newProductName: Name) :
            ProductInboundOutboundAddStockEvent

        /**
         * Sets the product for the movement.
         */
        data class OnProductSelected(val product: Product) : ProductInboundOutboundAddStockEvent

        /**
         * Changes the type of movement (Purchase, Sale, etc.).
         */
        data class OnMovementTypeChanged(val movementType: StockMovementType): ProductInboundOutboundAddStockEvent

        /**
         * Persists the movement to the database.
         */
        data object OnAddMovementClicked : ProductInboundOutboundAddStockEvent
    }
}
