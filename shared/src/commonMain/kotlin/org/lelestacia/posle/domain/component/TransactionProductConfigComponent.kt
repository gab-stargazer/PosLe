package org.lelestacia.posle.domain.component

import androidx.compose.foundation.text.input.TextFieldState
import com.arkivanov.decompose.value.Value
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.Variant

/**
 * UI State for configuring a product during a transaction.
 */
data class TransactionProductConfigState(
    val product: Product,
    val selectedVariants: List<Variant> = emptyList(),
    val amountState: TextFieldState = TextFieldState("1"),
    val priceState: TextFieldState = TextFieldState(),
    val noteState: TextFieldState = TextFieldState(),
    val settings: PosLeSettings = PosLeSettings()
)

/**
 * Events for product configuration.
 */
sealed interface TransactionProductConfigEvent {
    /**
     * Toggles a variant for the product.
     */
    data class OnVariantClicked(val variant: Variant, val isChecked: Boolean) :
        TransactionProductConfigEvent

    /**
     * Updates the quantity being sold.
     */
    data class OnAmountChanged(val amount: java.math.BigDecimal) : TransactionProductConfigEvent

    /**
     * Confirms the configuration and adds it to the cart.
     */
    data object OnConfirmed : TransactionProductConfigEvent
}

/**
 * Component interface for configuring a product's sale parameters (price, quantity, variants).
 */
interface TransactionProductConfigComponent {
    /**
     * Current configuration state.
     */
    val state: Value<TransactionProductConfigState>

    /**
     * Processes configuration events.
     */
    fun onEvent(event: TransactionProductConfigEvent)
}
