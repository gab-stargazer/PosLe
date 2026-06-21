package org.lelestacia.posle.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.lelestacia.posle.domain.model.StockMovement
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Unit

@Entity(
    tableName = "stock"
)
data class StockEntity(
    @ColumnInfo("id")
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo("product_id")
    val productId: Int,
    @ColumnInfo("stock")
    val stock: Amount,
    @ColumnInfo("updated_at")
    val updatedAt: Long?
)

@Entity(
    tableName = "stock_movement",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("product_id")]
)
data class StockMovementEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Int = 0,

    @ColumnInfo("product_id")
    val productId: Int,

    @ColumnInfo("product_name")
    val productName: Name,

    @ColumnInfo("product_unit")
    val productUnit: Unit,

    @ColumnInfo("movement_type")
    val movementType: StockMovementType,

    @ColumnInfo("amount")
    val amount: Amount,

    @ColumnInfo("note")
    val note: String? = null,

    @ColumnInfo("created_at")
    val createdAt: Long,
)

enum class StockMovementType {
    Inbound,
    Purchase,
    Adjustment,
}

fun StockMovementEntity.toDomain(): StockMovement {
    return StockMovement(
        id = id,
        productId = productId,
        productName = productName,
        productUnit = productUnit,
        movementType = movementType,
        amount = amount,
        note = note,
        createdAt = createdAt
    )
}