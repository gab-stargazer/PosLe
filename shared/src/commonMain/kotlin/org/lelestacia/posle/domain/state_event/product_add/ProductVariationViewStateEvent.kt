package org.lelestacia.posle.domain.state_event.product_add

import org.lelestacia.posle.domain.model.Variant

data class ProductVariantViewState(
    val selectedVariant: List<Variant> = emptyList(),
    val currentTab: Int = 0,
)

sealed interface ProductVariantViewEvent {
    data class OnVariantClicked(val variant: Variant) : ProductVariantViewEvent
    data class OnAddVariant(val variant: Variant) : ProductVariantViewEvent
    data class OnTabChanged(val index: Int) : ProductVariantViewEvent
    data object OnConfirmed : ProductVariantViewEvent
}