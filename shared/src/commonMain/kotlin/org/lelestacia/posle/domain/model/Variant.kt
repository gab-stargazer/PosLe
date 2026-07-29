package org.lelestacia.posle.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import org.lelestacia.posle.data.entity.VariantEntity
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price

/**
 * Domain model representing a product variation (e.g., Size, Color, Add-on).
 *
 * @property id Unique variant identifier.
 * @property name Variant display name.
 * @property priceAdjustment The amount to add to the base price when this variant is selected.
 */
@Immutable
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
