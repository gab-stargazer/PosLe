package org.lelestacia.posle.domain.component.product_add_edit

import androidx.compose.foundation.text.input.TextFieldState
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
import org.lelestacia.posle.domain.model.Variant
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
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.msg_error_name_cannot_be_empty
import posle.shared.generated.resources.msg_error_price_cannot_be_empty
import posle.shared.generated.resources.msg_error_price_cannot_contain_alphabet
import posle.shared.generated.resources.msg_error_unit_cannot_be_empty
import posle.shared.generated.resources.msg_stock_added
import java.math.BigDecimal
import kotlin.math.roundToInt
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
        .readProductBuyPriceHistory(product?.id ?: 0)

    private val sellPriceHistory = productRepository
        .readProductSellPriceHistory(product?.id ?: 0)

    init {
        if (product != null) {
            scope.launch {
                variantRepository
                    .readVariantByProductId(product.id)
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
            name = TextFieldState(product?.name?.value.orEmpty()),
            unit = TextFieldState(product?.unit?.value.orEmpty()),
            skuNumber = product?.skuNumber?.value.orEmpty(),
            buyPriceState = TextFieldState(product?.buyPrice?.value?.toString() ?: ""),
            sellPriceState = TextFieldState(product?.sellPrice?.value?.toString() ?: ""),
            productImageUri = product?.imageUri,
            variants = product?.variants ?: emptyList(),
            mode = mode
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

            is OnSellPriceTheSameAsBuyPriceCheckedChange -> {
                _state.update { currentState ->
                    currentState.copy(
                        isSellPriceAndBuyPriceTheSame = event.newState
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
                    validate(
                        onSuccess = {
                            when (state.value.mode) {
                                Add -> {
                                    productRepository.addProduct(
                                        product = buildProduct(id = 0),
                                        imageByteArray = state.value.productImageByteArray
                                    )
                                }

                                AddEdit.Edit -> {
                                    val original: Map<Int, Variant> = product
                                        ?.variants
                                        ?.associateBy { it.id }
                                        ?: return@validate

                                    val modified = state
                                        .value
                                        .variants
                                        .associateBy { it.id }

                                    val variantsToAdd =
                                        modified
                                            .filter { it.key !in original }
                                            .map { it.value }

                                    val variantsToRemove =
                                        original
                                            .filter { it.key !in modified }
                                            .map { it.value }

                                    productRepository.updateProduct(
                                        product = buildProduct(id = product.id),
                                        variantsToAdd = variantsToAdd.toList(),
                                        variantsToRemove = variantsToRemove.toList(),
                                        imageByteArray = state.value.productImageByteArray
                                    )
                                }
                            }

                            onNavigationEvent(OnPop)
                        }
                    )
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
                            .toFloatOrNull()
                            ?: return@launch

                    stockRepository.addStock(
                        productId = product?.id ?: return@launch,
                        amount = Amount(
                            state.value.dialogAddStockState
                                .amountAdded
                                .text
                                .toString()
                                .toFloatOrNull() ?: return@launch
                        ),
                        movementType = StockMovementType.Inbound,
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
                            stock.roundToInt().toString(),
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
            name = Name(currentState.name.text.toString()),
            buyPrice = Price(BigDecimal(currentState.buyPriceState.text.toString())),
            sellPrice = Price(BigDecimal(currentState.sellPriceState.text.toString())),
            stock = Amount(0F),
            unit = PosLeUnit(currentState.unit.text.toString()),
            skuNumber = SkuNumber(currentState.skuNumber),
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
        return when {
            currentState.name.text.toString().isBlank() ->
                Res.string.msg_error_name_cannot_be_empty

            currentState.unit.text.toString().isBlank() ->
                Res.string.msg_error_unit_cannot_be_empty

            currentState.buyPriceState.text.toString().isBlank() ->
                Res.string.msg_error_price_cannot_be_empty

            currentState.sellPriceState.text.toString().isBlank() ->
                Res.string.msg_error_price_cannot_be_empty

            currentState.buyPriceState.text.toString().any { it.isLetter() } ->
                Res.string.msg_error_price_cannot_contain_alphabet

            currentState.sellPriceState.text.toString().any { it.isLetter() } ->
                Res.string.msg_error_price_cannot_contain_alphabet

            else -> null
        }
    }
}