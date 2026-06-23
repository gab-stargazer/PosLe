package org.lelestacia.posle.domain.component

import androidx.compose.foundation.text.input.TextFieldState
import androidx.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.lelestacia.posle.data.SettingManager
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.model.TransactionItem
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.domain.repository.TransactionRepository
import org.lelestacia.posle.domain.state_event.TransactionAddEvent
import org.lelestacia.posle.domain.state_event.TransactionAddState
import org.lelestacia.posle.domain.state_event.TransactionItemState
import org.lelestacia.posle.navigation.Config
import org.lelestacia.posle.navigation.Config.TransactionView
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.coroutineScope
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

interface TransactionAddNavigation {
    fun onNavigateTo(config: Config, onComplete: () -> Unit = {})

    fun onNavigateToProductConfig(
        product: Product,
        onConfirmed: (TransactionItemState, List<Variant>) -> Unit
    )
}

class TransactionAddComponent(
    componentContext: ComponentContext,
    productRepository: ProductRepository,
    private val settingManager: SettingManager,
    private val navigation: TransactionAddNavigation,
    private val transactionRepository: TransactionRepository
) : ComponentContext by componentContext {

    private val scope = coroutineScope(Dispatchers.Main.immediate)

    private val _searchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val products = _searchQuery
        .debounce(300.milliseconds)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            productRepository.readProducts(query)
        }
        .cachedIn(scope)

    val state: StateFlow<TransactionAddState>
        field = MutableStateFlow(TransactionAddState())

    init {
        scope.launch {
            state.update { currentState ->
                currentState.copy(
                    settings = settingManager
                        .readSettings()
                        .first()
                )
            }
        }
    }

    fun onEvent(event: TransactionAddEvent) {
        when (event) {

            is TransactionAddEvent.OnSearchQueryChanged -> {
                _searchQuery.value = event.query
            }

            is TransactionAddEvent.OnTabChanged -> {
                state.update { it.copy(currentTab = event.index) }
            }

            is TransactionAddEvent.OnRequestProductConfig -> {
                val product = event.product
                navigation.onNavigateToProductConfig(product) { itemState: TransactionItemState, variants: List<Variant> ->
                    scope.launch {
                        state.update { currentState ->

                            val cartItems = currentState.cartItems.toMutableList()

                            //  Will Comeback later, probably needed for restaurant, might make it hard for selling fruits or something in bulk like Karung
                            val isInCart = cartItems.any { cartItem ->
                                cartItem.productName == product.name &&
                                        cartItem.variants.toSet() == variants.toSet()
                            }

                            cartItems.add(
                                TransactionItem(
                                    id = 0,
                                    productName = product.name,
                                    productId = product.id,
                                    productPrice = Price(
                                        itemState.priceState.text
                                            .toString()
                                            .ifBlank { "0" }
                                            .toBigDecimal()
                                    ),
                                    productUnit = product.unit,
                                    productAmount = Amount(
                                        itemState.amountState.text
                                            .toString()
                                            .toFloat()
                                    ),
                                    productNote = itemState.noteState.text
                                        .toString()
                                        .ifBlank { null },
                                    variants = variants
                                )
                            )

                            currentState.copy(
                                cartItems = cartItems.sortedBy { it.productName.value }
                            )
                        }
                    }
                }
            }

            is TransactionAddEvent.OnRemoveProduct -> state.update { currentState ->
                val cartItems = currentState.cartItems.toMutableList()
                cartItems.remove(event.product)
                currentState.copy(cartItems = cartItems)
            }

            TransactionAddEvent.OnAddTransactionClicked -> {
                val transaction = Transaction(
                    id = 0,
                    customerName = Name(state.value.customerName.text.toString()),
                    items = state.value.cartItems,
                    createdAt = Clock.System.now().toEpochMilliseconds()
                )

                scope.launch {
                    navigation.onNavigateTo(
                        TransactionView(
                            transaction = transactionRepository.insertAndGetTransaction(transaction)
                        )
                    ) {
                        state.update {
                            it.copy(
                                searchQuery = TextFieldState(),
                                customerName = TextFieldState(),
                                cartItems = emptyList(),
                                currentTab = 0
                            )
                        }
                    }
                }
            }
        }
    }
}
