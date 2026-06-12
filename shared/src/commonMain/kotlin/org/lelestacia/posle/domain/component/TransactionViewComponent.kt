package org.lelestacia.posle.domain.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.state_event.TransactionViewState

class TransactionViewComponent(
    componentContext: ComponentContext,
    transaction: Transaction,
    onNavigation: (TransactionViewNavigation) -> Unit
) : ComponentContext by componentContext {

    val state: Value<TransactionViewState>
        field = MutableValue(TransactionViewState(transaction = transaction))
}

sealed interface TransactionViewNavigation {
    data object OnPop : TransactionViewNavigation
}