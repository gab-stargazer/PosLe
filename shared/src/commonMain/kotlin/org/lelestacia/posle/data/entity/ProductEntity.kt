package org.lelestacia.posle.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(
    tableName = "product"
)
data class ProductEntity(
    @ColumnInfo("id")
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo("name")
    val name: String,
    @ColumnInfo("price")
    val price: BigDecimal,
    @ColumnInfo("unit")
    val unit: String,
    @ColumnInfo("image_uri")
    val imageUri: String? = null,
    @ColumnInfo("is_product_volatile")
    val isProductVolatile: Boolean,
    @ColumnInfo("created_at")
    val createdAt: Long,
    @ColumnInfo("updated_at")
    val updatedAt: Long? = null,
)
