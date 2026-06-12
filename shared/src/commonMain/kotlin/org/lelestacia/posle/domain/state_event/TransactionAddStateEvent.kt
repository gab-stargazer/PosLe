package org.lelestacia.posle.domain.state_event

import androidx.compose.foundation.text.input.TextFieldState
import org.lelestacia.posle.domain.model.Product

data class TransactionAddState(
    val products: Map<Product, TransactionItemState> = mapOf()
)

sealed interface TransactionAddEvent {
    data class OnAddNewProduct(val product: Product): TransactionAddEvent
    data class OnRemoveProduct(val product: Product): TransactionAddEvent
    data object OnAddTransactionClicked: TransactionAddEvent
}

data class TransactionItemState(
    val amountState: TextFieldState = TextFieldState(),
    val priceState: TextFieldState = TextFieldState()
)