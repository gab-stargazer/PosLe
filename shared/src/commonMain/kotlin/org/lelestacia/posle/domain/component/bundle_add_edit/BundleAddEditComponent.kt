package org.lelestacia.posle.domain.component.bundle_add_edit

import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.StringResource
import org.lelestacia.posle.domain.model.Product
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.msg_error_price_cannot_be_empty
import posle.shared.generated.resources.msg_error_quantity_cannot_be_empty

interface BundleAddEditComponent {
    val state: StateFlow<BundleAddEditState>
    fun onEvent(event: BundleAddEditEvent)
}

sealed interface BundleAddEditEvent {
    data class OnBundleNameChanged(val newBundleName: String) : BundleAddEditEvent
    data class OnProductNameChanged(val newSearchQuery: String) : BundleAddEditEvent
    data class OnBundleProductRemoved(val product: Product) : BundleAddEditEvent
    data class OnBundleProductAdded(val product: Product) : BundleAddEditEvent
    data object OnBundleAddClicked : BundleAddEditEvent

    sealed interface BundleProductEvent : BundleAddEditEvent {
        data class OnQuantityChanged(val index: Int, val newQuantity: String) : BundleProductEvent
        data class OnSellPriceChanged(val index: Int, val newSellPrice: String) : BundleProductEvent
    }
}

data class BundleAddEditState(
    val bundleName: String = "",
    val bundleNameError: StringResource? = null,
    val productName: String = "",
    val bundleProducts: List<BundleProductState> = emptyList(),
    val availableProducts: List<Product> = emptyList()
)

data class BundleProductState(
    val product: Product,
    val quantity: String = "",
    val quantityError: StringResource? = null,
    val sellPrice: String = "",
    val sellPriceError: StringResource? = null
)

fun BundleProductState.validate(): BundleProductState? {
    val quantityError = when {
        quantity.isBlank() || (quantity.toFloatOrNull() ?: 0F) == 0F -> Res.string.msg_error_quantity_cannot_be_empty
        else -> null
    }

    val sellPriceError = when {
        sellPrice.isBlank() -> Res.string.msg_error_price_cannot_be_empty
        else -> null
    }

    val listError = listOf(quantityError, sellPriceError)
    return if (listError.any { it != null }) {
        copy(
            quantityError = quantityError,
            sellPriceError = sellPriceError
        )
    } else {
        null
    }
}