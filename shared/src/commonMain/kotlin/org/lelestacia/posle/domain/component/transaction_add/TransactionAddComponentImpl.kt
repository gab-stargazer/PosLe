package org.lelestacia.posle.domain.component.transaction_add

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.SnackbarHostState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.lelestacia.posle.data.SettingManager
import org.lelestacia.posle.domain.model.Bundle
import org.lelestacia.posle.domain.model.CartItems
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.domain.repository.BundleRepository
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.domain.repository.TransactionRepository
import org.lelestacia.posle.domain.state_event.TransactionAddEvent
import org.lelestacia.posle.domain.state_event.TransactionAddState
import org.lelestacia.posle.domain.state_event.TransactionAddState.DialogBundleState
import org.lelestacia.posle.domain.state_event.TransactionAddState.DialogProductState
import org.lelestacia.posle.domain.state_event.TransactionItemState
import org.lelestacia.posle.domain.state_event.validate
import org.lelestacia.posle.navigation.Config.TransactionView
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.coroutineScope
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.msg_error_item_not_available
import posle.shared.generated.resources.msg_error_quantity_cannot_be_empty
import java.math.BigDecimal
import kotlin.time.Duration.Companion.milliseconds

class TransactionAddComponentImpl(
    componentContext: ComponentContext,
    settingManager: SettingManager,
    private val snackBarHostState: SnackbarHostState,
    private val navigation: TransactionAddNavigation,
    private val bundleRepository: BundleRepository,
    private val productRepository: ProductRepository,
    private val transactionRepository: TransactionRepository
) : ComponentContext by componentContext, TransactionAddComponent {

    private val scope = coroutineScope(Dispatchers.Main.immediate)

    private val _searchQuery = MutableStateFlow("")

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    override val bundles: Flow<PagingData<Bundle>> = _searchQuery
        .debounce(300.milliseconds)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            bundleRepository.readBundleByName(query)
        }
        .cachedIn(scope)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    override val products = _searchQuery
        .debounce(300.milliseconds)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            productRepository.readProductsByName(query)
        }
        .cachedIn(scope)

    private val _state = MutableStateFlow(TransactionAddState())
    override val state = combine(
        flow = _state,
        flow2 = settingManager.readSettings(),
        flow3 = _searchQuery
    ) { state, settings, searchQuery ->
        state.copy(
            searchQuery = searchQuery,
            settings = settings,
            dialogProductState = state.dialogProductState.copy(
                settings = settings
            ),
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = TransactionAddState()
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
//                        _state.update { currentState ->
//
//                            val cartItems = currentState.cartItems.toMutableList()
//
//                            //  Will Come back later, probably needed for restaurant, might make it hard for selling fruits or something in bulk like Karung
//                            val isInCart = cartItems.any { cartItem ->
//                                cartItem.productName == product.name &&
//                                        cartItem.variants.toSet() == variants.toSet()
//                            }
//
//                            cartItems.add(
//                                TransactionItem(
//                                    id = 0,
//                                    productName = product.name,
//                                    productId = product.id,
//                                    productBuyPrice = product.buyPrice,
//                                    productSellPrice = Price(
//                                        itemState.priceState.text
//                                            .toString()
//                                            .ifBlank { "0" }
//                                            .toBigDecimal()
//                                    ),
//                                    productUnit = product.unit,
//                                    productAmount = Amount(
//                                        itemState.amountState.text
//                                            .toString()
//                                            .toFloat()
//                                    ),
//                                    productNote = itemState.noteState.text
//                                        .toString()
//                                        .ifBlank { null },
//                                    variants = variants
//                                )
//                            )
//
//                            currentState.copy(
//                                cartItems = cartItems.sortedBy { it.productName.value }
//                            )
//                        }
                    }
                }
            }

            is TransactionAddEvent.OnRemoveProduct -> {
                _state.update { currentState ->
                    val cartItems = currentState.cartItems.toMutableList()
                    cartItems.remove(event.cartItems)
                    currentState.copy(cartItems = cartItems)
                }
            }

            TransactionAddEvent.OnAddTransactionClicked -> {
                val customerName = Name(state.value.customerName.text.toString())
                val cartItems = state.value.cartItems

                scope.launch {
                    navigation.onNavigateTo(
                        config = TransactionView(
                            transaction = transactionRepository.insertAndGetTransaction(
                                customerName = customerName,
                                cartItems = cartItems
                            )
                        )
                    ) {
                        _state.update {
                            it.copy(
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
                        val product = productRepository.getProductBySkuNumber(skuNumber)
                        product?.let { product ->
                            _state.update { currentState ->
                                currentState.copy(
                                    isDialogProductShown = true,
                                    dialogProductState = DialogProductState(
                                        selectedProduct = product,
                                        settings = state.value.settings
                                    )
                                )
                            }
                        }

                        if (product == null) {
                            snackBarHostState.showSnackbar("Produk tidak ditemukan")
                        }
                    }
                }
            }

            is TransactionAddEvent.DialogProductEvent -> onDialogEvent(event)
            is TransactionAddEvent.DialogBundleEvent -> onDialogBundleEvent(event)
        }
    }

    private fun onDialogEvent(event: TransactionAddEvent.DialogProductEvent) {
        when (event) {
            is TransactionAddEvent.DialogProductEvent.OnAmountChanged -> {
                _state.update { currentState ->
                    currentState.copy(
                        dialogProductState = currentState.dialogProductState.copy(
                            amount = event.newAmount,
                            amountError = null
                        )
                    )
                }
            }

            is TransactionAddEvent.DialogProductEvent.OnPriceChanged -> {
                _state.update { currentState ->
                    currentState.copy(
                        dialogProductState = currentState.dialogProductState.copy(
                            price = event.newPrice,
                            priceStateError = null
                        )
                    )
                }
            }

            TransactionAddEvent.DialogProductEvent.OnAddClicked -> {
                _state.update { currentState ->

                    val cartItems = currentState.cartItems.toMutableList()

                    val selectedProduct = currentState.dialogProductState.selectedProduct
                        ?: throw Exception("Product didn't get passed properly")

                    val validationResult = currentState.dialogProductState.validate()
                    val errors = listOf(
                        validationResult.amountError,
                        validationResult.priceStateError
                    )

                    if (errors.any { error -> error != null }) {
                        currentState.copy(
                            dialogProductState = validationResult
                        )
                    } else {
                        cartItems.add(
                            CartItems.ProductCartItem(
                                id = 0,
                                productName = selectedProduct.name,
                                productId = selectedProduct.id,
                                skuNumber = selectedProduct.skuNumber,
                                imageUri = selectedProduct.imageUri,
                                //  Updated on Repository
                                productBuyPrice = Price(BigDecimal.ZERO),
                                productSellPrice = Price(
                                    when (state.value.settings.isProductVolatile) {
                                        true -> {
                                            currentState.dialogProductState.price
                                                .ifBlank { "0" }
                                                .toBigDecimal()
                                        }

                                        false -> {
                                            selectedProduct.sellPrice.value
                                        }
                                    }
                                ),
                                productUnit = selectedProduct.unit,
                                productQuantity = Amount(
                                    currentState.dialogProductState.amount
                                        .toFloat()
                                ),
                                productNote = state.value.dialogProductState.noteState.text
                                    .toString()
                                    .ifBlank { null },
                            )
                        )

                        currentState.copy(
                            isDialogProductShown = false,
                            dialogProductState = DialogProductState(),
                            cartItems = cartItems
                        )
                    }
                }
            }

            TransactionAddEvent.DialogProductEvent.OnDismiss -> {
                _state.update { currentState ->
                    currentState.copy(
                        isDialogProductShown = false,
                        dialogProductState = DialogProductState()
                    )
                }
            }

            is TransactionAddEvent.DialogProductEvent.OnShown -> {
                println("Settings: ${state.value.settings}")
                println("Sell Price: ${event.selectedProduct.sellPrice.value}")

                _state.update { currentState ->
                    currentState.copy(
                        isDialogProductShown = true,
                        dialogProductState = DialogProductState(
                            selectedProduct = event.selectedProduct,
                            settings = state.value.settings
                        )
                    )
                }
            }

            is TransactionAddEvent.DialogBundleEvent -> onDialogBundleEvent(event)
        }
    }

    fun onDialogBundleEvent(event: TransactionAddEvent.DialogBundleEvent) {
        when (event) {
            is TransactionAddEvent.DialogBundleEvent.OnShown -> _state.update { currentState ->
                currentState.copy(
                    isDialogBundleShown = true,
                    dialogBundleState = DialogBundleState(
                        selectedBundle = event.selectedBundle
                    )
                )
            }

            is TransactionAddEvent.DialogBundleEvent.OnQuantityChanged -> _state.update { currentState ->
                currentState.copy(
                    dialogBundleState = currentState.dialogBundleState.copy(
                        quantity = event.newQuantity,
                        quantityError = null
                    )
                )
            }

            TransactionAddEvent.DialogBundleEvent.OnDismiss -> _state.update { currentState ->
                currentState.copy(
                    isDialogBundleShown = false,
                    dialogBundleState = DialogBundleState()
                )
            }

            TransactionAddEvent.DialogBundleEvent.OnAddClicked -> {
                val currentState = state.value
                val selectedBundle = currentState.dialogBundleState.selectedBundle
                    ?: throw Exception("Bundle didn't get passed properly")

                //  Check For Product Availability
                val bundleProductsError = mutableListOf<Name>()

                scope.launch {
                    if (currentState.dialogBundleState.quantity.isBlank() || currentState.dialogBundleState.quantity.toFloatOrNull() == null) {
                        _state.update { currentState ->
                            currentState.copy(
                                dialogBundleState = currentState.dialogBundleState.copy(
                                    quantityError = getString(
                                        Res.string.msg_error_quantity_cannot_be_empty
                                    )
                                )
                            )
                        }

                        return@launch
                    }

                    if (currentState.settings.isProductStockTracked) {
                        selectedBundle.bundleProducts.forEach { bundleProduct ->
                            val currentlyAvailable =
                                productRepository.getProductAvailability(bundleProduct.productId)

                            val currentlyRequested =
                                bundleProduct.quantity.value * currentState.dialogBundleState.quantity.toFloat()

                            if (currentlyAvailable < currentlyRequested) {
                                bundleProductsError.add(bundleProduct.productName)
                            }
                        }

                        if (bundleProductsError.isNotEmpty()) {
                            _state.update { currentState ->
                                currentState.copy(
                                    dialogBundleState = currentState.dialogBundleState.copy(
                                        quantityError = getString(
                                            Res.string.msg_error_item_not_available,
                                            bundleProductsError.joinToString(", ") { it.value }
                                        )
                                    )
                                )
                            }

                            return@launch
                        }
                    }

                    val cartItems = currentState.cartItems.toMutableList()
                    cartItems.add(
                        CartItems.BundleCartItem(
                            id = 0,
                            bundleId = selectedBundle.id,
                            bundleName = selectedBundle.name,
                            bundleQuantity = Amount(currentState.dialogBundleState.quantity.toFloat()),
                            bundleTotalPrice = Price(
                                selectedBundle.bundleProducts.sumOf { it.sellPrice.value * it.quantity.value.toBigDecimal() }
                            ),
                            bundleNote = currentState
                                .dialogBundleState
                                .noteState
                                .text
                                .toString()
                                .ifBlank { null },
                            bundleProducts = selectedBundle.bundleProducts
                        )
                    )

                    _state.update { currentState ->
                        currentState.copy(
                            isDialogBundleShown = false,
                            dialogBundleState = DialogBundleState(),
                            cartItems = cartItems
                        )
                    }
                }
            }
        }
    }
}
