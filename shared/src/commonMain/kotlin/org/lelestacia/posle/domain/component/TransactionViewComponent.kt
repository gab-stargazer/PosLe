package org.lelestacia.posle.domain.component

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.lelestacia.posle.data.SettingManager
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.state_event.TransactionViewState

class TransactionViewComponent(
    componentContext: ComponentContext,
    transaction: Transaction,
    settingManager: SettingManager,
    private val onNavigation: (TransactionViewNavigation) -> Unit
) : ComponentContext by componentContext {

    private val scope = CoroutineScope(Dispatchers.Main.immediate)

    private val settings = settingManager.readSettings()
    private val _state = MutableStateFlow(
        TransactionViewState(
            transaction = transaction
        )
    )
    val state = combine(
        flow = settings,
        flow2 = _state
    ) { settings, state ->
        TransactionViewState(
            transaction = state.transaction,
            settings = settings
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = TransactionViewState(transaction = transaction)
    )

    fun onAction(navigation: TransactionViewNavigation) = onNavigation(navigation)
}

sealed interface TransactionViewNavigation {
    data object OnPop : TransactionViewNavigation
}