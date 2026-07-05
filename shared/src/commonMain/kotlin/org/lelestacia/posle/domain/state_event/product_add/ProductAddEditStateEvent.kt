package org.lelestacia.posle.domain.state_event.product_add

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import org.lelestacia.posle.domain.model.ProductPriceHistory
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.navigation.AddEdit
import org.lelestacia.posle.navigation.Config

@Immutable
data class ProductAddEditState(
    val name: TextFieldState = TextFieldState(),
    val unit: TextFieldState = TextFieldState(),
    val skuNumber: String = "",
    val buyPriceState: TextFieldState = TextFieldState(),
    val sellPriceState: TextFieldState = TextFieldState(),
    val isSellPriceAndBuyPriceTheSame: Boolean = false,
    val variants: List<Variant> = emptyList(),

    //  Price History
    val buyPriceHistory: List<ProductPriceHistory> = emptyList(),
    val sellPriceHistory: List<ProductPriceHistory> = emptyList(),

    //  Image Section
    val productImageUri: String? = null,
    val productImageByteArray: ByteArray? = null,

    //  Dialog
    val isDialogAddStockShown: Boolean = false,
    val dialogAddStockState: ProductAddStockDialogState = ProductAddStockDialogState(),

    //  Mode
    val mode: AddEdit,
) {
    @Immutable
    data class ProductAddStockDialogState(
        val amountAdded: TextFieldState = TextFieldState(),
    )
}

sealed interface ProductAddEditEvent {
    data class OnSellPriceTheSameAsBuyPriceCheckedChange(val newState: Boolean) :
        ProductAddEditEvent

    data class OnImageChanged(val uri: String?, val bytes: ByteArray?) : ProductAddEditEvent
    data class OnVariantSelected(val variants: List<Variant>) : ProductAddEditEvent
    data object OnAddProductClicked : ProductAddEditEvent
    data object OnDeleteProductClicked : ProductAddEditEvent

    sealed interface Navigation : ProductAddEditEvent {
        data class OnNavigateToVariantView(val config: Config.VariantView) : Navigation
        data object OnNavigateToQrScanner : Navigation
        data object OnPop : Navigation
    }

    sealed interface OnAddStockEvent : ProductAddEditEvent {
        data object OnShown : OnAddStockEvent
        data object OnDismiss : OnAddStockEvent
        data object OnConfirm : OnAddStockEvent
    }
}

interface ProductAddEditNavigation {
    fun onPop()

    fun onNavigateToVariantSelection(
        config: Config.VariantView,
        onResult: (List<Variant>) -> Unit
    )

    fun onNavigateToQRScanner(
        config: Config.QrScanner,
        onResult: (String) -> Unit
    )
}