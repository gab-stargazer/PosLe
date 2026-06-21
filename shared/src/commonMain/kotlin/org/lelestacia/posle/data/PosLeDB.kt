@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.lelestacia.posle.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import org.lelestacia.posle.data.converter.BigDecimalConverter
import org.lelestacia.posle.data.converter.StockMovementTypeConverter
import org.lelestacia.posle.data.converter.TransactionVariantConverter
import org.lelestacia.posle.data.dao.CategoryDao
import org.lelestacia.posle.data.dao.ProductDao
import org.lelestacia.posle.data.dao.StockDao
import org.lelestacia.posle.data.dao.TransactionDao
import org.lelestacia.posle.data.dao.VariantDao
import org.lelestacia.posle.data.entity.CategoryEntity
import org.lelestacia.posle.data.entity.ProductCategoryJunction
import org.lelestacia.posle.data.entity.ProductEntity
import org.lelestacia.posle.data.entity.StockEntity
import org.lelestacia.posle.data.entity.StockMovementEntity
import org.lelestacia.posle.data.entity.TransactionEntity
import org.lelestacia.posle.data.entity.TransactionItemEntity
import org.lelestacia.posle.data.entity.VariantEntity
import org.lelestacia.posle.data.entity.VariantJunction

@Database(
    entities = [
        ProductEntity::class,
        TransactionEntity::class,
        TransactionItemEntity::class,
        VariantEntity::class,
        VariantJunction::class,
        CategoryEntity::class,
        ProductCategoryJunction::class,
        StockEntity::class,
        StockMovementEntity::class
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
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<PosLeDB>
