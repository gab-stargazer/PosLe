package org.lelestacia.posle.domain.model

import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.Unit

data class Transaction(
    val id: Int,
    val productName: Name,
    val productPrice: Price,
    val productUnit: Unit,
    val productAmount: Amount,
    val transactionDate: Long
)
