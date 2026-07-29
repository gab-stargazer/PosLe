package org.lelestacia.posle.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.SkuNumber
import org.lelestacia.posle.util.Unit

/**
 * Domain model representing a product bundle.
 *
 * @property id Unique bundle identifier.
 * @property name Bundle display name.
 * @property imageUri Local path to the bundle image.
 * @property bundleProducts List of products included in the bundle.
 * @property createdAt Creation timestamp.
 * @property updatedAt Last modification timestamp.
 */
@Serializable
data class Bundle(
    val id: Int,
    val name: Name,
    val imageUri: String? = null,
    val bundleProducts: List<BundleProduct>,
    val createdAt: Long,
    val updatedAt: Long? = null,
)

/**
 * Represents a product as part of a bundle.
 */
@Serializable
@Immutable
data class BundleProduct(
    val productId: Int,
    val productName: Name,
    val skuNumber: SkuNumber?,
    val imageUri: String?,

    /**
     * Original buy price of the product.
     */
    val buyPrice: Price,

    /**
     * Special sell price applied when this product is sold AS PART of this bundle.
     */
    val sellPrice: Price,

    /**
     * Standard sell price of the product when sold individually.
     */
    val sellPriceIndividual: Price,

    /**
     * Quantity of the product included in one unit of the bundle.
     */
    val quantity: Amount,

    val unit: Unit,
    val createdAt: Long,
    val updatedAt: Long?,
)

/**
 * Sealed interface for items that can be placed in the shopping cart.
 */
@Immutable
sealed interface CartItems {

    /**
     * A single product added to the cart.
     */
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

    /**
     * A product bundle added to the cart.
     */
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
