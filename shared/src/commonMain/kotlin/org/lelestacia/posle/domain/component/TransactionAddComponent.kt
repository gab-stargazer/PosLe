package org.lelestacia.posle.domain.component

import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.lelestacia.posle.data.SettingManager
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.model.TransactionItem
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.domain.repository.TransactionRepository
import org.lelestacia.posle.domain.state_event.TransactionAddEvent
import org.lelestacia.posle.domain.state_event.TransactionAddState
import org.lelestacia.posle.domain.state_event.TransactionItemState
import org.lelestacia.posle.navigation.Config
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import java.math.BigDecimal
import kotlin.time.Clock

class TransactionAddComponent(
    componentContext: ComponentContext,
    productRepository: ProductRepository,
    private val settingManager: SettingManager,
    private val onNavigateTo: (Config) -> Unit,
    private val transactionRepository: TransactionRepository
) : ComponentContext by componentContext {

    val scope = CoroutineScope(Dispatchers.Main.immediate)

    val products = productRepository.readProduct("").cachedIn(scope)

    private val _state = MutableStateFlow(TransactionAddState())
    private val settings = settingManager.readSettings()
    val state = combine(
        flow = _state,
        flow2 = settings
    ) { state, settings ->
        TransactionAddState(
            products = state.products,
            customerName = state.customerName,
            settings = settings
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = TransactionAddState()
    )

    fun onEvent(event: TransactionAddEvent) = scope.launch {
        when (event) {
            is TransactionAddEvent.OnAddNewProduct -> _state.update { currentState ->
                val productMap = currentState.products.toMutableMap()
                productMap[event.product] = TransactionItemState()
                currentState.copy(
                    products = productMap
                )
            }

            is TransactionAddEvent.OnRemoveProduct -> _state.update { currentState ->
                val productMap = currentState.products.toMutableMap()
                productMap.remove(event.product)
                currentState.copy(
                    products = productMap
                )
            }

            is TransactionAddEvent.OnAmountChanged -> _state.update { currentState ->
                val productMap = currentState.products.toMutableMap()
                val itemState = productMap[event.product]
                itemState?.amountState?.setTextAndPlaceCursorAtEnd(
                    if (event.newAmount % 1 == 0f) {
                        event.newAmount.toInt().toString()
                    } else {
                        event.newAmount.toString()
                    }
                )
                currentState.copy(
                    products = productMap
                )
            }

            TransactionAddEvent.OnAddTransactionClicked -> {
                val selectedProducts = mutableListOf<TransactionItem>()
                state.value.products.entries.forEach { map ->
                    selectedProducts.add(
                        TransactionItem(
                            id = 0,
                            productName = map.key.name,
                            productPrice =
                                if (settings.first().isProductVolatile) {
                                    val currentPrice = Price(BigDecimal(map.value.priceState.text.toString().ifBlank { "0" }))
                                    if (currentPrice.value > BigDecimal.ZERO) {
                                        currentPrice
                                    } else {
                                        map.key.price
                                    }
                                } else {
                                    map.key.price
                                },
                            productUnit = map.key.unit,
                            productAmount = Amount(map.value.amountState.text.toString().toFloat())
                        )
                    )
                }

                val transaction = Transaction(
                    id = 0,
                    customerName = Name(state.value.customerName.text.toString()),
                    items = selectedProducts,
                    createdAt = Clock.System.now().toEpochMilliseconds()
                )

                onNavigateTo(
                    Config.TransactionView(
                        transaction = transactionRepository.insertAndGetTransaction(transaction)
                    )
                )
            }
        }
    }
}