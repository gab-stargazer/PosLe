package org.lelestacia.posle.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.model.TransactionItem
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.Unit

@Entity(tableName = "transaction")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Int = 0,
    @ColumnInfo("customer_name")
    val customerName: Name,
    @ColumnInfo("is_recapped")
    val isRecapped: Boolean = false,
    @ColumnInfo("created_at")
    val createdAt: Long,
    @ColumnInfo("updated_at")
    val updatedAt: Long? = null,
)

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
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Int = 0,
    @ColumnInfo("transaction_id")
    val transactionId: Int,
    @ColumnInfo("product_id")
    val productId: Int,
    @ColumnInfo("product_name")
    val productName: Name,
    @ColumnInfo("product_price")
    val productPrice: Price,
    @ColumnInfo("product_unit")
    val productUnit: Unit,
    @ColumnInfo("product_amount")
    val productAmount: Amount,
    @ColumnInfo("variants")
    val variants: List<Variant> = emptyList(),
)

data class TransactionWithItems(
    @Embedded
    val transaction: TransactionEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "transaction_id"
    )
    val items: List<TransactionItemEntity>
)

fun TransactionWithItems.toDomain() = Transaction(
    id = transaction.id,
    customerName = transaction.customerName,
    items = items.map { it.toDomain() },
    isRecapped = transaction.isRecapped,
    createdAt = transaction.createdAt,
    updatedAt = transaction.updatedAt,
)

fun TransactionItemEntity.toDomain() = TransactionItem(
    id = id,
    productId = productId,
    productName = productName,
    productPrice = productPrice,
    productUnit = productUnit,
    productAmount = productAmount,
    variants = variants
)