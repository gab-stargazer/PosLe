package org.lelestacia.posle.domain.component.product_add_edit

import androidx.compose.material3.SnackbarHostState
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.lelestacia.posle.data.entity.StockMovementType
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.domain.repository.StockRepository
import org.lelestacia.posle.domain.repository.VariantRepository
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.Navigation
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.Navigation.OnPop
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.OnAddProductClicked
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.OnAddStockEvent
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.OnDeleteProductClicked
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.OnImageChanged
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.OnSellPriceTheSameAsBuyPriceCheckedChange
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.OnVariantSelected
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditNavigation
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditState
import org.lelestacia.posle.navigation.AddEdit
import org.lelestacia.posle.navigation.AddEdit.Add
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.SkuNumber
import org.lelestacia.posle.util.coroutineScope
import org.lelestacia.posle.util.toDisplayText
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.msg_stock_added
import java.math.BigDecimal
import org.lelestacia.posle.util.Unit as PosLeUnit

class ProductAddEditComponentImpl(
    componentContext: ComponentContext,
    mode: AddEdit,
    private val navigation: ProductAddEditNavigation,
    private val product: Product?,
    private val snackbarHostState: SnackbarHostState,
    private val productRepository: ProductRepository,
    private val stockRepository: StockRepository,
    private val variantRepository: VariantRepository,
) : ComponentContext by componentContext, ProductAddEditComponent {

    private val scope = coroutineScope(Dispatchers.Main.immediate)

    private val buyPriceHistory = productRepository
        .getProductBuyPriceHistory(product?.id ?: 0)

    private val sellPriceHistory = productRepository
        .getProductSellPriceHistory(product?.id ?: 0)

    init {
        if (product != null) {
            scope.launch {
                variantRepository
                    .getVariantsByProductId(product.id)
                    .collectLatest { variants ->
                        _state.update {
                            it.copy(
                                variants = variants
                            )
                        }
                    }
            }
        }
    }

    private val _state = MutableStateFlow(
        ProductAddEditState(
            productName = product?.name?.value.orEmpty(),
            productUnit = product?.unit?.value.orEmpty(),
            skuNumber = product?.skuNumber?.value.orEmpty(),
            productModalPrice = product?.buyPrice?.value?.toString().orEmpty(),
            productSellPrice = product?.sellPrice?.value?.toString().orEmpty(),
            productImageUri = product?.imageUri,
            variants = product?.variants ?: emptyList(),
            mode = mode,
        )
    )

    override val state: StateFlow<ProductAddEditState> = combine(
        flow = _state,
        flow2 = buyPriceHistory,
        flow3 = sellPriceHistory
    ) { state, buyPrice, sellPrice ->
        state.copy(
            buyPriceHistory = buyPrice,
            sellPriceHistory = sellPrice
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = ProductAddEditState(mode = Add)
    )

    override fun onEvent(event: ProductAddEditEvent) {
        when (event) {

            is ProductAddEditEvent.OnProductNameChange -> {
                scope.launch {
                    val currentState = state.value
                    if (currentState.productNameError != null) {
                        val productNameError = currentState.validateName(event.newProductName)
                        if (productNameError != null) {
                            _state.update { currentState ->
                                currentState.copy(
                                    productName = event.newProductName,
                                    productNameError = getString(productNameError)
                                )
                            }
                        } else {
                            _state.update { currentState ->
                                currentState.copy(
                                    productName = event.newProductName,
                                    productNameError = null,
                                )
                            }
                        }
                    } else {
                        _state.update { currentState ->
                            currentState.copy(
                                productName = event.newProductName
                            )
                        }
                    }
                }
            }

            ProductAddEditEvent.OnProductNameRequestValidation -> {
                scope.launch {
                    val currentState = state.value
                    val productNameError = currentState.validateName(currentState.productName)
                    if (productNameError != null) {
                        _state.update { currentState ->
                            currentState.copy(
                                productNameError = getString(productNameError)
                            )
                        }
                    }
                }
            }

            is ProductAddEditEvent.OnProductUnitChange -> {
                scope.launch {
                    val currentState = state.value
                    if (currentState.productUnitError != null) {
                        val productUnitError = currentState.validateUnit(event.newProductUnit)
                        if (productUnitError != null) {
                            _state.update { currentState ->
                                currentState.copy(
                                    productUnit = event.newProductUnit,
                                    productUnitError = getString(productUnitError)
                                )
                            }
                        } else {
                            _state.update { currentState ->
                                currentState.copy(
                                    productUnit = event.newProductUnit,
                                    productUnitError = null,
                                )
                            }
                        }
                    } else {
                        _state.update { currentState ->
                            currentState.copy(
                                productUnit = event.newProductUnit
                            )
                        }
                    }
                }
            }

            ProductAddEditEvent.OnProductUnitRequestValidation -> {
                scope.launch {
                    val currentState = state.value
                    val productUnitError = currentState.validateUnit(currentState.productUnit)
                    if (productUnitError != null) {
                        _state.update { currentState ->
                            currentState.copy(
                                productUnitError = getString(productUnitError)
                            )
                        }
                    }
                }
            }

            is ProductAddEditEvent.OnProductModalPriceChange -> {
                scope.launch {
                    val currentState = state.value
                    if (currentState.productModalPriceError != null) {
                        val productModalPriceError =
                            currentState.validatePrice(event.newModalPrice)
                        if (productModalPriceError != null) {
                            _state.update { currentState ->
                                currentState.copy(
                                    productModalPrice = event.newModalPrice,
                                    productModalPriceError = getString(productModalPriceError)
                                )
                            }
                        } else if (event.newModalPrice.all { it.isDigit() }) {
                            _state.update { currentState ->
                                currentState.copy(
                                    productModalPrice = event.newModalPrice,
                                    productModalPriceError = null,
                                )
                            }
                        }
                    } else if (event.newModalPrice.all { it.isDigit() }) {
                        _state.update { currentState ->
                            currentState.copy(
                                productModalPrice = event.newModalPrice
                            )
                        }
                    }
                }
            }

            ProductAddEditEvent.OnProductModalPriceRequestValidation -> {
                scope.launch {
                    val currentState = state.value
                    val productModalPriceError =
                        currentState.validatePrice(currentState.productModalPrice)
                    if (productModalPriceError != null) {
                        _state.update { currentState ->
                            currentState.copy(
                                productModalPriceError = getString(productModalPriceError)
                            )
                        }
                    }
                }
            }

            is ProductAddEditEvent.OnProductSellPriceChange -> {
                scope.launch {
                    val currentState = state.value
                    if (currentState.productSellPriceError != null) {
                        val productSellPriceError = currentState
                            .validatePrice(event.newSellPrice)

                        if (productSellPriceError != null) {
                            _state.update { currentState ->
                                currentState.copy(
                                    productSellPrice = event.newSellPrice,
                                    productSellPriceError = getString(productSellPriceError)
                                )
                            }
                        } else if (event.newSellPrice.all { it.isDigit() }) {
                            _state.update { currentState ->
                                currentState.copy(
                                    productSellPrice = event.newSellPrice,
                                    productSellPriceError = null,
                                )
                            }
                        }
                    } else if (event.newSellPrice.all { it.isDigit() }) {
                        _state.update { currentState ->
                            currentState.copy(
                                productSellPrice = event.newSellPrice
                            )
                        }
                    }
                }
            }

            ProductAddEditEvent.OnProductSellPriceRequestValidation -> {
                scope.launch {
                    val currentState = state.value
                    val productSellPriceError =
                        currentState.validatePrice(currentState.productSellPrice)
                    if (productSellPriceError != null) {
                        _state.update { currentState ->
                            currentState.copy(
                                productSellPriceError = getString(productSellPriceError)
                            )
                        }
                    }
                }
            }


            is OnSellPriceTheSameAsBuyPriceCheckedChange -> {
                _state.update { currentState ->
                    currentState.copy(
                        isSellPriceAndBuyPriceTheSame = event.newState,
                        productSellPrice = currentState.productModalPrice
                    )
                }
            }

            is OnImageChanged -> {
                _state.update { currentState ->
                    currentState.copy(
                        productImageUri = event.uri,
                        productImageByteArray = event.bytes
                    )
                }
            }

            OnAddProductClicked -> {
                scope.launch {

                    val currentState = state.value
                    val productNameError = currentState.validateName(currentState.productName)
                    val productUnitError = currentState.validateUnit(currentState.productUnit)
                    val modalPriceError = currentState.validatePrice(currentState.productModalPrice)
                    val sellPriceError = currentState.validatePrice(currentState.productSellPrice)
                    val errors = listOf(
                        productNameError,
                        productUnitError,
                        modalPriceError,
                        sellPriceError
                    )

                    if (errors.any { it != null }) {
                        _state.update { currentState ->
                            currentState.copy(
                                productNameError = productNameError?.let { getString(it) },
                                productUnitError = productUnitError?.let { getString(it) },
                                productModalPriceError = modalPriceError?.let { getString(it) },
                                productSellPriceError = sellPriceError?.let { getString(it) }
                            )
                        }
                        return@launch
                    }

                    when (state.value.mode) {
                        Add -> {
                            productRepository.createProduct(
                                product = buildProduct(id = 0),
                                imageByteArray = state.value.productImageByteArray
                            )
                        }

                        AddEdit.Edit -> {
                            productRepository.updateProduct(
                                product = buildProduct(id = product?.id ?: throw Exception("Invalid Product ID")),
                                variantsToAdd = emptyList(),
                                variantsToRemove = emptyList(),
                                imageByteArray = state.value.productImageByteArray
                            )
                        }
                    }

                    onNavigationEvent(OnPop)
                }
            }

            is OnVariantSelected -> {
                val variants = state.value.variants.toMutableList()
                variants.removeAll(state.value.variants)
                variants.addAll(event.variants)

                _state.update { currentState ->
                    currentState.copy(
                        variants = variants.distinctBy { it.id }
                    )
                }
            }

            OnDeleteProductClicked -> {
                scope.launch {
                    productRepository.deleteProduct(
                        product = buildProduct(id = product?.id ?: return@launch)
                    )

                    onNavigationEvent(OnPop)
                }
            }

            is Navigation -> {
                onNavigationEvent(event)
            }

            is OnAddStockEvent -> {
                onAddStockEvent(event)
            }


        }
    }

    private fun onNavigationEvent(event: Navigation) {
        when (event) {
            is Navigation.OnNavigateToVariantView -> {
                navigation.onNavigateToVariantSelection(
                    config = event.config,
                    onResult = { newlySelectedVariant ->
                        onEvent(OnVariantSelected(newlySelectedVariant))
                    }
                )
            }

            is Navigation.OnNavigateToQrScanner -> {
                navigation.onNavigateToQRScanner(
                    onResult = { qrData ->
                        _state.update { currentState ->
                            currentState.copy(
                                skuNumber = qrData
                            )
                        }
                    }
                )
            }

            OnPop -> {
                navigation.onPop()
            }


        }
    }

    private fun onAddStockEvent(event: OnAddStockEvent) {
        when (event) {
            OnAddStockEvent.OnConfirm -> {
                scope.launch {
                    val stock =
                        state.value
                            .dialogAddStockState
                            .amountAdded
                            .text
                            .toString()
                            .toBigDecimalOrNull()
                            ?: return@launch

                    stockRepository.createStockMovement(
                        productId = product?.id ?: return@launch,
                        amount = Amount(
                            state.value.dialogAddStockState
                                .amountAdded
                                .text
                                .toString()
                                .toBigDecimalOrNull() ?: return@launch
                        ),
                        movementType = StockMovementType.Purchase,
                    )

                    _state.update { currentState ->
                        currentState.copy(
                            isDialogAddStockShown = false,
                            dialogAddStockState = ProductAddEditState.ProductAddStockDialogState()
                        )
                    }

                    snackbarHostState.showSnackbar(
                        getString(
                            Res.string.msg_stock_added,
                            product.name.value,
                            stock.toDisplayText(),
                            product.unit.value
                        )
                    )
                }
            }

            OnAddStockEvent.OnDismiss -> {
                _state.update { currentState ->
                    currentState.copy(
                        isDialogAddStockShown = false
                    )
                }
            }

            OnAddStockEvent.OnShown -> {
                _state.update { currentState ->
                    currentState.copy(
                        isDialogAddStockShown = true
                    )
                }
            }
        }
    }

    private fun buildProduct(id: Int): Product {
        val currentState = state.value
        return Product(
            id = id,
            name = Name(currentState.productName),
            skuNumber = SkuNumber(currentState.skuNumber),
            unit = PosLeUnit(currentState.productUnit),
            buyPrice = Price(BigDecimal(currentState.productModalPrice)),
            sellPrice = Price(BigDecimal(currentState.productSellPrice)),
            stock = Amount(BigDecimal.ZERO),
            imageUri = currentState.productImageUri,
            variants = currentState.variants
        )
    }

    private suspend fun validate(onSuccess: suspend () -> Unit) {
        val errorMessage = getValidationError() ?: run {
            onSuccess()
            return
        }

        snackbarHostState.showSnackbar(getString(errorMessage))
    }

    private fun getValidationError(): StringResource? {
        val currentState = state.value
        val nameError = currentState.validateName(currentState.productName)
        val unitError = currentState.validateUnit(currentState.productUnit)
        val modalPriceError = currentState.validatePrice(currentState.productModalPrice)
        val sellPriceError = currentState.validatePrice(currentState.productSellPrice)

        return when {
            nameError != null -> nameError

            unitError != null -> unitError

            modalPriceError != null -> modalPriceError

            sellPriceError != null -> sellPriceError

            else -> null
        }
    }
}