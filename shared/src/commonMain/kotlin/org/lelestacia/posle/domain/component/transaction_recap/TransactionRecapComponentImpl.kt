package org.lelestacia.posle.domain.component.transaction_recap

import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DateRangePickerState
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.lelestacia.posle.data.SettingManager
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.repository.TransactionRepository
import org.lelestacia.posle.domain.state_event.TransactionRecapEvent
import org.lelestacia.posle.domain.state_event.TransactionRecapState
import org.lelestacia.posle.util.coroutineScope
import org.lelestacia.posle.util.endOfDayEpochMilliseconds
import org.lelestacia.posle.util.getTodayRangeMilliseconds
import org.lelestacia.posle.util.startOfDayEpochMilliseconds
import kotlin.time.Instant


class TransactionRecapComponentImpl(
    componentContext: ComponentContext,
    private val settingManager: SettingManager,
    private val transactionRepository: TransactionRepository,
    private val onNavigateToTransactionView: (Transaction) -> Unit,
) : ComponentContext by componentContext, TransactionRecapComponent {

    private val scope = coroutineScope(Dispatchers.Main.immediate)
    private val startAndFinishDate = MutableStateFlow(getTodayRangeMilliseconds())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val transaction: Flow<List<Transaction>> = startAndFinishDate
        .flatMapLatest { range ->
            transactionRepository.readTransactionInRange(range.first, range.second)
        }

    private val _state = MutableStateFlow(TransactionRecapState())
    override val state: StateFlow<TransactionRecapState> =
        combine(
            flow = _state,
            flow2 = transaction,
            flow3 = startAndFinishDate,
            flow4 = settingManager.readSettings()
        ) { state, transaction, dateRange, settings ->
            val timeZone = TimeZone.currentSystemDefault()
            val startDate = Instant.fromEpochMilliseconds(dateRange.first)
                .toLocalDateTime(timeZone)
                .date

            val endDate = Instant.fromEpochMilliseconds(dateRange.second)
                .minus(1, DateTimeUnit.NANOSECOND, timeZone)
                .toLocalDateTime(timeZone)
                .date

            state.copy(
                transactionHistory = transaction,
                startDate = dateRange.first,
                finishDate = dateRange.second,
                isSameDay = startDate == endDate,
                settings = settings
            )
        }.stateIn(
            scope,
            SharingStarted.Lazily,
            TransactionRecapState()
        )

    override fun onEvent(event: TransactionRecapEvent) {
        when (event) {
            is TransactionRecapEvent.OnPrimaryTabChanged -> {
                _state.update { currentState ->
                    currentState.copy(
                        selectedPrimaryTab = event.newIndex
                    )
                }
            }

            is TransactionRecapEvent.OnNavigateToTransactionView -> {
                onNavigateToTransactionView(
                    event.transaction
                )
            }

            is TransactionRecapEvent.OnDateRangeChanged -> {
                startAndFinishDate.update {
                    Pair(
                        event.dateRange.first.startOfDayEpochMilliseconds(),
                        event.dateRange.second.endOfDayEpochMilliseconds()
                    )
                }
            }

            is TransactionRecapEvent.OnDateRangePickerVisibilityChanged -> {
                _state.update { currentState ->
                    currentState.copy(
                        isDateRangePickerShown = event.isShown,
                        dateRangePickerState =
                            DateRangePickerState(
                                locale = CalendarLocale.forLanguageTag("id-ID"),
                                initialSelectedStartDateMillis = state.value.startDate,
                                initialSelectedEndDateMillis = state.value.finishDate
                            )
                    )
                }
            }
        }
    }
}