package org.lelestacia.posle.domain.component

import androidx.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreateSimple
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.data.SettingManager
import org.lelestacia.posle.domain.repository.TransactionRepository
import org.lelestacia.posle.navigation.Config

class TransactionHistoryComponent(
    componentContext: ComponentContext,
    repository: TransactionRepository,
    settingManager: SettingManager,
    val onNavigation: (Config) -> Unit,
) : ComponentContext by componentContext {

    val scope = instanceKeeper.getOrCreateSimple { CoroutineScope(Dispatchers.Main.immediate) }

    val history = repository
        .readTransactionHistory()
        .cachedIn(scope)

    val settings = settingManager
        .readSettings()
        .stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = PosLeSettings()
        )
}