package org.lelestacia.posle.domain.component.transaction_add

import androidx.compose.foundation.text.input.TextFieldState
import androidx.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
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
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.model.TransactionItem
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.domain.repository.TransactionRepository
import org.lelestacia.posle.domain.state_event.TransactionAddEvent
import org.lelestacia.posle.domain.state_event.TransactionAddState
import org.lelestacia.posle.domain.state_event.TransactionAddState.DialogState
import org.lelestacia.posle.domain.state_event.TransactionItemState
import org.lelestacia.posle.domain.state_event.validate
import org.lelestacia.posle.navigation.Config.TransactionView
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.coroutineScope
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

class TransactionAddComponentImpl(
    componentContext: ComponentContext,
    private val settingManager: SettingManager,
    private val navigation: TransactionAddNavigation,
    private val productRepository: ProductRepository,
    private val transactionRepository: TransactionRepository
) : ComponentContext by componentContext, TransactionAddComponent {

    private val scope = coroutineScope(Dispatchers.Main.immediate)

    private val _searchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    override val products = _searchQuery
        .debounce(300.milliseconds)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            productRepository.readProducts(query)
        }
        .cachedIn(scope)

    private val _state = MutableStateFlow(TransactionAddState())
    override val state = combine(
        flow = _state,
        flow2 = settingManager.readSettings()
    ) { state, settings ->
        state.copy(
            settings = settings,
            dialogState = state.dialogState.copy(
                settings = settings
            )
        )
    }.stateIn(
        scope,
        SharingStarted.Lazily,
        TransactionAddState()
    )

    override fun onEvent(event: TransactionAddEvent) {
        when (event) {

            is TransactionAddEvent.OnSearchQueryChanged -> {
                _searchQuery.value = event.query
            }

            is TransactionAddEvent.OnTabChanged -> {
                _state.update { it.copy(currentTab = event.index) }
            }

            is TransactionAddEvent.OnRequestProductConfig -> {
                val product = event.product
                navigation.onNavigateToProductConfig(product) { itemState: TransactionItemState, variants: List<Variant> ->
                    scope.launch {
                        _state.update { currentState ->

                            val cartItems = currentState.cartItems.toMutableList()

                            //  Will Come back later, probably needed for restaurant, might make it hard for selling fruits or something in bulk like Karung
                            val isInCart = cartItems.any { cartItem ->
                                cartItem.productName == product.name &&
                                        cartItem.variants.toSet() == variants.toSet()
                            }

                            cartItems.add(
                                TransactionItem(
                                    id = 0,
                                    productName = product.name,
                                    productId = product.id,
                                    productBuyPrice = product.buyPrice,
                                    productSellPrice = Price(
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

            is TransactionAddEvent.OnRemoveProduct -> {
                _state.update { currentState ->
                    val cartItems = currentState.cartItems.toMutableList()
                    cartItems.remove(event.product)
                    currentState.copy(cartItems = cartItems)
                }
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
                        config = TransactionView(
                            transaction = transactionRepository.insertAndGetTransaction(transaction)
                        )
                    ) {
                        _state.update {
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

            TransactionAddEvent.OnNavigateToQrScanner -> {
                navigation.onNavigateToQRScanner { skuNumber ->
                    scope.launch {
                        productRepository.getProductBySkuNumber(skuNumber)?.let { product ->
                            _state.update { currentState ->
                                currentState.copy(
                                    isDialogShown = true,
                                    dialogState = DialogState(
                                        selectedProduct = product,
                                        settings = state.value.settings
                                    )
                                )
                            }
                        }
                    }
                }
            }

            is TransactionAddEvent.DialogEvent -> onDialogEvent(event)
        }
    }

    private fun onDialogEvent(event: TransactionAddEvent.DialogEvent) {
        when (event) {
            is TransactionAddEvent.DialogEvent.OnAmountChanged -> {
                _state.update { currentState ->
                    currentState.copy(
                        dialogState = currentState.dialogState.copy(
                            amount = event.newAmount,
                            amountError = null
                        )
                    )
                }
            }

            is TransactionAddEvent.DialogEvent.OnPriceChanged -> {
                _state.update { currentState ->
                    currentState.copy(
                        dialogState = currentState.dialogState.copy(
                            price = event.newPrice,
                            priceStateError = null
                        )
                    )
                }
            }

            TransactionAddEvent.DialogEvent.OnAddClicked -> {
                _state.update { currentState ->

                    val cartItems = currentState.cartItems.toMutableList()

                    val selectedProduct = currentState.dialogState.selectedProduct
                        ?: throw Exception("Product didn't get passed properly")

                    //  Will Come back later, probably needed for restaurant, might make it hard for selling fruits or something in bulk like Karung
                    val isInCart = cartItems.any { cartItem ->
                        cartItem.productName == selectedProduct.name
                    }


                    println("CurrentState: ${currentState.dialogState}")
                    val validationResult = currentState.dialogState.validate()
                    val errors = listOf(
                        validationResult.amountError,
                        validationResult.priceStateError
                    )

                    if (errors.any { error -> error != null }) {
                        currentState.copy(
                            dialogState = validationResult
                        )
                    } else {
                        cartItems.add(
                            TransactionItem(
                                id = 0,
                                productName = selectedProduct.name,
                                productId = selectedProduct.id,
                                productBuyPrice = selectedProduct.buyPrice,
                                productSellPrice = Price(
                                    when (state.value.settings.isProductVolatile) {
                                        true -> {
                                            currentState.dialogState.price
                                                .ifBlank { "0" }
                                                .toBigDecimal()
                                        }

                                        false -> {
                                            selectedProduct.sellPrice.value
                                        }
                                    }
                                ),
                                productUnit = selectedProduct.unit,
                                productAmount = Amount(
                                    currentState.dialogState.amount
                                        .toFloat()
                                ),
                                productNote = state.value.dialogState.noteState.text
                                    .toString()
                                    .ifBlank { null },
                            )
                        )

                        currentState.copy(
                            isDialogShown = false,
                            dialogState = DialogState(),
                            cartItems = cartItems.sortedBy { it.productName.value }
                        )
                    }
                }
            }

            TransactionAddEvent.DialogEvent.OnDismiss -> {
                _state.update { currentState ->
                    currentState.copy(
                        isDialogShown = false,
                        dialogState = DialogState()
                    )
                }
            }

            is TransactionAddEvent.DialogEvent.OnShown -> {
                println("Settings: ${state.value.settings}")
                println("Sell Price: ${event.selectedProduct.sellPrice.value}")

                _state.update { currentState ->
                    currentState.copy(
                        isDialogShown = true,
                        dialogState = DialogState(
                            selectedProduct = event.selectedProduct,
                            settings = state.value.settings
                        )
                    )
                }
            }

        }
    }
}
