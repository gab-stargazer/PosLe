package org.lelestacia.posle.domain.component

import androidx.compose.material3.SnackbarHostState
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.lelestacia.posle.data.SettingManager
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.repository.TransactionRepository
import org.lelestacia.posle.domain.state_event.TransactionViewEvent
import org.lelestacia.posle.domain.state_event.TransactionViewState
import org.lelestacia.posle.util.coroutineScope

class TransactionViewComponentImpl(
    componentContext: ComponentContext,
    transaction: Transaction,
    settingManager: SettingManager,
    private val snackbarHostState: SnackbarHostState,
    private val repository: TransactionRepository,
    private val onNavigation: (TransactionViewNavigation) -> Unit
) : ComponentContext by componentContext, TransactionViewComponent {

    private val scope = coroutineScope(Dispatchers.Main.immediate)
    private val settings = settingManager.readSettings()
    private val _state = MutableStateFlow(
        TransactionViewState(
            transaction = transaction
        )
    )
    
    override val state: StateFlow<TransactionViewState> = combine(
        flow = settings,
        flow2 = _state
    ) { settings, state ->
        state.copy(
            settings = settings
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = TransactionViewState(transaction = transaction)
    )

    override fun onEvent(event: TransactionViewEvent) {
        scope.launch {
            when (event) {
                TransactionViewEvent.OnRecapClicked -> {
                    _state.update { currentState ->
                        currentState.copy(
                            transaction = currentState.transaction.copy(
                                isRecapped = true
                            )
                        )
                    }

                    repository.updateTransaction(
                        state.value.transaction.copy(
                            isRecapped = true
                        )
                    )
                }

                is TransactionViewEvent.OnNavigateTo -> onNavigation(event.navigation)

                is TransactionViewEvent.OnShowMessage -> snackbarHostState.showSnackbar(
                    message = event.message
                )

                is TransactionViewEvent.OnChangeSaveLoadingState -> _state.update { currentState ->
                    currentState.copy(
                        isSaveProcessing = event.isLoading
                    )
                }
            }
        }
    }
}
