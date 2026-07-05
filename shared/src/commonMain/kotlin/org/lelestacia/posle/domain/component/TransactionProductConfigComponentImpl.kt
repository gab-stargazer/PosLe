package org.lelestacia.posle.domain.component

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.SnackbarHostState
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.lelestacia.posle.data.SettingManager
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.domain.state_event.TransactionItemState
import org.lelestacia.posle.util.coroutineScope
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.msg_error_price_cannot_be_empty
import posle.shared.generated.resources.msg_error_unit_cannot_be_empty

class TransactionProductConfigComponentImpl(
    componentContext: ComponentContext,
    private val product: Product,
    private val snackbarHostState: SnackbarHostState,
    private val settingManager: SettingManager,
    private val onConfirmed: (TransactionItemState, List<Variant>) -> Unit
) : ComponentContext by componentContext, TransactionProductConfigComponent {

    private val scope = coroutineScope(Dispatchers.Main.immediate)

    override val state: Value<TransactionProductConfigState>
        field = MutableValue(
            TransactionProductConfigState(
                product = product,
                priceState = TextFieldState()
            )
        )

    init {
        scope.launch {
            val settings = settingManager.readSettings().first()
            state.update { it.copy(settings = settings) }
        }
    }

    override fun onEvent(event: TransactionProductConfigEvent) {
        when (event) {
            is TransactionProductConfigEvent.OnVariantClicked -> {
                val selected = state.value.selectedVariants.toMutableList()
                when (event.isChecked) {
                    true -> {
                        selected.add(event.variant)
                    }

                    false -> {
                        selected.remove(event.variant)
                    }
                }

                state.update { it.copy(selectedVariants = selected) }
            }

            is TransactionProductConfigEvent.OnAmountChanged -> {
                state.value.amountState.edit {
                    replace(
                        start = 0,
                        end = length,
                        text = if (event.amount % 1 == 0f) event.amount.toInt()
                            .toString() else event.amount.toString()
                    )
                }
            }

            TransactionProductConfigEvent.OnConfirmed -> {
                val currentState = state.value
                if (currentState.settings.isProductStockTracked) {
                    val amount = currentState.amountState
                        .text
                        .toString()
                        .toFloat()

                    if (amount > product.stock.value) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                getString(Res.string.msg_error_unit_cannot_be_empty)
                            )
                        }
                        return
                    }
                }

                if (
                    currentState.settings.isProductVolatile && currentState.priceState.text
                        .toString()
                        .isBlank()
                ) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            getString(Res.string.msg_error_price_cannot_be_empty)
                        )
                    }
                    return
                }

                val productPrice =
                    when (currentState.settings.isProductVolatile) {
                        true -> currentState.priceState
                        false -> TextFieldState(currentState.product.sellPrice.value.toString())
                    }

                onConfirmed(
                    TransactionItemState(
                        amountState = currentState.amountState,
                        priceState = productPrice,
                        noteState = currentState.noteState
                    ),
                    currentState.selectedVariants
                )
            }
        }
    }
}
