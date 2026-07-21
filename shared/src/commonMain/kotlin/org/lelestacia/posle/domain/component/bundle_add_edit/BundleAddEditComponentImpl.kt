package org.lelestacia.posle.domain.component.bundle_add_edit

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
import org.lelestacia.posle.domain.repository.BundleRepository
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.coroutineScope

class BundleAddEditComponentImpl(
    componentContext: ComponentContext,
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

    private val _state = MutableStateFlow(BundleAddEditState())
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

            is BundleAddEditEvent.OnBundleProductAdded -> {
                searchQuery.update { "" }

                _state.update { currentState ->
                    val products = currentState.bundleProducts.toMutableList()
                    if (products.any { it.product == event.product }) return@update currentState

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
                val error = state.value
                    .bundleProducts
                    .mapNotNull { it.validate() }

                if (error.size > 0) {
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
                    bundleRepository.insertBundle(
                        bundleName = Name(state.value.bundleName),
                        bundleProducts = state.value.bundleProducts
                    ).also { onDone.invoke() }
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