package org.lelestacia.posle.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.SkuNumber
import org.lelestacia.posle.util.Unit
import java.math.BigDecimal

/**
 * Domain model representing a product in the catalog.
 *
 * @property id Unique product identifier.
 * @property name Product display name.
 * @property stock Current available quantity.
 * @property buyPrice The current purchase price from supplier.
 * @property sellPrice The current default selling price.
 * @property unit Unit of measurement (e.g., kg, pcs).
 * @property skuNumber Unique Stock Keeping Unit string.
 * @property imageUri Local path to the product image.
 * @property variants List of variations available for this product.
 * @property categories List of categories this product belongs to.
 */
@Immutable
@Serializable
data class Product(
    val id: Int,
    val name: Name,
    val stock: Amount,
    val buyPrice: Price,
    val sellPrice: Price,
    val unit: Unit,
    val skuNumber: SkuNumber? = null,
    val imageUri: String? = null,
    val variants: List<Variant> = emptyList(),
    val categories: List<Category> = emptyList()
)
/**
 * Represents a historical record of a product's price.
 *
 * @property id Unique record identifier.
 * @property price The price at that point in time.
 * @property changes Magnitude of price change compared to previous record.
 * @property createdAt Timestamp of the price change.
 */
data class ProductPriceHistory(
    val id: Int,
    val price: Price,
    val changes: BigDecimal,
    val createdAt: Long
)