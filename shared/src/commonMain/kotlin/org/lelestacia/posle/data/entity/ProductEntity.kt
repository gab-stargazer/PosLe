package org.lelestacia.posle.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.SkuNumber
import org.lelestacia.posle.util.Unit as PosLeUnit

@Entity(
    tableName = "product",
    indices = [Index("name"), Index("sku_number")]
)
data class ProductEntity(
    @ColumnInfo("id")
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo("name")
    val name: Name,
    @ColumnInfo("unit")
    val unit: PosLeUnit,
    @ColumnInfo("sku_number")
    val skuNumber: SkuNumber? = null,
    @ColumnInfo("image_uri")
    val imageUri: String? = null,
    @ColumnInfo("created_at")
    val createdAt: Long,
    @ColumnInfo("updated_at")
    val updatedAt: Long? = null,
)
