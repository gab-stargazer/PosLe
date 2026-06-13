package org.lelestacia.posle.domain.component

import androidx.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.lelestacia.posle.domain.repository.TransactionRepository
import org.lelestacia.posle.navigation.Config

class TransactionHistoryComponent(
    componentContext: ComponentContext,
    repository: TransactionRepository,
    val onNavigation: (Config) -> Unit,
) : ComponentContext by componentContext {

    val scope = CoroutineScope(Dispatchers.Main.immediate)
    val history = repository
        .readTransactionHistory()
        .cachedIn(scope)
}