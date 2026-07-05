package org.lelestacia.posle.domain.component.product_add_edit

import com.arkivanov.decompose.value.Value
import org.lelestacia.posle.domain.state_event.product_add.ProductVariantViewEvent
import org.lelestacia.posle.domain.state_event.product_add.ProductVariantViewState

interface ProductAddVariantsViewComponent {
    val state: Value<ProductVariantViewState>
    fun onEvent(event: ProductVariantViewEvent)
}
