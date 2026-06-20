package org.lelestacia.posle.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.lelestacia.posle.domain.model.Category
import org.lelestacia.posle.util.Name

@Entity(
    tableName = "category"
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Int = 0,

    @ColumnInfo("name")
    val name: Name,
)

@Entity(
    tableName = "product_category_junction",
    indices = [
        Index("product_id"),
        Index("category_id")
    ]
)
data class ProductCategoryJunction(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Int = 0,

    @ColumnInfo("product_id")
    val productId: Int,

    @ColumnInfo("category_id")
    val categoryId: Int,
)

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = id,
        name = name
    )
}