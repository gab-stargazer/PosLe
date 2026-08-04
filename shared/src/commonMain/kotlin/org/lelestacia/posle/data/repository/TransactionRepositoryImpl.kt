package org.lelestacia.posle.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.lelestacia.posle.data.SettingManager
import org.lelestacia.posle.data.dao.ProductDao
import org.lelestacia.posle.data.dao.StockDao
import org.lelestacia.posle.data.dao.TransactionDao
import org.lelestacia.posle.data.entity.StockMovementEntity
import org.lelestacia.posle.data.entity.StockMovementType
import org.lelestacia.posle.data.entity.TransactionEntity
import org.lelestacia.posle.data.entity.TransactionItemEntity
import org.lelestacia.posle.data.entity.TransactionItemProductEntity
import org.lelestacia.posle.data.entity.TransactionItemType
import org.lelestacia.posle.data.entity.TransactionWithItems
import org.lelestacia.posle.domain.model.CartItems
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.model.TransactionItem
import org.lelestacia.posle.domain.model.toDomain
import org.lelestacia.posle.domain.model.toEntity
import org.lelestacia.posle.domain.repository.TransactionRepository
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.UuidProvider
import org.lelestacia.posle.util.getTodayRangeMilliseconds
import kotlin.time.Clock

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val productDao: ProductDao,
    private val stockDao: StockDao,
    private val settingManager: SettingManager,
    private val transactionRunner: org.lelestacia.posle.data.TransactionRunner
) : TransactionRepository {

    override suspend fun createTransaction(
        customerName: Name,
        cartItems: List<CartItems>
    ): Transaction = transactionRunner.runTransaction {
        val currentTimeAsTimestamp = Clock.System.now().toEpochMilliseconds()
        val newTransactionEntity = TransactionEntity(
            id = UuidProvider.newUuid(),
            customerName = customerName,
            createdAt = currentTimeAsTimestamp,
            updatedAt = null
        )

        val newTransactionId = newTransactionEntity.id
        transactionDao.insertTransaction(newTransactionEntity)
        val transactionItems = cartItems.map { cartItem ->
            when (cartItem) {
                is CartItems.BundleCartItem -> {
                    val newTransactionItemEntity = TransactionItemEntity(
                        id = UuidProvider.newUuid(),
                        transactionId = newTransactionId,
                        type = TransactionItemType.Bundle,
                        referenceId = cartItem.bundleId,
                        name = cartItem.bundleName,
                        quantity = cartItem.bundleQuantity,
                        sellPrice = cartItem.bundleTotalPrice,
                        note = cartItem.bundleNote,
                        createdAt = currentTimeAsTimestamp
                    )

                    val newTransactionItemId = newTransactionItemEntity.id
                    transactionDao.insertTransactionItem(newTransactionItemEntity)

                    val newTransactionItemProductEntities =
                        cartItem.bundleProducts.map { bundleProduct ->
                            TransactionItemProductEntity(
                                id = UuidProvider.newUuid(),
                                transactionItemId = newTransactionItemId,
                                productId = bundleProduct.productId,
                                productName = bundleProduct.productName,
                                skuNumber = bundleProduct.skuNumber,
                                imageUri = bundleProduct.imageUri,
                                productBuyPrice = bundleProduct.buyPrice,
                                productSellPrice = bundleProduct.sellPrice,
                                productUnit = bundleProduct.unit,
                                productNote = null,
                                productAmount = bundleProduct.quantity
                            )
                        }

                    transactionDao.insertTransactionItemProduct(newTransactionItemProductEntities)

                    TransactionItem(
                        id = newTransactionItemId,
                        type = TransactionItemType.Bundle,
                        referenceId = cartItem.bundleId,
                        name = cartItem.bundleName,
                        quantity = cartItem.bundleQuantity,
                        sellPrice = cartItem.bundleTotalPrice,
                        note = cartItem.bundleNote,
                        products = newTransactionItemProductEntities.map(TransactionItemProductEntity::toDomain),
                        createdAt = currentTimeAsTimestamp,
                        updatedAt = null
                    )
                }

                is CartItems.ProductCartItem -> {
                    val newTransactionItemEntity = TransactionItemEntity(
                        id = UuidProvider.newUuid(),
                        transactionId = newTransactionId,
                        type = TransactionItemType.Product,
                        referenceId = cartItem.productId,
                        name = cartItem.productName,
                        quantity = cartItem.productQuantity,
                        sellPrice = cartItem.productSellPrice,
                        note = cartItem.productNote,
                        createdAt = currentTimeAsTimestamp
                    )

                    val newTransactionItemId = newTransactionItemEntity.id
                    transactionDao.insertTransactionItem(newTransactionItemEntity)

                    val product = productDao.getProductById(cartItem.productId)

                    val newTransactionItemProductEntity = TransactionItemProductEntity(
                        id = UuidProvider.newUuid(),
                        transactionItemId = newTransactionItemId,
                        productId = cartItem.productId,
                        productName = cartItem.productName,
                        skuNumber = cartItem.skuNumber,
                        imageUri = cartItem.imageUri,
                        productBuyPrice = product.buyPriceHistorical.maxBy { it.createdAt }.price,
                        productSellPrice = cartItem.productSellPrice,
                        productUnit = cartItem.productUnit,
                        productNote = cartItem.productNote,
                        productAmount = cartItem.productQuantity
                    )

                    transactionDao.insertTransactionItemProduct(
                        listOf(newTransactionItemProductEntity)
                    )

                    TransactionItem(
                        id = newTransactionItemId,
                        type = TransactionItemType.Product,
                        referenceId = cartItem.productId,
                        name = cartItem.productName,
                        quantity = cartItem.productQuantity,
                        sellPrice = cartItem.productSellPrice,
                        note = cartItem.productNote,
                        products = listOf(
                            newTransactionItemProductEntity.toDomain()
                        ),
                        createdAt = currentTimeAsTimestamp,
                        updatedAt = null
                    )
                }
            }
        }

        val setting = settingManager
            .getSettings()
            .first()

        if (setting.isProductStockTracked) {
            val stockMovements = transactionItems.flatMap { transactionItem ->
                transactionItem.products.map { product ->
                    StockMovementEntity(
                        id = UuidProvider.newUuid(),
                        productId = product.productId,
                        productName = product.productName,
                        productUnit = product.unit,
                        movementType = StockMovementType.Sale,
                        amount = when (transactionItem.type) {
                            TransactionItemType.Product -> Amount(product.quantity.value.negate())
                            TransactionItemType.Bundle -> Amount(
                                product.quantity.value.multiply(transactionItem.quantity.value).negate()
                            )
                        },
                        note = null,
                        createdAt = currentTimeAsTimestamp
                    )
                }
            }
            stockDao.insertStockMovements(movements = stockMovements)
        }

        Transaction(
            id = newTransactionId,
            customerName = customerName,
            items = transactionItems,
            isRecapped = false,
            createdAt = currentTimeAsTimestamp,
            updatedAt = null
        )
    }

    override fun getTodayTransactions(): Flow<List<Transaction>> {
        val time = getTodayRangeMilliseconds()
        return transactionDao
            .readTransactionsForToday(time.first, time.second)
            .map {
                it.map(TransactionWithItems::toDomain)
            }
    }

    override fun getTransactionsInRange(
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

    override fun getUnRecappedTransactions(): Flow<PagingData<Transaction>> {
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

    override fun getAllTransactions(): Flow<PagingData<Transaction>> {
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

    override fun getTransactionsByQuery(query: String): Flow<PagingData<Transaction>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 30,
                prefetchDistance = 5
            ),
            pagingSourceFactory = {
                transactionDao.searchTransactions(query)
            }
        ).flow.map {
            it.map(TransactionWithItems::toDomain)
        }
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.toEntity())
    }
}