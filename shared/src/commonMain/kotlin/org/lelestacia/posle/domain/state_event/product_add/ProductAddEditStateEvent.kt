package org.lelestacia.posle.domain.state_event.product_add

import androidx.compose.foundation.text.input.TextFieldState
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.navigation.AddEdit

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
): InstanceKeeper.Instance

sealed interface ProductAddEditEvent {
    data class OnImageChanged(val uri: String?, val bytes: ByteArray?) : ProductAddEditEvent
    data class OnVariantSelected(val variants: List<Variant>) : ProductAddEditEvent
    data object OnNavigateToViewVariant : ProductAddEditEvent
    data object OnAddProductClicked : ProductAddEditEvent
    data object OnDeleteProductClicked : ProductAddEditEvent
}
