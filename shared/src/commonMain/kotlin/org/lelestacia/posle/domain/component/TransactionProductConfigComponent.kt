package org.lelestacia.posle.domain.component

import androidx.compose.foundation.text.input.TextFieldState
import com.arkivanov.decompose.value.Value
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.Variant

data class TransactionProductConfigState(
    val product: Product,
    val selectedVariants: List<Variant> = emptyList(),
    val amountState: TextFieldState = TextFieldState("1"),
    val priceState: TextFieldState = TextFieldState(),
    val noteState: TextFieldState = TextFieldState(),
    val settings: PosLeSettings = PosLeSettings()
)

sealed interface TransactionProductConfigEvent {
    data class OnVariantClicked(val variant: Variant, val isChecked: Boolean) :
        TransactionProductConfigEvent

    data class OnAmountChanged(val amount: Float) : TransactionProductConfigEvent
    data object OnConfirmed : TransactionProductConfigEvent
}

interface TransactionProductConfigComponent {
    val state: Value<TransactionProductConfigState>
    fun onEvent(event: TransactionProductConfigEvent)
}
