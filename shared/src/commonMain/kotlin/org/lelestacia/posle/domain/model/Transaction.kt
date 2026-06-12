package org.lelestacia.posle.domain.model

import kotlinx.serialization.Serializable
import org.lelestacia.posle.data.entity.TransactionEntity
import org.lelestacia.posle.data.entity.TransactionItemEntity
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.Unit

@Serializable
data class Transaction(
    val id: Int,
    val items: List<TransactionItem>,
    val createdAt: Long,
    val updatedAt: Long? = null,
)

@Serializable
data class TransactionItem(
    val id: Int,
    val productName: Name,
    val productPrice: Price,
    val productUnit: Unit,
    val productAmount: Amount,
)

fun Transaction.toEntity() = TransactionEntity(
    id = id,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun TransactionItem.toEntity(transactionId: Int) = TransactionItemEntity(
    id = id,
    transactionId = transactionId,
    productName = productName,
    productPrice = productPrice,
    productUnit = productUnit,
    productAmount = productAmount,
)