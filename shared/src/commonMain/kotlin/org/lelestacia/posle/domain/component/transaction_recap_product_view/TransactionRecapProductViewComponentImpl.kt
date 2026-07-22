package org.lelestacia.posle.domain.component.transaction_recap_product_view

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.lelestacia.posle.navigation.Config.TransactionRecapProductItem

class TransactionRecapProductViewComponentImpl(
    componentContext: ComponentContext,
    products: List<TransactionRecapProductItem>,
    private val onPop: () -> Unit
) : ComponentContext by componentContext, TransactionRecapProductViewComponent {

    override val state: StateFlow<TransactionRecapProductViewState> =
        MutableStateFlow(
            TransactionRecapProductViewState(
                transactionProducts = products
            )
        ).asStateFlow()

    override fun onEvent(event: TransactionRecapProductViewEvent) {
        when (event) {
            TransactionRecapProductViewEvent.OnPop -> onPop.invoke()
        }
    }
}