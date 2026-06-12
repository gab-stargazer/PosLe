package org.lelestacia.posle.domain.component

import androidx.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.model.TransactionItem
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.domain.repository.TransactionRepository
import org.lelestacia.posle.domain.state_event.TransactionAddEvent
import org.lelestacia.posle.domain.state_event.TransactionAddState
import org.lelestacia.posle.domain.state_event.TransactionItemState
import org.lelestacia.posle.navigation.Config
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Price
import java.math.BigDecimal
import kotlin.time.Clock

class TransactionAddComponent(
    componentContext: ComponentContext,
    private val onNavigateTo: (Config) -> Unit,
    private val productRepository: ProductRepository,
    private val transactionRepository: TransactionRepository
) : ComponentContext by componentContext {

    val scope = CoroutineScope(Dispatchers.Main.immediate)

    val products = productRepository.readProduct("").cachedIn(scope)

    val state: Value<TransactionAddState>
        field = MutableValue(TransactionAddState())

    fun onEvent(event: TransactionAddEvent) = scope.launch {
        when (event) {
            is TransactionAddEvent.OnAddNewProduct -> state.update { currentState ->
                val productMap = currentState.products.toMutableMap()
                productMap[event.product] = TransactionItemState()
                currentState.copy(
                    products = productMap
                )
            }

            is TransactionAddEvent.OnRemoveProduct -> state.update { currentState ->
                val productMap = currentState.products.toMutableMap()
                productMap.remove(event.product)
                currentState.copy(
                    products = productMap
                )
            }

            TransactionAddEvent.OnAddTransactionClicked -> {
                val selectedProducts = mutableListOf<TransactionItem>()
                state.value.products.entries.forEach {
                    selectedProducts.add(
                        TransactionItem(
                            id = 0,
                            productName = it.key.name,
                            productPrice = if (it.key.isProductVolatile) {
                                Price(BigDecimal(it.value.priceState.text.toString()))
                            } else {
                                it.key.price
                            },
                            productUnit = it.key.unit,
                            productAmount = Amount(it.value.amountState.text.toString().toFloat())
                        )
                    )
                }

                val transaction = Transaction(
                    id = 0,
                    items = selectedProducts,
                    createdAt = Clock.System.now().toEpochMilliseconds()
                )

                onNavigateTo(Config.TransactionView(transactionRepository.insertAndGetTransaction(transaction)))
            }
        }
    }
}