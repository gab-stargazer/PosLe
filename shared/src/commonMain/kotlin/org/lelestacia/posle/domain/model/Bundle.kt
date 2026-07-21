package org.lelestacia.posle.domain.model

import androidx.compose.runtime.Immutable
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.SkuNumber
import org.lelestacia.posle.util.Unit

data class Bundle(
    val id: Int,
    val name: Name,
    val imageUri: String? = null,
    val bundleProducts: List<BundleProduct>,
    val createdAt: Long,
    val updatedAt: Long? = null,
)

@Immutable
data class BundleProduct(
    val productId: Int,
    val productName: Name,
    val skuNumber: SkuNumber?,
    val imageUri: String?,
    val buyPrice: Price,
    val sellPrice: Price,
    val quantity: Amount,
    val unit: Unit,
    val createdAt: Long,
    val updatedAt: Long?,
)

@Immutable
sealed interface CartItems {

    data class ProductCartItem(
        val id: Int,
        val productId: Int,
        val productName: Name,
        val skuNumber: SkuNumber?,
        val imageUri: String?,
        val productQuantity: Amount,
        val productBuyPrice: Price,
        val productSellPrice: Price,
        val productUnit: Unit,
        val productNote: String?,
    ) : CartItems

    data class BundleCartItem(
        val id: Int,
        val bundleId: Int,
        val bundleName: Name,
        val bundleQuantity: Amount,
        val bundleTotalPrice: Price,
        val bundleNote: String?,
        val bundleProducts: List<BundleProduct>
    ) : CartItems
}
