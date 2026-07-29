package org.lelestacia.posle.domain.component

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.lelestacia.posle.data.SettingManager
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.repository.TransactionRepository
import org.lelestacia.posle.domain.state_event.TransactionSearchEvent
import org.lelestacia.posle.domain.state_event.TransactionSearchState
import org.lelestacia.posle.navigation.Config
import org.lelestacia.posle.util.coroutineScope
import kotlin.time.Duration.Companion.milliseconds

class TransactionSearchComponentImpl(
    componentContext: ComponentContext,
    settingManager: SettingManager,
    private val repository: TransactionRepository,
    private val onNavigate: (Config) -> Unit,
    private val onPop: () -> Unit,
) : ComponentContext by componentContext, TransactionSearchComponent {

    private val scope = coroutineScope(Dispatchers.Main.immediate)

    private val _searchQuery = MutableStateFlow("")

    override val state: StateFlow<TransactionSearchState> = combine(
        flow = _searchQuery,
        flow2 = settingManager.getSettings()
    ) { searchQuery, settings ->
        TransactionSearchState(
            searchQuery = searchQuery,
            settings = settings
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = TransactionSearchState()
    )

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    override val searchResults: Flow<PagingData<Transaction>> = _searchQuery
        .debounce(300.milliseconds)
        .distinctUntilChanged()
        .flatMapLatest { newQuery ->
            repository.getTransactionsByQuery(newQuery)
        }
        .cachedIn(scope)

    override fun onEvent(event: TransactionSearchEvent) {
        when (event) {
            is TransactionSearchEvent.OnSearchQueryChange -> _searchQuery.update { event.newQuery }
            is TransactionSearchEvent.OnNavigateTo -> onNavigate(event.config)
            TransactionSearchEvent.OnPop -> onPop()
        }
    }
}
