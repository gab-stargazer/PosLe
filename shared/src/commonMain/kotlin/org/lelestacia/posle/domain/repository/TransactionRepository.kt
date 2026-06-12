package org.lelestacia.posle.domain.repository

import org.lelestacia.posle.domain.model.Transaction

interface TransactionRepository {

    suspend fun insertAndGetTransaction(transaction: Transaction): Transaction
}