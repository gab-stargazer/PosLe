package org.lelestacia.posle.domain.component.bundle_add_edit

import androidx.compose.material3.SnackbarHostState
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.lelestacia.posle.domain.model.Bundle
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.repository.BundleRepository
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.navigation.AddEdit
import org.lelestacia.posle.navigation.AddEdit.Add
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.coroutineScope
import org.lelestacia.posle.util.toDisplayText
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.msg_error_bundle_must_contain_at_least_one_item

class BundleAddEditComponentImpl(
    componentContext: ComponentContext,
    bundle: Bundle? = null,
    private val mode: AddEdit,
    private val snackbarHostState: SnackbarHostState,
    private val productRepository: ProductRepository,
    private val bundleRepository: BundleRepository,
    private val onDone: () -> Unit
) : ComponentContext by componentContext, BundleAddEditComponent {

    private val scope = coroutineScope(Dispatchers.Main.immediate)

    private val searchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    private val products = searchQuery.flatMapLatest { searchQuery ->
        productRepository.readAvailableProducts(searchQuery)
    }

    private val _state = MutableStateFlow(
        BundleAddEditState(
            mode = mode,
            bundleId = bundle?.id ?: 0,
            bundleName = bundle?.name?.value ?: "",
            bundleImageUri = bundle?.imageUri,
            bundleProducts = bundle?.bundleProducts?.map { bp ->
                BundleProductState(
                    product = Product(
                        id = bp.productId,
                        name = bp.productName,
                        stock = org.lelestacia.posle.util.Amount(0f),
                        buyPrice = bp.buyPrice,
                        sellPrice = bp.sellPriceIndividual,
                        unit = bp.unit,
                        skuNumber = bp.skuNumber,
                        imageUri = bp.imageUri,
                        variants = emptyList(),
                        categories = emptyList()
                    ),
                    quantity = bp.quantity.value.toDisplayText(),
                    sellPrice = bp.sellPrice.value.toPlainString()
                )
            } ?: emptyList()
        )
    )
    override val state: StateFlow<BundleAddEditState> =
        combine(
            flow = _state,
            flow2 = products,
            flow3 = searchQuery
        ) { state, products, searchQuery ->
            state.copy(
                availableProducts = products,
                productName = searchQuery
            )
        }.stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = BundleAddEditState()
        )

    override fun onEvent(event: BundleAddEditEvent) {
        when (event) {
            is BundleAddEditEvent.OnBundleNameChanged -> {
                _state.update { currentState ->
                    currentState.copy(
                        bundleName = event.newBundleName,
                        bundleNameError = null
                    )
                }
            }

            is BundleAddEditEvent.OnProductNameChanged -> {
                searchQuery.update { event.newSearchQuery }
            }

            is BundleAddEditEvent.OnImageChanged -> {
                _state.update { currentState ->
                    currentState.copy(
                        bundleImageUri = event.uri,
                        bundleImageByteArray = event.bytes
                    )
                }
            }

            BundleAddEditEvent.OnDeleteBundleClicked -> {
                _state.update { currentState ->
                    currentState.copy(
                        isDeleteConfirmationShown = true
                    )
                }
            }

            BundleAddEditEvent.OnDeleteConfirmationDismissed -> {
                _state.update { currentState ->
                    currentState.copy(
                        isDeleteConfirmationShown = false
                    )
                }
            }

            BundleAddEditEvent.OnDeleteConfirmed -> {
                scope.launch {
                    bundleRepository.deleteBundle(
                        bundleId = state.value.bundleId,
                        bundleName = Name(state.value.bundleName)
                    )
                    onDone.invoke()
                }
            }

            BundleAddEditEvent.OnPop -> onDone.invoke()

            is BundleAddEditEvent.OnBundleProductAdded -> {
                searchQuery.update { "" }

                _state.update { currentState ->
                    val products = currentState.bundleProducts.toMutableList()
                    if (products.any { it.product.id == event.product.id }) return@update currentState

                    products.add(
                        BundleProductState(product = event.product)
                    )

                    currentState.copy(
                        bundleProducts = products.toList()
                    )
                }
            }

            is BundleAddEditEvent.OnBundleProductRemoved -> {
                _state.update { currentState ->
                    val products = currentState.bundleProducts.toMutableList()
                    products.removeIf { it.product == event.product }

                    currentState.copy(
                        bundleProducts = products.toList()
                    )
                }
            }

            BundleAddEditEvent.OnBundleAddClicked -> {
                if (state.value.bundleProducts.isEmpty()) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            getString(Res.string.msg_error_bundle_must_contain_at_least_one_item)
                        )
                    }
                    return
                }

                val error = state.value
                    .bundleProducts
                    .mapNotNull { it.validate() }

                if (error.isNotEmpty()) {
                    var bundleProducts = state.value.bundleProducts
                    bundleProducts = bundleProducts.map { bundleProduct ->
                        if (error.any { it.product == bundleProduct.product }) {
                            bundleProduct.copy(
                                quantityError = error.first { it.product == bundleProduct.product }.quantityError,
                                sellPriceError = error.first { it.product == bundleProduct.product }.sellPriceError
                            )
                        } else bundleProduct
                    }


                    _state.update { currentState ->
                        currentState.copy(
                            bundleProducts = bundleProducts
                        )
                    }

                    println(state.value.bundleProducts)
                    return
                }

                scope.launch {
                    if (state.value.mode == Add) {
                        bundleRepository.insertBundle(
                            bundleName = Name(state.value.bundleName),
                            bundleProducts = state.value.bundleProducts,
                            imageByteArray = state.value.bundleImageByteArray
                        )
                    } else {
                        bundleRepository.updateBundle(
                            bundleId = state.value.bundleId,
                            bundleName = Name(state.value.bundleName),
                            bundleProducts = state.value.bundleProducts,
                            imageUri = state.value.bundleImageUri,
                            imageByteArray = state.value.bundleImageByteArray
                        )
                    }
                    onDone.invoke()
                }
            }

            is BundleAddEditEvent.BundleProductEvent -> onBundleProductEvent(event)
        }
    }

    private fun onBundleProductEvent(event: BundleAddEditEvent.BundleProductEvent) {
        when (event) {
            is BundleAddEditEvent.BundleProductEvent.OnQuantityChanged -> {
                _state.update { currentState ->
                    val bundleProducts = currentState.bundleProducts.mapIndexed { index, state ->
                        if (index == event.index) {
                            state.copy(
                                quantity = event.newQuantity,
                                quantityError = null
                            )
                        } else state
                    }

                    currentState.copy(
                        bundleProducts = bundleProducts
                    )
                }
            }

            is BundleAddEditEvent.BundleProductEvent.OnSellPriceChanged -> {
                _state.update { currentState ->
                    val bundleProducts = currentState.bundleProducts.mapIndexed { index, state ->
                        if (index == event.index) {
                            state.copy(
                                sellPrice = event.newSellPrice,
                                sellPriceError = null
                            )
                        } else state
                    }

                    currentState.copy(
                        bundleProducts = bundleProducts
                    )
                }
            }
        }
    }
}