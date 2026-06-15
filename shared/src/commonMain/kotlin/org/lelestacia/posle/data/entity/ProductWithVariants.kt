package org.lelestacia.posle.data.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.toDomain

data class ProductWithVariants(
    @Embedded
    val product: ProductEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = VariantJunction::class,
            parentColumn = "product_id",
            entityColumn = "variant_id"
        )
    )
    val variants: List<VariantEntity>
)

fun ProductWithVariants.toDomain(): Product {
    return Product(
        id = product.id,
        name = product.name,
        price = product.price,
        unit = product.unit,
        imageUri = product.imageUri,
        variants = variants.map { it.toDomain() }.sortedBy { it.name.value }
    )
}
