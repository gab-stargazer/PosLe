package org.lelestacia.posle.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.SkuNumber
import org.lelestacia.posle.util.Unit
import java.math.BigDecimal

@Immutable
@Serializable
data class Product(
    val id: Int,
    val name: Name,
    val stock: Amount,
    val buyPrice: Price,
    val sellPrice: Price,
    val unit: Unit,
    val skuNumber: SkuNumber? = null,
    val imageUri: String? = null,
    val variants: List<Variant> = emptyList(),
    val categories: List<Category> = emptyList()
)
data class ProductPriceHistory(
    val id: Int,
    val price: Price,
    val changes: BigDecimal,
    val createdAt: Long
)