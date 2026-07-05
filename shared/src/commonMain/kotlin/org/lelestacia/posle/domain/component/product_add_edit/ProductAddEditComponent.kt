package org.lelestacia.posle.domain.component.product_add_edit

import kotlinx.coroutines.flow.StateFlow
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditState

interface ProductAddEditComponent {
    val state: StateFlow<ProductAddEditState>
    fun onEvent(event: ProductAddEditEvent)
}
