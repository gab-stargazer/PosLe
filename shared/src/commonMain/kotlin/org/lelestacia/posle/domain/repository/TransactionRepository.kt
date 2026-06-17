package org.lelestacia.posle.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.lelestacia.posle.domain.model.Transaction

interface TransactionRepository {

    suspend fun insertAndGetTransaction(transaction: Transaction): Transaction
    fun readTodayTransactionHistory(): Flow<List<Transaction>>
    fun readUnRecappedTransactionHistory(): Flow<PagingData<Transaction>>
    fun readTransactionHistory(): Flow<PagingData<Transaction>>
    suspend fun updateTransaction(transaction: Transaction)
}