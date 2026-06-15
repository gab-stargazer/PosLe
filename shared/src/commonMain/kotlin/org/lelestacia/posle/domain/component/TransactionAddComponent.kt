package org.lelestacia.posle.domain.component

import androidx.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
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
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

class TransactionAddComponent(
    componentContext: ComponentContext,
    productRepository: ProductRepository,
    private val settingManager: SettingManager,
    private val onNavigateTo: (Config) -> Unit,
    private val onNavigateToProductConfig: (Product, onConfirmed: (TransactionItemState, List<Variant>) -> Unit) -> Unit,
    private val transactionRepository: TransactionRepository
) : ComponentContext by componentContext {

    val scope = CoroutineScope(Dispatchers.Main.immediate)

    private val _searchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val products = _searchQuery
        .debounce(300.milliseconds)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            productRepository.readProduct(query)
        }
        .cachedIn(scope)

    private val _state = MutableStateFlow(TransactionAddState())
    private val settings = settingManager.readSettings()
    val state = combine(
        flow = _state,
        flow2 = settings
    ) { state, settings ->
        TransactionAddState(
            searchQuery = state.searchQuery,
            carts = state.carts,
            customerName = state.customerName,
            settings = settings,
            currentTab = state.currentTab
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = TransactionAddState()
    )

    fun onEvent(event: TransactionAddEvent) = scope.launch {
        when (event) {

            is TransactionAddEvent.OnSearchQueryChanged -> {
                _searchQuery.value = event.query
            }

            is TransactionAddEvent.OnTabChanged -> {
                _state.update { it.copy(currentTab = event.index) }
            }

            is TransactionAddEvent.OnRequestProductConfig -> {
                val product = event.product
                onNavigateToProductConfig(product) { itemState: TransactionItemState, variants: List<Variant> ->
                    scope.launch {
                        _state.update { currentState ->

                            val cartItems = currentState.carts.toMutableList()
                            cartItems.add(
                                TransactionItem(
                                    id = 0,
                                    productName = product.name,
                                    productPrice = Price(itemState.priceState.text.toString().ifBlank { "0" }.toBigDecimal()),
                                    productUnit = product.unit,
                                    productAmount = Amount(itemState.amountState.text.toString().toFloat()),
                                    variants = variants
                                )
                            )

                            currentState.copy(
                                carts = cartItems
                            )
                        }
                    }
                }
            }

            is TransactionAddEvent.OnRemoveProduct -> _state.update { currentState ->
                val carts = currentState.carts.toMutableList()
                carts.remove(event.product)
                currentState.copy(carts = carts)
            }

            TransactionAddEvent.OnAddTransactionClicked -> {
                val transaction = Transaction(
                    id = 0,
                    customerName = Name(state.value.customerName.text.toString()),
                    items = state.value.carts,
                    createdAt = Clock.System.now().toEpochMilliseconds()
                )

                onNavigateTo(
                    TransactionView(
                        transaction = transactionRepository.insertAndGetTransaction(transaction)
                    )
                )
            }
        }
    }
}
