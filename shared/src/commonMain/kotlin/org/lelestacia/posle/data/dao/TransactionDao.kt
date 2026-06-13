package org.lelestacia.posle.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import org.lelestacia.posle.data.entity.TransactionEntity
import org.lelestacia.posle.data.entity.TransactionItemEntity
import org.lelestacia.posle.data.entity.TransactionWithItems

@Dao
interface TransactionDao {

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Insert
    suspend fun insertTransactionItems(items: List<TransactionItemEntity>)

    @Transaction
    @Query("SELECT * FROM `transaction` WHERE id = :transactionId")
    suspend fun getTransactionWithItems(transactionId: Int): TransactionWithItems

    @Transaction
    @Query("SELECT * FROM `transaction` ORDER BY created_at DESC")
    fun readTransactionWithItems(): PagingSource<Int, TransactionWithItems>

    @Transaction
    suspend fun insertTransactionAndReturnTransactionItems(
        transaction: TransactionEntity,
        transactionItems: List<TransactionItemEntity>
    ): TransactionWithItems {
        val transactionId = insertTransaction(transaction = transaction).toInt()
        val mappedTransactionItems = transactionItems
            .map { it.copy(transactionId = transactionId) }

        insertTransactionItems(mappedTransactionItems)

        return getTransactionWithItems(transactionId)
    }
}