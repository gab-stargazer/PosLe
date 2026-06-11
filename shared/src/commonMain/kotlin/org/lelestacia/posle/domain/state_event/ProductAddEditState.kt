package org.lelestacia.posle.domain.state_event

import androidx.compose.foundation.text.input.TextFieldState
import org.lelestacia.posle.navigation.AddEdit

data class ProductAddEditState(
    val name: TextFieldState = TextFieldState(),
    val unit: TextFieldState =  TextFieldState(),
    val price: TextFieldState = TextFieldState(),
    val isProductVolatile: Boolean = false,

    //  Image Section
    val productImageUri: String? = null,
    val productImageByteArray: ByteArray? = null,

    //  Mode
    val mode: AddEdit,
)

sealed interface ProductAddEditEvent {
    data class OnImageChanged(val uri: String?, val bytes: ByteArray?): ProductAddEditEvent
    data class OnToggleProductVolatility(val newToggle: Boolean): ProductAddEditEvent
    data object OnAddProductClicked: ProductAddEditEvent
    data object OnDeleteProductClicked: ProductAddEditEvent
}
