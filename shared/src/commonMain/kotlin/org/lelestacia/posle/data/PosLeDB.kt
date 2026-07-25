@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.lelestacia.posle.data

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.Transactor
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.room.useWriterConnection
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
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
    version = 3,
    exportSchema = true,
    autoMigrations = [AutoMigration(1, 2)]
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
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(connection: SQLiteConnection) {
                // 1. stock
                connection.execSQL("CREATE TABLE IF NOT EXISTS `stock_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `product_id` INTEGER NOT NULL, `stock` TEXT NOT NULL, `updated_at` INTEGER)")
                connection.execSQL("INSERT INTO `stock_new` (id, product_id, stock, updated_at) SELECT id, product_id, CAST(stock AS TEXT), updated_at FROM stock")
                connection.execSQL("DROP TABLE stock")
                connection.execSQL("ALTER TABLE stock_new RENAME TO stock")

                // 2. transaction_item
                connection.execSQL("CREATE TABLE IF NOT EXISTS `transaction_item_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `transaction_id` INTEGER NOT NULL, `type` TEXT NOT NULL, `reference_id` INTEGER NOT NULL, `name` TEXT NOT NULL, `quantity` TEXT NOT NULL, `sell_price` TEXT NOT NULL, `note` TEXT, `created_at` INTEGER NOT NULL, `updated_at` INTEGER)")
                connection.execSQL("INSERT INTO `transaction_item_new` (id, transaction_id, type, reference_id, name, quantity, sell_price, note, created_at, updated_at) SELECT id, transaction_id, type, reference_id, name, CAST(quantity AS TEXT), sell_price, note, created_at, updated_at FROM transaction_item")
                connection.execSQL("DROP TABLE transaction_item")
                connection.execSQL("ALTER TABLE transaction_item_new RENAME TO transaction_item")
                connection.execSQL("CREATE INDEX IF NOT EXISTS `index_transaction_item_transaction_id` ON `transaction_item` (`transaction_id`)")

                // 3. transaction_item_product
                connection.execSQL("CREATE TABLE IF NOT EXISTS `transaction_item_product_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `transaction_item_id` INTEGER NOT NULL, `product_id` INTEGER NOT NULL, `product_name` TEXT NOT NULL, `sku_number` TEXT, `image_uri` TEXT, `product_buy_price` TEXT NOT NULL, `product_sell_price` TEXT NOT NULL, `product_unit` TEXT NOT NULL, `product_note` TEXT, `product_amount` TEXT NOT NULL, `variants` TEXT NOT NULL, FOREIGN KEY(`transaction_item_id`) REFERENCES `transaction_item`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
                connection.execSQL("INSERT INTO `transaction_item_product_new` (id, transaction_item_id, product_id, product_name, sku_number, image_uri, product_buy_price, product_sell_price, product_unit, product_note, product_amount, variants) SELECT id, transaction_item_id, product_id, product_name, sku_number, image_uri, product_buy_price, product_sell_price, product_unit, product_note, CAST(product_amount AS TEXT), variants FROM transaction_item_product")
                connection.execSQL("DROP TABLE transaction_item_product")
                connection.execSQL("ALTER TABLE transaction_item_product_new RENAME TO transaction_item_product")
                connection.execSQL("CREATE INDEX IF NOT EXISTS `index_transaction_item_product_transaction_item_id` ON `transaction_item_product` (`transaction_item_id`)")

                // 4. stock_movement
                connection.execSQL("CREATE TABLE IF NOT EXISTS `stock_movement_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `product_id` INTEGER NOT NULL, `product_name` TEXT NOT NULL, `product_unit` TEXT NOT NULL, `movement_type` TEXT NOT NULL, `amount` TEXT NOT NULL, `note` TEXT, `created_at` INTEGER NOT NULL, FOREIGN KEY(`product_id`) REFERENCES `product`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
                connection.execSQL("INSERT INTO `stock_movement_new` (id, product_id, product_name, product_unit, movement_type, amount, note, created_at) SELECT id, product_id, product_name, product_unit, movement_type, CAST(amount AS TEXT), note, created_at FROM stock_movement")
                connection.execSQL("DROP TABLE stock_movement")
                connection.execSQL("ALTER TABLE stock_movement_new RENAME TO stock_movement")
                connection.execSQL("CREATE INDEX IF NOT EXISTS `index_stock_movement_product_id` ON `stock_movement` (`product_id`)")

                // 5. bundle_product
                connection.execSQL("CREATE TABLE IF NOT EXISTS `bundle_product_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `bundle_id` INTEGER NOT NULL, `product_id` INTEGER NOT NULL, `name` TEXT NOT NULL, `quantity` TEXT NOT NULL, `unit` TEXT NOT NULL, `sell_price` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER, FOREIGN KEY(`bundle_id`) REFERENCES `bundle`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`product_id`) REFERENCES `product`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
                connection.execSQL("INSERT INTO `bundle_product_new` (id, bundle_id, product_id, name, quantity, unit, sell_price, created_at, updated_at) SELECT id, bundle_id, product_id, name, CAST(quantity AS TEXT), unit, sell_price, created_at, updated_at FROM bundle_product")
                connection.execSQL("DROP TABLE bundle_product")
                connection.execSQL("ALTER TABLE bundle_product_new RENAME TO bundle_product")
                connection.execSQL("CREATE INDEX IF NOT EXISTS `index_bundle_product_bundle_id` ON `bundle_product` (`bundle_id`)")
                connection.execSQL("CREATE INDEX IF NOT EXISTS `index_bundle_product_product_id` ON `bundle_product` (`product_id`)")
            }
        }
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<PosLeDB>

interface TransactionRunner {
    suspend fun runTransaction(block: suspend () -> Unit)
}

class TransactionRunnerImpl(val db: PosLeDB) : TransactionRunner {
    override suspend fun runTransaction(block: suspend () -> Unit) {
        db.useWriterConnection { transactor ->
            transactor.withTransaction(Transactor.SQLiteTransactionType.IMMEDIATE) {
                block()
            }
        }
    }
}
