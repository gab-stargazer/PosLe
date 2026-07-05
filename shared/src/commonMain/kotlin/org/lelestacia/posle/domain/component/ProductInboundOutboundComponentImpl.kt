package org.lelestacia.posle.domain.component

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.lelestacia.posle.data.SettingManager
import org.lelestacia.posle.data.entity.StockMovementType
import org.lelestacia.posle.domain.component.ProductInboundOutboundComponentEvent.ProductInboundOutboundAddStockEvent.OnAddStockClicked
import org.lelestacia.posle.domain.component.ProductInboundOutboundComponentEvent.ProductInboundOutboundAddStockEvent.OnProductNameChanged
import org.lelestacia.posle.domain.component.ProductInboundOutboundComponentEvent.ProductInboundOutboundAddStockEvent.OnProductSelected
import org.lelestacia.posle.domain.component.ProductInboundOutboundComponentEvent.ProductInboundOutboundAddStockEvent.OnToggleDialog
import org.lelestacia.posle.domain.component.ProductInboundOutboundComponentState.ProductInboundOutboundAddStockState
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.StockMovement
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.domain.repository.StockRepository
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.coroutineScope

@OptIn(FlowPreview::class)
class ProductInboundOutboundComponentImpl(
    componentContext: ComponentContext,
    settingManager: SettingManager,
    private val stockRepository: StockRepository,
    private val productRepository: ProductRepository
) : ComponentContext by componentContext, ProductInboundOutboundComponent {

    private val scope = coroutineScope(Dispatchers.Main.immediate)

    private val searchQuery = MutableStateFlow("")
    private val availableProducts = searchQuery
        .flatMapLatest { query ->
            productRepository.readAvailableProducts(query)
        }

    private val _settings = settingManager.readSettings()

    override val products: Flow<PagingData<Product>> = productRepository
        .readProducts("")
        .cachedIn(scope)

    private val _state = MutableStateFlow(ProductInboundOutboundComponentState())

    override val state: StateFlow<ProductInboundOutboundComponentState> = combine(
        flow = _state,
        flow2 = availableProducts,
        flow3 = _settings
    ) { state, products, settings ->
        state.copy(
            addStockState = state.addStockState.copy(
                availableProducts = products
            ),
            settingState = settings
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = ProductInboundOutboundComponentState()
    )

    override val priceMovement: Flow<PagingData<StockMovement>> =
        stockRepository
            .readStockMovement()
            .cachedIn(scope)

    override fun onEvent(event: ProductInboundOutboundComponentEvent) {
        when (event) {
            is OnProductNameChanged -> {
                searchQuery.update { event.newProductName.value }
                _state.update { currentState ->
                    currentState.copy(
                        addStockState = currentState.addStockState.copy(
                            productName = event.newProductName
                        )
                    )
                }
            }

            OnToggleDialog -> _state.update { currentState ->
                currentState.copy(
                    isAddStockShown = !currentState.isAddStockShown,
                    addStockState = ProductInboundOutboundAddStockState()
                )
            }

            OnAddStockClicked -> {
                val currentState = _state.value.addStockState
                val product = currentState.selectedProduct ?: return
                val amountString = currentState.amountAdded.text.toString()
                val amount = amountString.toFloatOrNull() ?: return

                scope.launch {
                    stockRepository.addStock(
                        productId = product.id,
                        amount = Amount(amount),
                        movementType = StockMovementType.Inbound
                    )

                    onEvent(OnToggleDialog)
                }
            }

            is OnProductSelected -> _state.update { currentState ->
                currentState.copy(
                    addStockState = currentState.addStockState.copy(
                        selectedProduct = event.product,
                        productName = event.product.name
                    )
                )
            }

            is ProductInboundOutboundComponentEvent.OnTabSelected -> _state.update { currentState ->
                currentState.copy(selectedTab = event.index)
            }
        }
    }
}
