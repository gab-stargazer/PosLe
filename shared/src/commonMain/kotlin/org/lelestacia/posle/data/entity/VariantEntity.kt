package org.lelestacia.posle.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price

@Entity(
    tableName = "variant",
    indices = [Index("name", unique = true)]
)
data class VariantEntity(
    @PrimaryKey
    @ColumnInfo("id")
    val id: String,

    @ColumnInfo("name")
    val name: Name,

    @ColumnInfo("price_adjustment")
    val priceAdjustment: Price,
)

@Entity(
    tableName = "variant_junction",
    primaryKeys = ["product_id", "variant_id"],
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = VariantEntity::class,
            parentColumns = ["id"],
            childColumns = ["variant_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("product_id"),
        Index("variant_id")
    ]
)
data class VariantJunction(
    @ColumnInfo("product_id")
    val productId: String,

    @ColumnInfo("variant_id")
    val variantId: String,
)

fun Variant.toEntity(): VariantEntity {
    return VariantEntity(
        id = id,
        name = name,
        priceAdjustment = priceAdjustment
    )
}