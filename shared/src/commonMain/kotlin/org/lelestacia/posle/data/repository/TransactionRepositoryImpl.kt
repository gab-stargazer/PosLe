package org.lelestacia.posle.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.lelestacia.posle.data.SettingManager
import org.lelestacia.posle.data.dao.BatchDao
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
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.getTodayRangeMilliseconds
import kotlin.time.Clock

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val batchDao: BatchDao,
    private val productDao: ProductDao,
    private val stockDao: StockDao,
    private val settingManager: SettingManager
) : TransactionRepository {

    private suspend fun consumeStockFromBatches(
        productId: Int,
        requiredQuantity: Amount,
        isStockTracked: Boolean
    ): List<Pair<Price, Amount>> {
        if (!isStockTracked) {
            val latestBuyPrice = productDao.getLatestBuyPriceByProductId(productId).price
            return listOf(latestBuyPrice to requiredQuantity)
        }

        val activeBatches = batchDao.getActiveBatchesByProduct(productId)
        if (activeBatches.isEmpty()) {
            val latestBuyPrice = productDao.getLatestBuyPriceByProductId(productId).price
            return listOf(latestBuyPrice to requiredQuantity)
        }

        val consumedSegments = mutableListOf<Pair<Price, Amount>>()
        var remainingToConsume = requiredQuantity.value

        for (batch in activeBatches) {
            if (remainingToConsume <= java.math.BigDecimal.ZERO) break

            val availableInBatch = batch.currentQuantity.value
            val toTake = if (availableInBatch >= remainingToConsume) {
                remainingToConsume
            } else {
                availableInBatch
            }

            consumedSegments.add(batch.buyPrice to Amount(toTake))
            batchDao.updateBatchQuantity(batch.id, availableInBatch.subtract(toTake))
            remainingToConsume = remainingToConsume.subtract(toTake)
        }

        // If still remaining (oversell), use latest buy price for the rest
        if (remainingToConsume > java.math.BigDecimal.ZERO) {
            val latestBuyPrice = productDao.getLatestBuyPriceByProductId(productId).price
            consumedSegments.add(latestBuyPrice to Amount(remainingToConsume))
        }

        return consumedSegments
    }

    override suspend fun insertAndGetTransaction(
        customerName: Name,
        cartItems: List<CartItems>
    ): Transaction {
        val currentTimeAsTimestamp = Clock.System.now().toEpochMilliseconds()
        val setting = settingManager.readSettings().first()
        val isStockTracked = setting.isProductStockTracked

        val newTransactionEntity = TransactionEntity(
            id = 0,
            customerName = customerName,
            createdAt = currentTimeAsTimestamp,
            updatedAt = null
        )

        val newTransactionId = transactionDao.insertTransaction(newTransactionEntity).toInt()
        val transactionItems = cartItems.map { cartItem ->
            when (cartItem) {
                is CartItems.BundleCartItem -> {
                    val newTransactionItemEntity = TransactionItemEntity(
                        id = 0,
                        transactionId = newTransactionId,
                        type = TransactionItemType.Bundle,
                        referenceId = cartItem.bundleId,
                        name = cartItem.bundleName,
                        quantity = cartItem.bundleQuantity,
                        sellPrice = cartItem.bundleTotalPrice,
                        note = cartItem.bundleNote,
                        createdAt = currentTimeAsTimestamp
                    )

                    val newTransactionItemId =
                        transactionDao.insertTransactionItem(newTransactionItemEntity)

                    val newTransactionItemProductEntities = mutableListOf<TransactionItemProductEntity>()
                    
                    cartItem.bundleProducts.forEach { bundleProduct ->
                        val totalRequired = Amount(bundleProduct.quantity.value.multiply(cartItem.bundleQuantity.value))
                        val segments = consumeStockFromBatches(bundleProduct.productId, totalRequired, isStockTracked)
                        
                        segments.forEach { (buyPrice, amount) ->
                            val perBundleQuantity = Amount(amount.value.divide(cartItem.bundleQuantity.value, 4, java.math.RoundingMode.HALF_UP))
                            
                            newTransactionItemProductEntities.add(
                                TransactionItemProductEntity(
                                    id = 0,
                                    transactionItemId = newTransactionItemId.toInt(),
                                    productId = bundleProduct.productId,
                                    productName = bundleProduct.productName,
                                    skuNumber = bundleProduct.skuNumber,
                                    imageUri = bundleProduct.imageUri,
                                    productBuyPrice = buyPrice,
                                    productSellPrice = bundleProduct.sellPrice,
                                    productUnit = bundleProduct.unit,
                                    productNote = null,
                                    productAmount = perBundleQuantity
                                )
                            )
                        }
                    }

                    transactionDao.insertTransactionItemProduct(newTransactionItemProductEntities)

                    TransactionItem(
                        id = newTransactionItemId.toInt(),
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
                        id = 0,
                        transactionId = newTransactionId,
                        type = TransactionItemType.Product,
                        referenceId = cartItem.productId,
                        name = cartItem.productName,
                        quantity = cartItem.productQuantity,
                        sellPrice = cartItem.productSellPrice,
                        note = cartItem.productNote,
                        createdAt = currentTimeAsTimestamp
                    )

                    val newTransactionItemId =
                        transactionDao.insertTransactionItem(newTransactionItemEntity)

                    val segments = consumeStockFromBatches(cartItem.productId, cartItem.productQuantity, isStockTracked)
                    
                    val newTransactionItemProductEntities = segments.map { (buyPrice, amount) ->
                        TransactionItemProductEntity(
                            id = 0,
                            transactionItemId = newTransactionItemId.toInt(),
                            productId = cartItem.productId,
                            productName = cartItem.productName,
                            skuNumber = cartItem.skuNumber,
                            imageUri = cartItem.imageUri,
                            productBuyPrice = buyPrice,
                            productSellPrice = cartItem.productSellPrice,
                            productUnit = cartItem.productUnit,
                            productNote = cartItem.productNote,
                            productAmount = amount
                        )
                    }

                    transactionDao.insertTransactionItemProduct(newTransactionItemProductEntities)

                    TransactionItem(
                        id = newTransactionItemId.toInt(),
                        type = TransactionItemType.Product,
                        referenceId = cartItem.productId,
                        name = cartItem.productName,
                        quantity = cartItem.productQuantity,
                        sellPrice = cartItem.productSellPrice,
                        note = cartItem.productNote,
                        products = newTransactionItemProductEntities.map(TransactionItemProductEntity::toDomain),
                        createdAt = currentTimeAsTimestamp,
                        updatedAt = null
                    )
                }
            }
        }

        if (isStockTracked) {
            val stockMovements = transactionItems.flatMap { transactionItem ->
                transactionItem.products.map { product ->
                    StockMovementEntity(
                        id = 0,
                        productId = product.productId,
                        productName = product.productName,
                        productUnit = product.unit,
                        movementType = StockMovementType.Sale,
                        amount = when(transactionItem.type) {
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

        return Transaction(
            id = newTransactionId,
            customerName = newTransactionEntity.customerName,
            items = transactionItems,
            isRecapped = newTransactionEntity.isRecapped,
            createdAt = newTransactionEntity.createdAt,
            updatedAt = newTransactionEntity.updatedAt
        )
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
