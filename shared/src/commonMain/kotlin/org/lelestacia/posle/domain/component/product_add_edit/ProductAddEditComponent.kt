package org.lelestacia.posle.domain.component.product_add_edit

import kotlinx.coroutines.flow.StateFlow
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditState

/**
 * Component interface for Adding or Editing a Product.
 */
interface ProductAddEditComponent {
    /**
     * The current state of the product form, including name, unit, prices, and variants.
     */
    val state: StateFlow<ProductAddEditState>

    /**
     * Handles UI events such as field updates, variant selection, and saving the product.
     */
    fun onEvent(event: ProductAddEditEvent)
}
