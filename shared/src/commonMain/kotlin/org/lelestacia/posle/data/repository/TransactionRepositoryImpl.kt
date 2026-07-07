package org.lelestacia.posle.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.lelestacia.posle.data.SettingManager
import org.lelestacia.posle.data.dao.StockDao
import org.lelestacia.posle.data.dao.TransactionDao
import org.lelestacia.posle.data.entity.StockMovementEntity
import org.lelestacia.posle.data.entity.StockMovementType
import org.lelestacia.posle.data.entity.TransactionWithItems
import org.lelestacia.posle.data.entity.toDomain
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.model.toEntity
import org.lelestacia.posle.domain.repository.TransactionRepository
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.getTodayRangeMilliseconds
import kotlin.time.Clock

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val stockDao: StockDao,
    private val settingManager: SettingManager
) : TransactionRepository {

    override suspend fun insertAndGetTransaction(transaction: Transaction): Transaction {

        val transaction = transactionDao.insertTransactionAndReturnTransactionItems(
            transaction = transaction.toEntity(),
            transactionItems = transaction.items.map { it.toEntity(transaction.id) }
        ).toDomain()

        val setting = settingManager
            .readSettings()
            .first()

        if (setting.isProductStockTracked) {
            transaction.items.groupBy { it.productId }.onEach {
                val totalAmount = it.value
                    .sumOf { product -> product.productAmount.value.toBigDecimal() }
                    .toFloat()

                stockDao.insertStockMovement(
                    movement = StockMovementEntity(
                        productId = it.key,
                        productName = it.value.first().productName,
                        productUnit = it.value.first().productUnit,
                        movementType = StockMovementType.Purchase,
                        amount = Amount(-totalAmount),
                        createdAt = Clock.System.now().toEpochMilliseconds()
                    )
                )
            }
        }

        return transaction
    }

    override fun readTodayTransactionHistory(): Flow<List<Transaction>> {
        val time = getTodayRangeMilliseconds()
        return transactionDao
            .readTransactionsForToday(time.first, time.second)
            .map {
                it.map(TransactionWithItems::toDomain)
            }
    }

    override fun readTransactionInRange(
        startDate: Long,
        finishDate: Long
    ): Flow<List<Transaction>> {
        return transactionDao.readTransactionWithItemsInRange(
            startDate,
            finishDate
        ).map {
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
                transactionDao.readUnRecappedTransactionWithItems()
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
                transactionDao.readTransactionWithItems()
            }
        ).flow.map {
            it.map(TransactionWithItems::toDomain)
        }
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.toEntity())
    }
}