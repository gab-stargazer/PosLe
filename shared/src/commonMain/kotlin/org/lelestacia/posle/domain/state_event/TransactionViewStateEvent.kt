package org.lelestacia.posle.domain.state_event

import androidx.compose.runtime.Immutable
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.domain.component.TransactionViewNavigation
import org.lelestacia.posle.domain.model.Transaction

@Immutable
data class TransactionViewState(
    val transaction: Transaction,

    val isSaveProcessing: Boolean = false,

    val settings: PosLeSettings = PosLeSettings(),
)

sealed interface TransactionViewEvent {
    data class OnChangeSaveLoadingState(val isLoading: Boolean) : TransactionViewEvent
    data class OnShowMessage(val message: String) : TransactionViewEvent
    data object OnRecapClicked : TransactionViewEvent
    data class OnNavigateTo(val navigation: TransactionViewNavigation) : TransactionViewEvent
}