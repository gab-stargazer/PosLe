package org.lelestacia.posle.domain.state_event

import androidx.compose.foundation.text.input.TextFieldState
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.TransactionItem
import org.lelestacia.posle.domain.model.Variant

data class TransactionAddState(
    val searchQuery: TextFieldState = TextFieldState(),
    val customerName: TextFieldState = TextFieldState(),
    val settings: PosLeSettings = PosLeSettings(),
    val carts: List<TransactionItem> = emptyList(),
    val currentTab: Int = 0,
)

sealed interface TransactionAddEvent {
    data class OnSearchQueryChanged(val query: String): TransactionAddEvent
    data class OnTabChanged(val index: Int): TransactionAddEvent
    data class OnRequestProductConfig(val product: Product): TransactionAddEvent
    data class OnRemoveProduct(val product: TransactionItem): TransactionAddEvent
    data object OnAddTransactionClicked: TransactionAddEvent
}

data class TransactionItemState(
    val amountState: TextFieldState = TextFieldState(),
    val priceState: TextFieldState = TextFieldState(),
    val variants: List<Variant> = emptyList()
)