package org.lelestacia.posle.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Price

@Entity(
    tableName = "batch",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("product_id"),
        Index("created_at")
    ]
)
data class BatchEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Int = 0,

    @ColumnInfo("product_id")
    val productId: Int,

    @ColumnInfo("buy_price")
    val buyPrice: Price,

    @ColumnInfo("initial_quantity")
    val initialQuantity: Amount,

    @ColumnInfo("current_quantity")
    val currentQuantity: Amount,

    @ColumnInfo("created_at")
    val createdAt: Long,
)
