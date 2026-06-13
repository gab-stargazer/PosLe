package org.lelestacia.posle.domain.state_event

import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.domain.model.Transaction

data class TransactionViewState(
    val transaction: Transaction,

    val settings: PosLeSettings = PosLeSettings(),
)

sealed interface TransactionViewEvent {
    data object OnRecapClicked: TransactionViewEvent
}