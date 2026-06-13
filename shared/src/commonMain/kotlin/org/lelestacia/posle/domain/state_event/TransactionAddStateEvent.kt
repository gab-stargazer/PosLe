package org.lelestacia.posle.domain.state_event

import androidx.compose.foundation.text.input.TextFieldState
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.domain.model.Product

data class TransactionAddState(
    val searchQuery: TextFieldState = TextFieldState(),
    val products: Map<Product, TransactionItemState> = mapOf(),

    val customerName: TextFieldState = TextFieldState(),
    val settings: PosLeSettings = PosLeSettings()
)

sealed interface TransactionAddEvent {
    data class OnAddNewProduct(val product: Product): TransactionAddEvent
    data class OnRemoveProduct(val product: Product): TransactionAddEvent
    data class OnAmountChanged(val product: Product, val newAmount: Float): TransactionAddEvent
    data class OnSearchQueryChanged(val query: String): TransactionAddEvent
    data object OnAddTransactionClicked: TransactionAddEvent
}

data class TransactionItemState(
    val amountState: TextFieldState = TextFieldState(),
    val priceState: TextFieldState = TextFieldState()
)