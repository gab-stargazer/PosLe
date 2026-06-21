package org.lelestacia.posle.domain.state_event.product_add

import androidx.compose.foundation.text.input.TextFieldState
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.navigation.AddEdit
import org.lelestacia.posle.navigation.Config

data class ProductAddEditState(
    val name: TextFieldState = TextFieldState(),
    val unit: TextFieldState = TextFieldState(),
    val price: TextFieldState = TextFieldState(),
    val variants: List<Variant> = emptyList(),

    //  Image Section
    val productImageUri: String? = null,
    val productImageByteArray: ByteArray? = null,

    //  Mode
    val mode: AddEdit,
)

sealed interface ProductAddEditEvent {
    data class OnImageChanged(val uri: String?, val bytes: ByteArray?) : ProductAddEditEvent
    data class OnVariantSelected(val variants: List<Variant>) : ProductAddEditEvent
    data object OnAddProductClicked : ProductAddEditEvent
    data object OnDeleteProductClicked : ProductAddEditEvent

    sealed interface Navigation : ProductAddEditEvent {
        data class OnNavigateToVariantView(val config: Config.VariantView) : Navigation
        data object OnPop : Navigation
    }
}

interface ProductAddEditNavigation {
    fun onPop()

    fun onNavigateToVariantSelection(
        config: Config.VariantView,
        onResult: (List<Variant>) -> Unit
    )
}