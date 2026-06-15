package org.lelestacia.posle.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price

@Entity(
    tableName = "variant"
)
data class VariantEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Int = 0,

    @ColumnInfo("name")
    val name: Name,

    @ColumnInfo("price_adjustment")
    val priceAdjustment: Price,
)

@Entity(
    tableName = "variant_junction",
    indices = [
        Index("product_id"),
        Index("variant_id")
    ]
)
data class VariantJunction(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Int = 0,

    @ColumnInfo("product_id")
    val productId: Int,

    @ColumnInfo("variant_id")
    val variantId: Int,
)

fun Variant.toEntity(): VariantEntity {
    return VariantEntity(
        id = id,
        name = name,
        priceAdjustment = priceAdjustment
    )
}