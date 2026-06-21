package org.lelestacia.posle.domain.model

import androidx.compose.runtime.Immutable
import org.lelestacia.posle.data.entity.StockMovementType
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Unit


data class Stock(
    val id: Int,
    val productId: Int,
    val stock: Amount,
    val updatedAt: Long?
)

@Immutable
data class StockMovement(
    val id: Int = 0,
    val productId: Int,
    val productName: Name,
    val productUnit: Unit,
    val movementType: StockMovementType,
    val amount: Amount,
    val note: String? = null,
    val createdAt: Long,
)