package org.lelestacia.posle.domain.state_event.product_add

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.StringResource
import org.lelestacia.posle.domain.model.ProductPriceHistory
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.navigation.AddEdit
import org.lelestacia.posle.navigation.Config
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.msg_error_name_cannot_be_empty
import posle.shared.generated.resources.msg_error_price_cannot_be_empty
import posle.shared.generated.resources.msg_error_price_cannot_contain_alphabet
import posle.shared.generated.resources.msg_error_unit_cannot_be_empty

@Immutable
data class ProductAddEditState(
    val productName: String = "",
    val productNameError: String? = null,

    val skuNumber: String = "",

    val productUnit: String = "",
    val productUnitError: String? = null,

    val productModalPrice: String = "",
    val productModalPriceError: String? = null,

    val productSellPrice: String = "",
    val productSellPriceError: String? = null,


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
        val buyPrice: TextFieldState = TextFieldState(),
    )

    fun validateName(productName: String): StringResource? {
        return when {
            productName.isBlank() -> Res.string.msg_error_name_cannot_be_empty
            else -> null
        }
    }

    fun validateUnit(productUnit: String): StringResource? {
        return when {
            productUnit.isBlank() -> Res.string.msg_error_unit_cannot_be_empty
            else -> null
        }
    }

    fun validatePrice(price: String): StringResource? {
        return when {
            price.isBlank() -> Res.string.msg_error_price_cannot_be_empty
            price.toBigDecimalOrNull() == null -> Res.string.msg_error_price_cannot_contain_alphabet
            else -> null
        }
    }
}

sealed interface ProductAddEditEvent {


    data class OnProductNameChange(val newProductName: String) : ProductAddEditEvent
    data object OnProductNameRequestValidation : ProductAddEditEvent
    data class OnProductUnitChange(val newProductUnit: String) : ProductAddEditEvent
    data object OnProductUnitRequestValidation : ProductAddEditEvent
    data class OnProductModalPriceChange(val newModalPrice: String) : ProductAddEditEvent
    data object OnProductModalPriceRequestValidation : ProductAddEditEvent
    data class OnProductSellPriceChange(val newSellPrice: String) : ProductAddEditEvent
    data object OnProductSellPriceRequestValidation : ProductAddEditEvent
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
        onResult: (String) -> Unit
    )
}