package org.lelestacia.posle.domain.state_event

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.domain.model.Bundle
import org.lelestacia.posle.domain.model.CartItems
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.domain.state_event.TransactionAddState.DialogProductState
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.msg_error_price_cannot_be_empty
import posle.shared.generated.resources.msg_error_price_cannot_contain_alphabet
import posle.shared.generated.resources.msg_error_quantity_cannot_be_empty
import posle.shared.generated.resources.msg_error_quantity_exceeded
import java.math.BigDecimal

@Immutable
data class TransactionAddState(
    val searchQuery: String = "",
    val customerName: TextFieldState = TextFieldState(),
    val settings: PosLeSettings = PosLeSettings(),
    val cartItems: List<CartItems> = emptyList(),
    val currentTab: Int = 0,

    val isDialogProductShown: Boolean = false,
    val dialogProductState: DialogProductState = DialogProductState(),
    val isDialogBundleShown: Boolean = false,
    val dialogBundleState: DialogBundleState = DialogBundleState(),
    val isProductNotFoundShown: Boolean = false,
) {
    @Immutable
    data class DialogProductState(
        val selectedProduct: Product? = null,
        val amount: String = "",
        val amountError: String? = null,
        val isAmountValidated: Boolean = false,
        val price: String = "",
        val priceError: String? = null,
        val isPriceValidated: Boolean = false,
        val noteState: TextFieldState = TextFieldState(),
        val settings: PosLeSettings = PosLeSettings()
    ) {
        fun validatePrice(price: String): StringResource? {
            return when {
                price.isBlank() -> Res.string.msg_error_price_cannot_be_empty
                price.toBigDecimalOrNull() == null -> Res.string.msg_error_price_cannot_contain_alphabet
                else -> null
            }
        }
    }

    @Immutable
    data class DialogBundleState(
        val selectedBundle: Bundle? = null,
        val quantity: String = "",
        val quantityError: String? = null,
        val isQuantityValidated: Boolean = false,
        val noteState: TextFieldState = TextFieldState(),
    )
}

suspend fun DialogProductState.validate(alreadyInCart: BigDecimal = BigDecimal.ZERO): DialogProductState {
    val isStockEnabled = settings.isProductStockTracked
    val stock = selectedProduct?.stock?.value ?: throw Exception("Stock is null on Validation")
    val amountAsBigDecimal = amount.toBigDecimalOrNull() ?: BigDecimal.ZERO

    val amountError = when {
        amount.isBlank() -> Res.string.msg_error_quantity_cannot_be_empty
        amountAsBigDecimal == BigDecimal.ZERO -> Res.string.msg_error_quantity_cannot_be_empty
        isStockEnabled && amountAsBigDecimal + alreadyInCart > stock -> Res.string.msg_error_quantity_exceeded
        else -> null
    }

    val priceError = when {
        price.isBlank() && settings.isProductVolatile -> Res.string.msg_error_price_cannot_be_empty
        else -> null
    }

    return this.copy(
        amountError = amountError?.let { getString(it) },
        priceError = priceError?.let { getString(it) }
    )
}

sealed interface TransactionAddEvent {
    data class OnSearchQueryChanged(val query: String) : TransactionAddEvent
    data class OnTabChanged(val index: Int) : TransactionAddEvent
    data class OnRequestProductConfig(val product: Product) : TransactionAddEvent
    data class OnRemoveProduct(val cartItems: CartItems) : TransactionAddEvent
    data object OnAddTransactionClicked : TransactionAddEvent
    data object OnNavigateToQrScanner : TransactionAddEvent
    data object OnProductNotFoundDismissed : TransactionAddEvent

    sealed interface DialogProductEvent : TransactionAddEvent {
        data class OnAmountChanged(val newAmount: String) : DialogProductEvent
        data class OnPriceChanged(val newPrice: String) : DialogProductEvent
        data object OnPriceRequestValidation : DialogProductEvent
        data class OnShown(val selectedProduct: Product) : DialogProductEvent
        data object OnDismiss : DialogProductEvent
        data object OnAddClicked : DialogProductEvent
    }

    sealed interface DialogBundleEvent : TransactionAddEvent {
        data class OnQuantityChanged(val newQuantity: String) : DialogBundleEvent
        data object OnQuantityValidationRequest : DialogBundleEvent
        data class OnShown(val selectedBundle: Bundle) : DialogBundleEvent
        data object OnDismiss : DialogBundleEvent
        data object OnAddToCartClicked : DialogBundleEvent
    }
}

data class TransactionItemState(
    val amountState: TextFieldState = TextFieldState(),
    val priceState: TextFieldState = TextFieldState(),
    val noteState: TextFieldState = TextFieldState(),
    val variants: List<Variant> = emptyList()
)

/**
 * Total units of [productId] already held in [cartItems] — both as a plain
 * product line and inside bundle lines (bundle count × per-bundle quantity).
 *
 * Used to make stock-sufficiency checks cart-aware so that repeated additions
 * of the same product (or overlapping bundles) cannot exceed the stock.
 */
fun cartQuantityInCart(productId: String, cartItems: List<CartItems>): BigDecimal {
    return cartItems.fold(BigDecimal.ZERO) { acc, cartItem ->
        when (cartItem) {
            is CartItems.ProductCartItem ->
                if (cartItem.productId == productId) acc + cartItem.productQuantity.value else acc

            is CartItems.BundleCartItem -> {
                val bundleUnits = cartItem.bundleProducts
                    .filter { it.productId == productId }
                    .fold(BigDecimal.ZERO) { bundleAcc, bundleProduct ->
                        bundleAcc + bundleProduct.quantity.value * cartItem.bundleQuantity.value
                    }
                acc + bundleUnits
            }
        }
    }
}