package org.lelestacia.posle.domain.component.transaction_add

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.domain.state_event.TransactionAddEvent
import org.lelestacia.posle.domain.state_event.TransactionAddState
import org.lelestacia.posle.domain.state_event.TransactionItemState
import org.lelestacia.posle.navigation.Config

interface TransactionAddNavigation {
    fun onNavigateTo(config: Config, onComplete: () -> Unit = {})

    fun onNavigateToProductConfig(
        product: Product,
        onConfirmed: (TransactionItemState, List<Variant>) -> Unit
    )

    fun onNavigateToQRScanner(
        onResult: (String) -> Unit
    )
}

interface TransactionAddComponent {
    val products: Flow<PagingData<Product>>
    val state: StateFlow<TransactionAddState>
    fun onEvent(event: TransactionAddEvent)
}
