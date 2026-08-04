package org.lelestacia.posle.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.serialization.Serializable
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.SkuNumber
import org.lelestacia.posle.util.Unit

@Entity(
    tableName = "transaction",
    indices = [Index("customer_name")]
)
data class TransactionEntity(
    @PrimaryKey
    @ColumnInfo("id")
    val id: String,
    @ColumnInfo("customer_name")
    val customerName: Name,
    @ColumnInfo("is_recapped")
    val isRecapped: Boolean = false,
    @ColumnInfo("created_at")
    val createdAt: Long,
    @ColumnInfo("updated_at")
    val updatedAt: Long? = null,
)

@Serializable
enum class TransactionItemType {
    Product,
    Bundle
}

@Entity(
    tableName = "transaction_item",
    foreignKeys = [
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transaction_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("transaction_id")]
)
data class TransactionItemEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo("transaction_id")
    val transactionId: String,
    @ColumnInfo("type")
    val type: TransactionItemType,
    // Product.id or Bundle.id
    @ColumnInfo("reference_id")
    val referenceId: String,
    @ColumnInfo("name")
    val name: Name,
    @ColumnInfo("quantity")
    val quantity: Amount,
    @ColumnInfo("sell_price")
    val sellPrice: Price,
    @ColumnInfo("note")
    val note: String?,
    @ColumnInfo("created_at")
    val createdAt: Long,
    @ColumnInfo("updated_at")
    val updatedAt: Long? = null,
)

@Entity(
    tableName = "transaction_item_product",
    foreignKeys = [
        ForeignKey(
            entity = TransactionItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["transaction_item_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("transaction_item_id")]
)
data class TransactionItemProductEntity(
    @PrimaryKey
    @ColumnInfo("id")
    val id: String,
    @ColumnInfo("transaction_item_id")
    val transactionItemId: String,
    @ColumnInfo("product_id")
    val productId: String,
    @ColumnInfo("product_name")
    val productName: Name,
    @ColumnInfo("sku_number")
    val skuNumber: SkuNumber?,
    @ColumnInfo("image_uri")
    val imageUri: String?,
    @ColumnInfo("product_buy_price")
    val productBuyPrice: Price,
    @ColumnInfo("product_sell_price")
    val productSellPrice: Price,
    @ColumnInfo("product_unit")
    val productUnit: Unit,
    @ColumnInfo("product_note")
    val productNote: String? = null,
    @ColumnInfo("product_amount")
    val productAmount: Amount,
    @ColumnInfo("variants")
    val variants: List<Variant> = emptyList(),
)

data class TransactionItemWithProducts(
    @Embedded val item: TransactionItemEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "transaction_item_id"
    )
    val products: List<TransactionItemProductEntity>
)

data class TransactionWithItems(
    @Embedded val transaction: TransactionEntity,
    @Relation(
        entity = TransactionItemEntity::class,
        parentColumn = "id",
        entityColumn = "transaction_id"
    )
    val items: List<TransactionItemWithProducts>
)