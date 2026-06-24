package org.lelestacia.posle.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import org.lelestacia.posle.data.entity.TransactionEntity
import org.lelestacia.posle.data.entity.TransactionItemEntity
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.Unit

@Immutable
@Serializable
data class Transaction(
    val id: Int,
    val customerName: Name,
    val items: List<TransactionItem>,
    val isRecapped: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long? = null,
)

@Immutable
@Serializable
data class TransactionItem(
    val id: Int,
    val productId: Int,
    val productName: Name,
    val productBuyPrice: Price,
    val productSellPrice: Price,
    val productUnit: Unit,
    val productNote: String?,
    val productAmount: Amount,
    val variants: List<Variant> = emptyList(),
)

fun Transaction.toEntity() = TransactionEntity(
    id = id,
    customerName = customerName,
    isRecapped = isRecapped,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun TransactionItem.toEntity(transactionId: Int) = TransactionItemEntity(
    id = id,
    productId = productId,
    transactionId = transactionId,
    productName = productName,
    productBuyPrice = productBuyPrice,
    productSellPrice = productSellPrice,
    productUnit = productUnit,
    productNote = productNote,
    productAmount = productAmount,
    variants = variants
)