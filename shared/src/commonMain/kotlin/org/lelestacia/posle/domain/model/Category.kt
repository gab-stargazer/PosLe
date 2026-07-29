package org.lelestacia.posle.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import org.lelestacia.posle.data.entity.CategoryEntity
import org.lelestacia.posle.util.Name

/**
 * Domain model representing a product category.
 *
 * @property id Unique category identifier.
 * @property name Category display name.
 */
@Immutable
@Serializable
data class Category(
    val id: Int,
    val name: Name,
)

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name
    )
}