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
import org.lelestacia.posle.navigation.AddEdit.Edit
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.SkuNumber
import org.lelestacia.posle.util.UuidProvider
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
        .getProductBuyPriceHistory(product?.id ?: "")

    private val sellPriceHistory = productRepository
        .getProductSellPriceHistory(product?.id ?: "")

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
                        _state.update {
                            it.copy(
                                productName = event.newProductName,
                                productNameError = productNameError?.let { res -> getString(res) },
                                isProductNameValidated = productNameError == null
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                productName = event.newProductName,
                                isProductNameValidated = false
                            )
                        }
                    }
                }
            }

            ProductAddEditEvent.OnProductNameRequestValidation -> {
                scope.launch {
                    val currentState = state.value
                    val productNameError = currentState.validateName(currentState.productName)
                    _state.update {
                        it.copy(
                            productNameError = productNameError?.let { res -> getString(res) },
                            isProductNameValidated = productNameError == null
                        )
                    }
                }
            }

            is ProductAddEditEvent.OnProductUnitChange -> {
                scope.launch {
                    val currentState = state.value
                    if (currentState.productUnitError != null) {
                        val productUnitError = currentState.validateUnit(event.newProductUnit)
                        _state.update {
                            it.copy(
                                productUnit = event.newProductUnit,
                                productUnitError = productUnitError?.let { res -> getString(res) },
                                isProductUnitValidated = productUnitError == null
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                productUnit = event.newProductUnit,
                                isProductUnitValidated = false
                            )
                        }
                    }
                }
            }

            ProductAddEditEvent.OnProductUnitRequestValidation -> {
                scope.launch {
                    val currentState = state.value
                    val productUnitError = currentState.validateUnit(currentState.productUnit)
                    _state.update {
                        it.copy(
                            productUnitError = productUnitError?.let { res -> getString(res) },
                            isProductUnitValidated = productUnitError == null
                        )
                    }
                }
            }

            is ProductAddEditEvent.OnProductModalPriceChange -> {
                scope.launch {
                    val currentState = state.value
                    if (currentState.productModalPriceError != null) {
                        val productModalPriceError =
                            currentState.validatePrice(event.newModalPrice)
                        _state.update {
                            it.copy(
                                productModalPrice = event.newModalPrice,
                                productModalPriceError = productModalPriceError?.let { res -> getString(res) },
                                isProductModalPriceValidated = productModalPriceError == null
                            )
                        }
                    } else if (event.newModalPrice.all { it.isDigit() }) {
                        _state.update {
                            it.copy(
                                productModalPrice = event.newModalPrice,
                                isProductModalPriceValidated = false
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
                    _state.update {
                        it.copy(
                            productModalPriceError = productModalPriceError?.let { res -> getString(res) },
                            isProductModalPriceValidated = productModalPriceError == null
                        )
                    }
                }
            }

            is ProductAddEditEvent.OnProductSellPriceChange -> {
                scope.launch {
                    val currentState = state.value
                    if (currentState.productSellPriceError != null) {
                        val productSellPriceError = currentState
                            .validatePrice(event.newSellPrice)

                        _state.update {
                            it.copy(
                                productSellPrice = event.newSellPrice,
                                productSellPriceError = productSellPriceError?.let { res -> getString(res) },
                                isProductSellPriceValidated = productSellPriceError == null
                            )
                        }
                    } else if (event.newSellPrice.all { it.isDigit() }) {
                        _state.update {
                            it.copy(
                                productSellPrice = event.newSellPrice,
                                isProductSellPriceValidated = false
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
                    _state.update {
                        it.copy(
                            productSellPriceError = productSellPriceError?.let { res -> getString(res) },
                            isProductSellPriceValidated = productSellPriceError == null
                        )
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
                                product = buildProduct(id = UuidProvider.newUuid()),
                                imageByteArray = state.value.productImageByteArray
                            )
                        }

                        Edit -> {
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

    private fun buildProduct(id: String): Product {
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
}