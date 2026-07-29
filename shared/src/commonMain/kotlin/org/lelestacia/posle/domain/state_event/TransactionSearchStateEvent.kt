package org.lelestacia.posle.domain.state_event

import androidx.compose.runtime.Immutable
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.navigation.Config

@Immutable
data class TransactionSearchState(
    val searchQuery: String = "",
    val settings: PosLeSettings = PosLeSettings(),
)

sealed interface TransactionSearchEvent {
    data class OnSearchQueryChange(val newQuery: String) : TransactionSearchEvent
    data class OnNavigateTo(val config: Config) : TransactionSearchEvent
    data object OnPop : TransactionSearchEvent
}