package org.lelestacia.posle.domain.component.analytics

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.lelestacia.posle.data.SettingManager
import org.lelestacia.posle.domain.analytics.AnalyticsCalculator
import org.lelestacia.posle.domain.analytics.DateRange
import org.lelestacia.posle.domain.analytics.StockAnalyticsCalculator
import org.lelestacia.posle.domain.analytics.StockProductAnalytics
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.domain.repository.StockRepository
import org.lelestacia.posle.domain.repository.TransactionRepository
import org.lelestacia.posle.domain.state_event.AnalyticsEvent
import org.lelestacia.posle.domain.state_event.AnalyticsState
import org.lelestacia.posle.navigation.Config
import org.lelestacia.posle.util.coroutineScope
import java.math.BigDecimal

/**
 * Analytics dashboard component implementation.
 *
 * Combines transactions in the selected range, stock movements in the range and
 * the settings flow (stock tracking flag) into an [AnalyticsState]. Current stock
 * levels for the ranked fast/slow movers are fetched individually via
 * [ProductRepository.getProductAvailability].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsComponentImpl(
    componentContext: ComponentContext,
    private val transactionRepository: TransactionRepository,
    private val stockRepository: StockRepository,
    private val productRepository: ProductRepository,
    private val settingManager: SettingManager,
    private val onNavigation: (Config) -> Unit,
) : ComponentContext by componentContext, AnalyticsComponent {

    private val scope: CoroutineScope = coroutineScope(Dispatchers.Main.immediate)

    private val _state = MutableStateFlow(AnalyticsState())

    override val state: StateFlow<AnalyticsState> = combine(
        _state,
        _state.flatMapLatest { state ->
            transactionRepository.getTransactionsInRange(
                startDate = state.selectedRange.startDate,
                finishDate = state.selectedRange.finishDate
            )
        },
        _state.flatMapLatest { state ->
            stockRepository.getStockMovementsInRange(
                startDate = state.selectedRange.startDate,
                finishDate = state.selectedRange.finishDate
            )
        },
        settingManager.getSettings(),
    ) { state, transactions, movements, settings ->
        val salesResult = AnalyticsCalculator.calculate(
            transactions = transactions,
            range = state.selectedRange
        )
        val rankedMovers = StockAnalyticsCalculator.rankMovers(movements = movements)
        state.copy(
            salesResult = salesResult,
            stockResult = rankedMovers.copy(
                fastMovers = rankedMovers.fastMovers.map { it.withCurrentStockBlocking() },
                slowMovers = rankedMovers.slowMovers.map { it.withCurrentStockBlocking() },
            ),
            isStockTrackingEnabled = settings.isProductStockTracked,
            isLoading = false,
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = AnalyticsState()
    )

    /**
     * Fetches the current stock level for a mover and computes the restock flag.
     */
    private suspend fun StockProductAnalytics.withCurrentStockBlocking(): StockProductAnalytics {
        val currentStock = runCatching {
            productRepository.getProductAvailability(productId)
        }.getOrDefault(BigDecimal.ZERO)
        return copy(
            currentStock = currentStock,
            needsRestock = currentStock.signum() <= 0,
        )
    }

    override fun onEvent(event: AnalyticsEvent) {
        when (event) {
            is AnalyticsEvent.OnDateRangePickerVisibilityChanged -> _state.update { current ->
                current.copy(isDateRangePickerShown = event.isShown)
            }

            is AnalyticsEvent.OnDateRangeChanged -> _state.update { current ->
                current.copy(
                    selectedRange = event.range,
                    isLoading = true,
                )
            }
        }
    }
}
