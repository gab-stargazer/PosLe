package org.lelestacia.posle.domain.model

import kotlinx.serialization.Serializable
import org.lelestacia.posle.data.entity.TransactionEntity
import org.lelestacia.posle.data.entity.TransactionItemProductEntity
import org.lelestacia.posle.data.entity.TransactionItemType
import org.lelestacia.posle.data.entity.TransactionItemWithProducts
import org.lelestacia.posle.data.entity.TransactionWithItems
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.SkuNumber
import org.lelestacia.posle.util.Unit

@Serializable
data class Transaction(
    val id: Int,
    val customerName: Name,
    val items: List<TransactionItem>,
    val isRecapped: Boolean,
    val createdAt: Long,
    val updatedAt: Long?
)

@Serializable
data class TransactionItem(
    val id: Int,
    val type: TransactionItemType,

    /**
     * Original Product.id or Bundle.id.
     * Useful for navigating back to the original object if it still exists.
     */
    val referenceId: Int,

    /**
     * Name shown on the receipt.
     * For Product -> product name.
     * For Bundle -> bundle name.
     */
    val name: Name,

    /**
     * Number of bundles or products purchased.
     */
    val quantity: Amount,

    /**
     * Selling price of the product/bundle at purchase time.
     */
    val sellPrice: Price,
    val note: String?,

    /**
     * Products contained in this item.
     *
     * Product purchase:
     *   quantity = 3
     *   products = [Coffee]
     *
     * Bundle purchase:
     *   quantity = 2
     *   products = [Coffee, Milk, Sugar]
     */
    val products: List<TransactionProduct>,
    val createdAt: Long,
    val updatedAt: Long?
)

@Serializable
data class TransactionProduct(
    val productId: Int,
    val productName: Name,
    val skuNumber: SkuNumber?,
    val imageUri: String?,

    /**
     * Snapshot of product prices at purchase time.
     */
    val buyPrice: Price,
    val sellPrice: Price,

    val unit: Unit,
    val note: String?,

    /**
     * Quantity of this product inside ONE transaction item.
     *
     * Example:
     * Product purchase:
     *   quantity = 3 bottles
     *
     * Bundle:
     *   1 Bundle contains:
     *     Coffee x2
     *     Sugar x1
     */
    val quantity: Amount,

    val variants: List<Variant>
)

fun List<TransactionProduct>.groupForDisplay(): List<TransactionProduct> {
    return this.groupBy { it.productId }
        .map { (_, segments) ->
            val first = segments.first()
            first.copy(
                quantity = Amount(segments.sumOf { it.quantity.value })
            )
        }
}

fun TransactionWithItems.toDomain() = Transaction(
    id = transaction.id,
    customerName = transaction.customerName,
    isRecapped = transaction.isRecapped,
    createdAt = transaction.createdAt,
    updatedAt = transaction.updatedAt,
    items = items.map { it.toDomain() }
)

fun TransactionItemWithProducts.toDomain() = TransactionItem(
    id = item.id,
    type = item.type,
    referenceId = item.referenceId,
    name = item.name,
    quantity = item.quantity,
    sellPrice = item.sellPrice,
    note = item.note,
    createdAt = item.createdAt,
    updatedAt = item.updatedAt,
    products = products.map { it.toDomain() }
)

fun TransactionItemProductEntity.toDomain() = TransactionProduct(
    productId = productId,
    productName = productName,
    skuNumber = skuNumber,
    imageUri = imageUri,
    buyPrice = productBuyPrice,
    sellPrice = productSellPrice,
    unit = productUnit,
    note = productNote,
    quantity = productAmount,
    variants = variants
)

fun Transaction.toEntity() = TransactionEntity(
    id = id,
    customerName = customerName,
    isRecapped = isRecapped,
    createdAt = createdAt,
    updatedAt = updatedAt,
)