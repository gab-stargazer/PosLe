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
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.getTodayRangeMilliseconds
import kotlin.time.Clock

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val productDao: ProductDao,
    private val stockDao: StockDao,
    private val settingManager: SettingManager
) : TransactionRepository {

    override suspend fun insertAndGetTransaction(
        customerName: Name,
        cartItems: List<CartItems>
    ): Transaction {

        val currentTimeAsTimestamp = Clock.System.now().toEpochMilliseconds()
        val newTransactionEntity = TransactionEntity(
            id = 0,
            customerName = customerName,
            createdAt = Clock.System.now().toEpochMilliseconds(),
            updatedAt = null
        )

        val newTransactionId = transactionDao.insertTransaction(newTransactionEntity).toInt()
        val transactionItem = cartItems.map { cartItems ->
            when (cartItems) {
                is CartItems.BundleCartItem -> {
                    val newTransactionItemEntity = TransactionItemEntity(
                        id = 0,
                        transactionId = newTransactionId,
                        type = TransactionItemType.Bundle,
                        referenceId = cartItems.bundleId,
                        name = cartItems.bundleName,
                        quantity = cartItems.bundleQuantity,
                        sellPrice = cartItems.bundleTotalPrice,
                        note = cartItems.bundleNote,
                        createdAt = currentTimeAsTimestamp
                    )

                    val newTransactionItemId =
                        transactionDao.insertTransactionItem(newTransactionItemEntity)

                    val newTransactionItemProductEntity =
                        cartItems.bundleProducts.map { bundleProduct ->
                            println(bundleProduct)
                            TransactionItemProductEntity(
                                id = 0,
                                transactionItemId = newTransactionItemId.toInt(),
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

                    transactionDao.insertTransactionItemProduct(newTransactionItemProductEntity)

                    TransactionItem(
                        id = newTransactionItemId.toInt(),
                        type = TransactionItemType.Bundle,
                        referenceId = cartItems.bundleId,
                        name = cartItems.bundleName,
                        quantity = cartItems.bundleQuantity,
                        sellPrice = cartItems.bundleTotalPrice,
                        note = cartItems.bundleNote,
                        products = newTransactionItemProductEntity.map(TransactionItemProductEntity::toDomain),
                        createdAt = currentTimeAsTimestamp,
                        updatedAt = null
                    )
                }

                is CartItems.ProductCartItem -> {
                    val newTransactionItemEntity = TransactionItemEntity(
                        id = 0,
                        transactionId = newTransactionId,
                        type = TransactionItemType.Product,
                        referenceId = cartItems.productId,
                        name = cartItems.productName,
                        quantity = cartItems.productQuantity,
                        sellPrice = cartItems.productSellPrice,
                        note = cartItems.productNote,
                        createdAt = currentTimeAsTimestamp
                    )

                    val newTransactionItemId =
                        transactionDao.insertTransactionItem(newTransactionItemEntity)

                    val products = productDao.getProductById(cartItems.productId)

                    val newTransactionItemProductEntity = TransactionItemProductEntity(
                        id = 0,
                        transactionItemId = newTransactionItemId.toInt(),
                        productId = cartItems.productId,
                        productName = cartItems.productName,
                        skuNumber = cartItems.skuNumber,
                        imageUri = cartItems.imageUri,
                        productBuyPrice = products.buyPriceHistorical.maxBy { it.createdAt }.price,
                        productSellPrice = cartItems.productSellPrice,
                        productUnit = cartItems.productUnit,
                        productNote = cartItems.productNote,
                        productAmount = cartItems.productQuantity
                    )

                    transactionDao.insertTransactionItemProduct(
                        listOf(newTransactionItemProductEntity)
                    )

                    TransactionItem(
                        id = newTransactionItemId.toInt(),
                        type = TransactionItemType.Product,
                        referenceId = cartItems.productId,
                        name = cartItems.productName,
                        quantity = cartItems.productQuantity,
                        sellPrice = cartItems.productSellPrice,
                        note = cartItems.productNote,
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
            .readSettings()
            .first()



        if (setting.isProductStockTracked) {
//            transaction.items.groupBy { it.productId }.onEach {
//                val totalAmount = it.value
//                    .sumOf { product -> product.productAmount.value.toBigDecimal() }
//                    .toFloat()
//
//                stockDao.insertStockMovement(
//                    movement = StockMovementEntity(
//                        productId = it.key,
//                        productName = it.value.first().productName,
//                        productUnit = it.value.first().productUnit,
//                        movementType = StockMovementType.Purchase,
//                        amount = Amount(-totalAmount),
//                        createdAt = Clock.System.now().toEpochMilliseconds()
//                    )
//                )
//            }
        }

        return Transaction(
            id = newTransactionId,
            customerName = newTransactionEntity.customerName,
            items = transactionItem,
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