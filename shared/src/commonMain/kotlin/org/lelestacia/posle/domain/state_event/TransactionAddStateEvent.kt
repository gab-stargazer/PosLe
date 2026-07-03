package org.lelestacia.posle.domain.state_event

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.StringResource
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.TransactionItem
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.domain.state_event.TransactionAddState.DialogState
import org.lelestacia.posle.util.CartItems
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.msg_error_amount_cannot_be_empty
import posle.shared.generated.resources.msg_error_price_cannot_be_empty
import posle.shared.generated.resources.msg_error_quantity_exceeded

@Immutable
data class TransactionAddState(
    val searchQuery: TextFieldState = TextFieldState(),
    val customerName: TextFieldState = TextFieldState(),
    val settings: PosLeSettings = PosLeSettings(),
    val cartItems: CartItems = emptyList(),
    val currentTab: Int = 0,

    val isDialogShown: Boolean = false,
    val dialogState: DialogState = DialogState(),
) {
    @Immutable
    data class DialogState(
        val selectedProduct: Product? = null,
        val amount: String = "",
        val amountError: StringResource? = null,
        val price: String = "",
        val priceState: TextFieldState = TextFieldState(),
        val priceStateError: StringResource? = null,
        val noteState: TextFieldState = TextFieldState(),
        val settings: PosLeSettings = PosLeSettings()
    )
}

fun DialogState.validate(): DialogState {
    val isStockEnabled = settings.isProductStockTracked
    val stock = selectedProduct?.stock?.value ?: throw Exception("Stock is null on Validation")

    val amountError = when {
        amount.isBlank() -> Res.string.msg_error_amount_cannot_be_empty
        (amount.toFloatOrNull() ?: 0F) == 0F -> Res.string.msg_error_amount_cannot_be_empty
        isStockEnabled && amount.toFloat() > stock -> Res.string.msg_error_quantity_exceeded
        else -> null
    }

    val priceError = when {
        price.isBlank() -> Res.string.msg_error_price_cannot_be_empty
        else -> null
    }

    return this.copy(
        amountError = amountError,
        priceStateError = priceError
    )
}

sealed interface TransactionAddEvent {
    data class OnSearchQueryChanged(val query: String) : TransactionAddEvent
    data class OnTabChanged(val index: Int) : TransactionAddEvent
    data class OnRequestProductConfig(val product: Product) : TransactionAddEvent
    data class OnRemoveProduct(val product: TransactionItem) : TransactionAddEvent
    data object OnAddTransactionClicked : TransactionAddEvent

    sealed interface DialogEvent : TransactionAddEvent {
        data class OnAmountChanged(val newAmount: String) : DialogEvent
        data class OnPriceChanged(val newPrice: String) : DialogEvent
        data class OnShown(val selectedProduct: Product) : DialogEvent
        data object OnDismiss : DialogEvent
        data object OnAddClicked : DialogEvent
    }
}

data class TransactionItemState(
    val amountState: TextFieldState = TextFieldState(),
    val priceState: TextFieldState = TextFieldState(),
    val noteState: TextFieldState = TextFieldState(),
    val variants: List<Variant> = emptyList()
)