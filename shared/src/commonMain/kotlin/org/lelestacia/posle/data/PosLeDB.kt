@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.lelestacia.posle.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import org.lelestacia.posle.data.converter.BigDecimalConverter
import org.lelestacia.posle.data.dao.ProductDao
import org.lelestacia.posle.data.dao.TransactionDao
import org.lelestacia.posle.data.entity.ProductEntity
import org.lelestacia.posle.data.entity.TransactionEntity
import org.lelestacia.posle.data.entity.TransactionItemEntity

@Database(
    entities = [ProductEntity::class, TransactionEntity::class, TransactionItemEntity::class],
    version = 2,
    exportSchema = true
)
@ConstructedBy(AppDatabaseConstructor::class)
@TypeConverters(BigDecimalConverter::class)
abstract class PosLeDB : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL("ALTER TABLE `transaction` ADD COLUMN `is_recapped` INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<PosLeDB>
