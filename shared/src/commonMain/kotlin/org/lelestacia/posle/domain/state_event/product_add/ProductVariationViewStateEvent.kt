package org.lelestacia.posle.domain.state_event.product_add

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.lelestacia.posle.domain.model.Variant

data class ProductVariantViewState(

    val selectedVariants: List<Variant> = emptyList(),

    //  Dialog Add Variant
    val addEditVariantDialogState: VariantAddEditState = VariantAddEditState(),
    val isAddEditVariantDialogShown: Boolean = false,

    //  Paging
    val variants: Flow<PagingData<Variant>> = flowOf()
) {

    @Immutable
    data class VariantAddEditState(
        val selectedVariant: Variant? = null,
        val variantNameState: TextFieldState = TextFieldState(),
        val variantPriceState: TextFieldState = TextFieldState(),
    )
}

sealed interface ProductVariantViewEvent {
    data class OnCheckedChange(
        val variant: Variant,
        val isChecked: Boolean
    ) : ProductVariantViewEvent


    data object OnConfirmed : ProductVariantViewEvent
    data object OnCancel : ProductVariantViewEvent

    //  Variant Dialog
    data object OnAddVariant : ProductVariantViewEvent
    data class OnEditVariant(val variant: Variant) : ProductVariantViewEvent
    data object OnVariantDialogDismissed : ProductVariantViewEvent
    data object OnSaveVariant : ProductVariantViewEvent
    data class OnDeleteVariant(val variant: Variant): ProductVariantViewEvent
}