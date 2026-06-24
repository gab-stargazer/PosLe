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
import org.lelestacia.posle.data.converter.StockMovementTypeConverter
import org.lelestacia.posle.data.converter.TransactionVariantConverter
import org.lelestacia.posle.data.dao.CategoryDao
import org.lelestacia.posle.data.dao.ProductDao
import org.lelestacia.posle.data.dao.StockDao
import org.lelestacia.posle.data.dao.TransactionDao
import org.lelestacia.posle.data.dao.VariantDao
import org.lelestacia.posle.data.entity.CategoryEntity
import org.lelestacia.posle.data.entity.ProductBuyPriceEntity
import org.lelestacia.posle.data.entity.ProductCategoryJunction
import org.lelestacia.posle.data.entity.ProductEntity
import org.lelestacia.posle.data.entity.ProductSellPriceEntity
import org.lelestacia.posle.data.entity.StockEntity
import org.lelestacia.posle.data.entity.StockMovementEntity
import org.lelestacia.posle.data.entity.TransactionEntity
import org.lelestacia.posle.data.entity.TransactionItemEntity
import org.lelestacia.posle.data.entity.VariantEntity
import org.lelestacia.posle.data.entity.VariantJunction

@Database(
    entities = [
        ProductEntity::class,
        ProductSellPriceEntity::class,
        ProductBuyPriceEntity::class,
        TransactionEntity::class,
        TransactionItemEntity::class,
        VariantEntity::class,
        VariantJunction::class,
        CategoryEntity::class,
        ProductCategoryJunction::class,
        StockEntity::class,
        StockMovementEntity::class
    ],
    version = 4,
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

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL(
                    "ALTER TABLE transaction_item ADD COLUMN product_note TEXT DEFAULT NULL"
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(connection: SQLiteConnection) {
                // 1. Create the new price history table
                connection.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `product_price` (
                    `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    `product_id` INTEGER NOT NULL,
                    `price` TEXT NOT NULL,
                    `change_type` TEXT NOT NULL,
                    `created_at` INTEGER NOT NULL,
                    FOREIGN KEY(`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE)
                    """
                )
                connection.execSQL("CREATE INDEX IF NOT EXISTS `index_product_price_product_id` ON `product_price` (`product_id`)")
                connection.execSQL("CREATE INDEX IF NOT EXISTS `index_product_price_product_id_created_at` ON `product_price` (`product_id`, `created_at`)")

                // 2. Backfill: one ProductCreation row per existing product
                connection.execSQL(
                    """
                    INSERT INTO product_price (product_id, price, change_type, created_at)
                    SELECT id, price, 'ProductCreation', created_at FROM product
                    """
                )

                // 3. Recreate `product` without the `price` column
                connection.execSQL(
                    """
                    CREATE TABLE `product_new` (
                    `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    `name` TEXT NOT NULL,
                    `unit` TEXT NOT NULL,
                    `sku_number` TEXT,
                    `image_uri` TEXT,
                    `created_at` INTEGER NOT NULL,
                    `updated_at` INTEGER
                    )
                    """
                )
                connection.execSQL(
                    """
                    INSERT INTO product_new (id, name, unit, sku_number, image_uri, created_at, updated_at)
                    SELECT id, name, unit, sku_number, image_uri, created_at, updated_at FROM product
                    """
                )
                connection.execSQL("DROP TABLE product")
                connection.execSQL("ALTER TABLE product_new RENAME TO product")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(connection: SQLiteConnection) {
                // 1. Rename existing price table to product_sell_price
                connection.execSQL("ALTER TABLE product_price RENAME TO product_sell_price")

                connection.execSQL("DROP INDEX IF EXISTS index_product_price_product_id")
                connection.execSQL("DROP INDEX IF EXISTS index_product_price_product_id_created_at")
                connection.execSQL("CREATE INDEX IF NOT EXISTS index_product_sell_price_product_id ON product_sell_price(product_id)")
                connection.execSQL("CREATE INDEX IF NOT EXISTS index_product_sell_price_product_id_created_at ON product_sell_price(product_id, created_at)")

                // 2. Create new product_buy_price table
                connection.execSQL("""
            CREATE TABLE IF NOT EXISTS `product_buy_price` (
                `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                `product_id` INTEGER NOT NULL,
                `price` TEXT NOT NULL,
                `change_type` TEXT NOT NULL,
                `created_at` INTEGER NOT NULL,
                FOREIGN KEY(`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE
            )
        """)
                connection.execSQL("CREATE INDEX IF NOT EXISTS index_product_buy_price_product_id ON product_buy_price(product_id)")
                connection.execSQL("CREATE INDEX IF NOT EXISTS index_product_buy_price_product_id_created_at ON product_buy_price(product_id, created_at)")

                // 3. Seed product_buy_price with only the LATEST sell price per product,
                //    forcing change_type to ProductCreation
                connection.execSQL("""
            INSERT INTO product_buy_price (product_id, price, change_type, created_at)
            SELECT sp.product_id, sp.price, 'ProductCreation', sp.created_at
            FROM product_sell_price sp
            WHERE sp.id = (
                SELECT id FROM product_sell_price
                WHERE product_id = sp.product_id
                ORDER BY created_at DESC, id DESC
                LIMIT 1
            )
        """)
            }
        }
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<PosLeDB>
