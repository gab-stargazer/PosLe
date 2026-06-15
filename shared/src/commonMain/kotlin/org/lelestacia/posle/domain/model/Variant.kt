package org.lelestacia.posle.domain.model

import kotlinx.serialization.Serializable
import org.lelestacia.posle.data.entity.VariantEntity
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price

@Serializable
data class Variant(
    val id: Int,
    val name: Name,
    val priceAdjustment: Price,
)

@Serializable
data class VariantJunction(
    val productId: Int,
    val variantId: Int,
)

fun VariantEntity.toDomain(): Variant {
    return Variant(
        id = id,
        name = name,
        priceAdjustment = priceAdjustment
    )
}
