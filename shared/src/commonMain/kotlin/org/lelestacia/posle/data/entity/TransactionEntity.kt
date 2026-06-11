package org.lelestacia.posle.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.Unit

@Entity(
    tableName = "transaction"
)
data class TransactionEntity(
    @ColumnInfo("id")
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo("product_name")
    val productName: Name,
    @ColumnInfo("product_price")
    val productPrice: Price,
    @ColumnInfo("product_unit")
    val productUnit: Unit,
    @ColumnInfo("product_amount")
    val productAmount: Amount,
    @ColumnInfo("created_at")
    val createdAt: Long,
    @ColumnInfo("updated_at")
    val updatedAt: Long? = null,
)
