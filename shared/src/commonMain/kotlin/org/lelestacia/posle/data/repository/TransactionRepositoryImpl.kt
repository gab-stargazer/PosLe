package org.lelestacia.posle.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.lelestacia.posle.data.dao.TransactionDao
import org.lelestacia.posle.data.entity.TransactionWithItems
import org.lelestacia.posle.data.entity.toDomain
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.model.toEntity
import org.lelestacia.posle.domain.repository.TransactionRepository
import org.lelestacia.posle.util.getTodayRangeMilliseconds

class TransactionRepositoryImpl(
    private val dao: TransactionDao
) : TransactionRepository {

    override suspend fun insertAndGetTransaction(transaction: Transaction): Transaction {
        return dao.insertTransactionAndReturnTransactionItems(
            transaction = transaction.toEntity(),
            transactionItems = transaction.items.map { it.toEntity(transaction.id) }
        ).toDomain()
    }

    override fun readTodayTransactionHistory(): Flow<List<Transaction>> {
        val time = getTodayRangeMilliseconds()
        return dao
            .readTransactionsForToday(time.first, time.second)
            .map {
                it.map(TransactionWithItems::toDomain)
            }
    }

    override fun readUnRecappedTransactionHistory(): Flow<PagingData<Transaction>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 30,
                prefetchDistance = 5
            ),
            pagingSourceFactory = {
                dao.readUnRecappedTransactionWithItems()
            }
        ).flow.map {
            it.map(TransactionWithItems::toDomain)
        }
    }

    override fun readTransactionHistory(): Flow<PagingData<Transaction>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 30,
                prefetchDistance = 5
            ),
            pagingSourceFactory = {
                dao.readTransactionWithItems()
            }
        ).flow.map {
            it.map(TransactionWithItems::toDomain)
        }
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        dao.updateTransaction(transaction.toEntity())
    }
}