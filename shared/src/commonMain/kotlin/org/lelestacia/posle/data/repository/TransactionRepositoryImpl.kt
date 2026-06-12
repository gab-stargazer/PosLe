package org.lelestacia.posle.data.repository

import org.lelestacia.posle.data.dao.TransactionDao
import org.lelestacia.posle.data.entity.toDomain
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.model.toEntity
import org.lelestacia.posle.domain.repository.TransactionRepository

class TransactionRepositoryImpl(
    private val dao: TransactionDao
) : TransactionRepository {

    override suspend fun insertAndGetTransaction(transaction: Transaction): Transaction {
        return dao.insertTransactionAndReturnTransactionItems(
            transaction = transaction.toEntity(),
            transactionItems = transaction.items.map { it.toEntity(transaction.id) }
        ).toDomain()
    }
}