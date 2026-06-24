package org.lelestacia.posle.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.lelestacia.posle.util.Price

@Entity(
    tableName = "product_sell_price",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("product_id"), Index("product_id", "created_at")]
)
data class ProductSellPriceEntity(
    @ColumnInfo("id")
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo("product_id")
    val productId: Int,
    @ColumnInfo("price")
    val price: Price,
    @ColumnInfo("change_type")
    val changeType: PriceChangeType,
    @ColumnInfo("created_at")
    val createdAt: Long,
)

@Entity(
    tableName = "product_buy_price",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("product_id"), Index("product_id", "created_at")]
)
data class ProductBuyPriceEntity(
    @ColumnInfo("id")
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo("product_id")
    val productId: Int,
    @ColumnInfo("price")
    val price: Price,
    @ColumnInfo("change_type")
    val changeType: PriceChangeType,
    @ColumnInfo("created_at")
    val createdAt: Long,
)

enum class PriceChangeType {
    ProductCreation, Adjustment
}