package org.lelestacia.posle.domain.component

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.lelestacia.posle.data.SettingManager
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.repository.TransactionRepository
import org.lelestacia.posle.navigation.Config
import org.lelestacia.posle.util.coroutineScope

class TransactionHistoryComponentImpl(
    componentContext: ComponentContext,
    private val repository: TransactionRepository,
    private val onNavigation: (Config) -> Unit,
    settingManager: SettingManager,
) : ComponentContext by componentContext, TransactionHistoryComponent {

    private val scope = coroutineScope(Dispatchers.Main.immediate)

    override val allHistory: Flow<PagingData<Transaction>>
        get() = repository
            .readTransactionHistory()
            .cachedIn(scope)

    override val unRecappedHistory: Flow<PagingData<Transaction>>
        get() = repository
            .readUnRecappedTransactionHistory()
            .cachedIn(scope)

    private val _state = MutableStateFlow(TransactionHistoryScreenState())

    override val state: StateFlow<TransactionHistoryScreenState> = combine(
        flow = _state,
        flow2 = settingManager.readSettings(),
        flow3 = repository.readTodayTransactionHistory()
    ) { state, settings, todayTransactions ->
        TransactionHistoryScreenState(
            todayTransactions = todayTransactions,
            selectedTab = state.selectedTab,
            settings = settings
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = TransactionHistoryScreenState()
    )

    override fun onEvent(event: TransactionHistoryScreenEvent) {
        when (event) {
            is TransactionHistoryScreenEvent.OnNavigate -> onNavigation(event.config)
            is TransactionHistoryScreenEvent.OnTabSelected -> _state.update { currentState ->
                currentState.copy(
                    selectedTab = event.selectedTab
                )
            }
        }
    }
}
