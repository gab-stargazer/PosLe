package org.lelestacia.posle.domain.state_event

import org.lelestacia.posle.domain.model.Transaction

data class TransactionViewState(
    val transaction: Transaction,
)