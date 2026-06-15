package org.lelestacia.posle.domain.component

import androidx.compose.foundation.text.input.TextFieldState
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.data.SettingManager
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.domain.state_event.TransactionItemState

data class TransactionProductConfigState(
    val product: Product,
    val selectedVariants: List<Variant> = emptyList(),
    val amountState: TextFieldState = TextFieldState("1"),
    val priceState: TextFieldState,
    val settings: PosLeSettings = PosLeSettings()
)

sealed interface TransactionProductConfigEvent {
    data class OnVariantClicked(val variant: Variant) : TransactionProductConfigEvent
    data class OnAmountChanged(val amount: Float) : TransactionProductConfigEvent
    data object OnConfirmed : TransactionProductConfigEvent
}

class TransactionProductConfigComponent(
    componentContext: ComponentContext,
    val product: Product,
    private val settingManager: SettingManager,
    private val onConfirmed: (TransactionItemState, List<Variant>) -> Unit
) : ComponentContext by componentContext {

    val scope = CoroutineScope(Dispatchers.Main.immediate)

    val state: Value<TransactionProductConfigState>
        field = MutableValue(
            TransactionProductConfigState(
                product = product,
                priceState = TextFieldState(product.price.value.toString())
            )
        )

    init {
        scope.launch {
            val settings = settingManager.readSettings().first()
            state.update { it.copy(settings = settings) }
        }
    }

    fun onEvent(event: TransactionProductConfigEvent) {
        when (event) {
            is TransactionProductConfigEvent.OnVariantClicked -> {
                val selected = state.value.selectedVariants.toMutableList()
                if (event.variant in selected) {
                    selected.remove(event.variant)
                } else {
                    selected.add(event.variant)
                }
                state.update { it.copy(selectedVariants = selected) }
            }

            is TransactionProductConfigEvent.OnAmountChanged -> {
                state.value.amountState.edit {
                    replace(0, length, if (event.amount % 1 == 0f) event.amount.toInt().toString() else event.amount.toString())
                }
            }

            TransactionProductConfigEvent.OnConfirmed -> {
                onConfirmed(
                    TransactionItemState(
                        amountState = state.value.amountState,
                        priceState = state.value.priceState
                    ),
                    state.value.selectedVariants
                )
            }
        }
    }
}
