@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.lelestacia.posle.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import org.lelestacia.posle.data.converter.BigDecimalConverter
import org.lelestacia.posle.data.dao.ProductDao
import org.lelestacia.posle.data.dao.TransactionDao
import org.lelestacia.posle.data.entity.ProductEntity
import org.lelestacia.posle.data.entity.TransactionEntity
import org.lelestacia.posle.data.entity.TransactionItemEntity

@Database(
    entities = [ProductEntity::class, TransactionEntity::class, TransactionItemEntity::class],
    version = 1,
    exportSchema = true
)
@ConstructedBy(AppDatabaseConstructor::class)
@TypeConverters(BigDecimalConverter::class)
abstract class PosLeDB : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun transactionDao(): TransactionDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<PosLeDB>
