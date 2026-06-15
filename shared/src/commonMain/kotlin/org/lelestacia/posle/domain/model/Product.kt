package org.lelestacia.posle.domain.model

import kotlinx.serialization.Serializable
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.Unit

@Serializable
data class Product(
    val id: Int,
    val name: Name,
    val price: Price,
    val unit: Unit,
    val imageUri: String? = null,
    val variants: List<Variant> = emptyList()
)