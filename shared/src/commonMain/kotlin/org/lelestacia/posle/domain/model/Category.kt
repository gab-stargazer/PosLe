package org.lelestacia.posle.domain.model

import kotlinx.serialization.Serializable
import org.lelestacia.posle.util.Name

@Serializable
data class Category(
    val id: Int,
    val name: Name,
)
