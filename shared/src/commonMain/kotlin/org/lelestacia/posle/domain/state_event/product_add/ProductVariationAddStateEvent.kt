package org.lelestacia.posle.domain.state_event.product_add

import androidx.compose.foundation.text.input.TextFieldState

data class ProductVariationAddState(
    val variantNameState: TextFieldState = TextFieldState(),
    val variantPriceState: TextFieldState = TextFieldState(),
)