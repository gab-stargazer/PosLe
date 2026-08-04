@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.lelestacia.posle.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.Transactor
import androidx.room.TypeConverters
import androidx.room.useWriterConnection
import org.lelestacia.posle.data.converter.BigDecimalConverter
import org.lelestacia.posle.data.converter.StockMovementTypeConverter
import org.lelestacia.posle.data.converter.TransactionVariantConverter
import org.lelestacia.posle.data.dao.BundleDao
import org.lelestacia.posle.data.dao.CategoryDao
import org.lelestacia.posle.data.dao.ProductDao
import org.lelestacia.posle.data.dao.StockDao
import org.lelestacia.posle.data.dao.TransactionDao
import org.lelestacia.posle.data.dao.VariantDao
import org.lelestacia.posle.data.entity.BundleEntity
import org.lelestacia.posle.data.entity.BundleProductEntity
import org.lelestacia.posle.data.entity.CategoryEntity
import org.lelestacia.posle.data.entity.ProductBuyPriceEntity
import org.lelestacia.posle.data.entity.ProductCategoryJunction
import org.lelestacia.posle.data.entity.ProductEntity
import org.lelestacia.posle.data.entity.ProductSellPriceEntity
import org.lelestacia.posle.data.entity.StockEntity
import org.lelestacia.posle.data.entity.StockMovementEntity
import org.lelestacia.posle.data.entity.TransactionEntity
import org.lelestacia.posle.data.entity.TransactionItemEntity
import org.lelestacia.posle.data.entity.TransactionItemProductEntity
import org.lelestacia.posle.data.entity.VariantEntity
import org.lelestacia.posle.data.entity.VariantJunction

@Database(
    entities = [
        ProductEntity::class,
        ProductSellPriceEntity::class,
        ProductBuyPriceEntity::class,
        TransactionEntity::class,
        TransactionItemEntity::class,
        TransactionItemProductEntity::class,
        VariantEntity::class,
        VariantJunction::class,
        CategoryEntity::class,
        ProductCategoryJunction::class,
        StockEntity::class,
        StockMovementEntity::class,
        BundleEntity::class,
        BundleProductEntity::class
    ],
    version = 1,
    exportSchema = true,
)
@ConstructedBy(AppDatabaseConstructor::class)
@TypeConverters(
    BigDecimalConverter::class,
    TransactionVariantConverter::class,
    StockMovementTypeConverter::class
)
abstract class PosLeDB : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun stockDao(): StockDao
    abstract fun transactionDao(): TransactionDao
    abstract fun variantDao(): VariantDao
    abstract fun categoryDao(): CategoryDao
    abstract fun bundleDao(): BundleDao

    companion object {
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<PosLeDB>

interface TransactionRunner {
    suspend fun <T> runTransaction(block: suspend () -> T): T
}

class TransactionRunnerImpl(val db: PosLeDB) : TransactionRunner {
    override suspend fun <T> runTransaction(block: suspend () -> T): T {
        return db.useWriterConnection { transactor ->
            transactor.withTransaction(Transactor.SQLiteTransactionType.IMMEDIATE) {
                block()
            }
        }
    }
}
