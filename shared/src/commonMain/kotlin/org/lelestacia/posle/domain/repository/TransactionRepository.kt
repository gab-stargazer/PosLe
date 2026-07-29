package org.lelestacia.posle.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.lelestacia.posle.domain.model.CartItems
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.util.Name

interface TransactionRepository {

    suspend fun insertAndGetTransaction(
        customerName: Name,
        cartItems: List<CartItems>
    ): Transaction
    fun readTodayTransactionHistory(): Flow<List<Transaction>>
    fun readTransactionInRange(startDate: Long, finishDate: Long): Flow<List<Transaction>>
    fun readUnRecappedTransactionHistory(): Flow<PagingData<Transaction>>
    fun readTransactionHistory(): Flow<PagingData<Transaction>>
    fun searchTransactions(query: String): Flow<PagingData<Transaction>>
    suspend fun updateTransaction(transaction: Transaction)
}