package org.lelestacia.posle.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.lelestacia.posle.domain.model.CartItems
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.util.Name

/**
 * Repository interface for managing transaction-related data operations.
 *
 * This repository is responsible for the full lifecycle of a transaction, including:
 * - Creation of new transactions with associated items and products.
 * - Retrieval of transaction history with support for pagination and date range filtering.
 * - Integration with stock management when sales are processed.
 *
 * @see org.lelestacia.posle.domain.model.Transaction
 */
interface TransactionRepository {

    /**
     * Inserts a new transaction into the database and returns the created [Transaction] object.
     *
     * This operation is atomic and handles:
     * 1. Creating the base [TransactionEntity].
     * 2. Inserting all [CartItems] as [TransactionItemEntity].
     * 3. Linking products for each item via [TransactionItemProductEntity].
     * 4. Recording stock movements if stock tracking is enabled in settings.
     *
     * @param customerName The name of the customer for the transaction.
     * @param cartItems The list of items in the cart to be processed.
     * @return The newly created [Transaction] with its database-assigned ID and metadata.
     * @throws Exception if any part of the transaction insertion fails.
     */
    suspend fun createTransaction(
        customerName: Name,
        cartItems: List<CartItems>
    ): Transaction

    /**
     * Retrieves all transactions that occurred within the current calendar day.
     *
     * @return A [Flow] containing a list of today's [Transaction]s.
     */
    fun getTodayTransactions(): Flow<List<Transaction>>

    /**
     * Retrieves transactions that occurred within a specific millisecond range.
     *
     * Useful for generating reports or custom filtered views.
     *
     * @param startDate The start timestamp in epoch milliseconds (inclusive).
     * @param finishDate The end timestamp in epoch milliseconds (inclusive).
     * @return A [Flow] containing the list of matching [Transaction]s.
     */
    fun getTransactionsInRange(startDate: Long, finishDate: Long): Flow<List<Transaction>>

    /**
     * Provides a paginated stream of transactions that have not yet been included in a recap.
     *
     * @return A [Flow] of [PagingData] containing unrecapped [Transaction]s.
     */
    fun getUnRecappedTransactions(): Flow<PagingData<Transaction>>

    /**
     * Provides a paginated stream of all transactions in the system, ordered by creation date.
     *
     * @return A [Flow] of [PagingData] containing all [Transaction]s.
     */
    fun getAllTransactions(): Flow<PagingData<Transaction>>

    /**
     * Searches for transactions based on a query string.
     *
     * The search is case-insensitive and matches against:
     * - Customer name
     * - Product names within the transaction
     *
     * @param query The search term provided by the user.
     * @return A [Flow] of [PagingData] containing matching [Transaction]s.
     */
    fun getTransactionsByQuery(query: String): Flow<PagingData<Transaction>>

    /**
     * Updates an existing transaction's metadata.
     *
     * Typically used to mark a transaction as "recapped" or update customer information.
     *
     * @param transaction The updated [Transaction] model.
     */
    suspend fun updateTransaction(transaction: Transaction)
}