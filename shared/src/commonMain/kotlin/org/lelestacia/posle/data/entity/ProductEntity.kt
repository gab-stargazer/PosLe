package org.lelestacia.posle.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.Unit as PosLeUnit

@Entity(
    tableName = "product"
)
data class ProductEntity(
    @ColumnInfo("id")
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo("name")
    val name: Name,
    @ColumnInfo("price")
    val price: Price,
    @ColumnInfo("unit")
    val unit: PosLeUnit,
    @ColumnInfo("image_uri")
    val imageUri: String? = null,
    @ColumnInfo("created_at")
    val createdAt: Long,
    @ColumnInfo("updated_at")
    val updatedAt: Long? = null,
)
