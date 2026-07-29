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
    version = 4,
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
                // ... (keep existing migration code for history)
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(connection: SQLiteConnection) {
                // 1. product: Add unique index for sku_number
                connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_product_sku_number` ON `product` (`sku_number`)")

                // 2. stock: Recreate to change PK and add FK
                connection.execSQL("CREATE TABLE IF NOT EXISTS `stock_new` (`product_id` INTEGER NOT NULL, `stock` TEXT NOT NULL, `updated_at` INTEGER, PRIMARY KEY(`product_id`), FOREIGN KEY(`product_id`) REFERENCES `product`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
                connection.execSQL("INSERT INTO `stock_new` (product_id, stock, updated_at) SELECT product_id, stock, updated_at FROM stock")
                connection.execSQL("DROP TABLE stock")
                connection.execSQL("ALTER TABLE stock_new RENAME TO stock")

                // 3. transaction_item: Recreate to add FK
                connection.execSQL("CREATE TABLE IF NOT EXISTS `transaction_item_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `transaction_id` INTEGER NOT NULL, `type` TEXT NOT NULL, `reference_id` INTEGER NOT NULL, `name` TEXT NOT NULL, `quantity` TEXT NOT NULL, `sell_price` TEXT NOT NULL, `note` TEXT, `created_at` INTEGER NOT NULL, `updated_at` INTEGER, FOREIGN KEY(`transaction_id`) REFERENCES `transaction`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
                connection.execSQL("INSERT INTO `transaction_item_new` (id, transaction_id, type, reference_id, name, quantity, sell_price, note, created_at, updated_at) SELECT id, transaction_id, type, reference_id, name, quantity, sell_price, note, created_at, updated_at FROM transaction_item")
                connection.execSQL("DROP TABLE transaction_item")
                connection.execSQL("ALTER TABLE transaction_item_new RENAME TO transaction_item")
                connection.execSQL("CREATE INDEX IF NOT EXISTS `index_transaction_item_transaction_id` ON `transaction_item` (`transaction_id`)")

                // 4. category: Add unique index for name
                connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_category_name` ON `category` (`name`)")

                // 5. product_category_junction: Recreate to change PK and add FKs
                connection.execSQL("CREATE TABLE IF NOT EXISTS `product_category_junction_new` (`product_id` INTEGER NOT NULL, `category_id` INTEGER NOT NULL, PRIMARY KEY(`product_id`, `category_id`), FOREIGN KEY(`product_id`) REFERENCES `product`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`category_id`) REFERENCES `category`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
                connection.execSQL("INSERT INTO `product_category_junction_new` (product_id, category_id) SELECT product_id, category_id FROM product_category_junction")
                connection.execSQL("DROP TABLE product_category_junction")
                connection.execSQL("ALTER TABLE product_category_junction_new RENAME TO product_category_junction")
                connection.execSQL("CREATE INDEX IF NOT EXISTS `index_product_category_junction_product_id` ON `product_category_junction` (`product_id`)")
                connection.execSQL("CREATE INDEX IF NOT EXISTS `index_product_category_junction_category_id` ON `product_category_junction` (`category_id`)")

                // 6. variant: Add unique index for name
                connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_variant_name` ON `variant` (`name`)")

                // 7. variant_junction: Recreate to change PK and add FKs
                connection.execSQL("CREATE TABLE IF NOT EXISTS `variant_junction_new` (`product_id` INTEGER NOT NULL, `variant_id` INTEGER NOT NULL, PRIMARY KEY(`product_id`, `variant_id`), FOREIGN KEY(`product_id`) REFERENCES `product`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`variant_id`) REFERENCES `variant`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
                connection.execSQL("INSERT INTO `variant_junction_new` (product_id, variant_id) SELECT product_id, variant_id FROM variant_junction")
                connection.execSQL("DROP TABLE variant_junction")
                connection.execSQL("ALTER TABLE variant_junction_new RENAME TO variant_junction")
                connection.execSQL("CREATE INDEX IF NOT EXISTS `index_variant_junction_product_id` ON `variant_junction` (`product_id`)")
                connection.execSQL("CREATE INDEX IF NOT EXISTS `index_variant_junction_variant_id` ON `variant_junction` (`variant_id`)")

                // 8. bundle_product: Add unique index for (bundle_id, product_id)
                connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_bundle_product_bundle_id_product_id` ON `bundle_product` (`bundle_id`, `product_id`)")
            }
        }
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
