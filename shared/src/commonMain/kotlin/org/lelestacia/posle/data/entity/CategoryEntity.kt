package org.lelestacia.posle.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.lelestacia.posle.domain.model.Category
import org.lelestacia.posle.util.Name

@Entity(
    tableName = "category",
    indices = [Index("name", unique = true)]
)
data class CategoryEntity(
    @PrimaryKey
    @ColumnInfo("id")
    val id: String,

    @ColumnInfo("name")
    val name: Name,
)

@Entity(
    tableName = "product_category_junction",
    primaryKeys = ["product_id", "category_id"],
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("product_id"),
        Index("category_id")
    ]
)
data class ProductCategoryJunction(
    @ColumnInfo("product_id")
    val productId: String,

    @ColumnInfo("category_id")
    val categoryId: String,
)

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = id,
        name = name
    )
}