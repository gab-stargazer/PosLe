package org.lelestacia.posle.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.RoomWarnings
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.lelestacia.posle.data.entity.ProductBuyPriceEntity
import org.lelestacia.posle.data.entity.ProductEntity
import org.lelestacia.posle.data.entity.ProductSellPriceEntity
import org.lelestacia.posle.data.entity.ProductWithVariantsAndStock

/**
 * Data Access Object for Product related entities.
 *
 * Handles complex joins for products including their variants, pricing history,
 * stock movements, and category associations.
 */
@Dao
interface ProductDao {

    @Insert
    suspend fun addProduct(product: ProductEntity): Long

    @Insert
    suspend fun addSellPrice(price: ProductSellPriceEntity)

    @Insert
    suspend fun addBuyPrice(price: ProductBuyPriceEntity)

    @Transaction
    @Query("SELECT * FROM PRODUCT WHERE id = :id")
    suspend fun getProductById(id: Int): ProductWithVariantsAndStock

    @Query("SELECT * FROM product WHERE name LIKE '%' || :name || '%' ORDER BY name ASC")
    fun readProductsByName(name: String = ""): PagingSource<Int, ProductEntity>

    @Transaction
    @Query(
        """
            SELECT DISTINCT product.* FROM product
            LEFT JOIN product_category_junction ON product.id = product_category_junction.product_id
            INNER JOIN product_buy_price ON product.id = product_buy_price.product_id
            INNER JOIN product_sell_price ON product.id = product_sell_price.product_id
            LEFT JOIN stock_movement ON product.id = stock_movement.product_id
            WHERE product_category_junction.category_id IS NULL 
            AND sku_number = :skuNumber
            ORDER BY name ASC
            LIMIT 1
        """
    )
    suspend fun getProductBySkuNumber(skuNumber: String): ProductWithVariantsAndStock?

    @Query(
        """
            SELECT * FROM product_buy_price
            WHERE product_id = :productId
            ORDER BY created_at DESC
            LIMIT 3
        """
    )
    fun readProductBuyPriceHistory(productId: Int): Flow<List<ProductBuyPriceEntity>>

    @Query(
        """
            SELECT * FROM product_sell_price
            WHERE product_id = :productId
            ORDER BY created_at DESC
            LIMIT 3
        """
    )
    fun readProductSellPriceHistory(productId: Int): Flow<List<ProductBuyPriceEntity>>

    @Query("SELECT * FROM product WHERE id = :id")
    suspend fun readProductById(id: Int): ProductEntity?

    @Transaction
    @Query("SELECT * FROM product WHERE name LIKE '%' || :name || '%' ORDER BY name ASC")
    fun readProductWithVariants(name: String = ""): PagingSource<Int, ProductWithVariantsAndStock>

    @Transaction
    @Query(
        """
            SELECT DISTINCT product.* FROM product
            LEFT JOIN product_category_junction ON product.id = product_category_junction.product_id
            INNER JOIN product_buy_price ON product.id = product_buy_price.product_id
            INNER JOIN product_sell_price ON product.id = product_sell_price.product_id
            LEFT JOIN stock_movement ON product.id = stock_movement.product_id
            WHERE product_category_junction.category_id IS NULL 
            AND name LIKE '%' || :searchQuery || '%'
            ORDER BY name ASC
        """
    )
    fun readProductWithoutCategories(searchQuery: String): PagingSource<Int, ProductWithVariantsAndStock>

    @Transaction
    @Query(
        """
            SELECT DISTINCT product.* FROM product
            LEFT JOIN product_category_junction ON product.id = product_category_junction.product_id
            INNER JOIN product_buy_price ON product.id = product_buy_price.product_id
            INNER JOIN product_sell_price ON product.id = product_sell_price.product_id
            LEFT JOIN stock_movement ON product.id = stock_movement.product_id
            WHERE name LIKE '%' || :searchQuery || '%'
            ORDER BY name ASC
        """
    )
    fun readProductWithLowStocks(searchQuery: String): PagingSource<Int, ProductWithVariantsAndStock>

    @Transaction
    @Query(
        value = """
            SELECT DISTINCT product.* FROM product
            INNER JOIN product_category_junction ON product.id = product_category_junction.product_id
            INNER JOIN product_buy_price ON product.id = product_buy_price.product_id 
            INNER JOIN product_sell_price ON product.id = product_sell_price.product_id
            LEFT JOIN stock_movement ON product.id = stock_movement.product_id
            WHERE product_category_junction.category_id = :categoryId
            AND name LIKE '%' || :searchQuery || '%'
            ORDER BY name ASC
        """
    )
    fun readProductWithCategories(
        searchQuery: String,
        categoryId: Int
    ): PagingSource<Int, ProductWithVariantsAndStock>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        value = """
            SELECT * FROM product 
            WHERE id NOT IN (
                SELECT product_id FROM product_category_junction WHERE category_id = :categoryId
            )
            AND name LIKE '%' || :searchQuery || '%'
            ORDER BY name ASC
        """
    )
    fun readProductNotInCategory(
        searchQuery: String,
        categoryId: Int
    ): PagingSource<Int, ProductWithVariantsAndStock>

    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
            SELECT *
            FROM product
            LEFT JOIN product_category_junction
            ON product.id = product_category_junction.product_id
            LEFT JOIN stock
            ON product.id = stock.product_id
            WHERE product.name LIKE '%' || :searchQuery || '%'
            ORDER BY product.name ASC
        """
    )
    fun getAvailableProducts(searchQuery: String = ""): Flow<List<ProductWithVariantsAndStock>>

    @Query("SELECT * FROM product_sell_price WHERE product_id = :productId ORDER BY created_at DESC LIMIT 1")
    suspend fun getLatestSellPriceByProductId(productId: Int): ProductSellPriceEntity

    @Query("SELECT * FROM product_buy_price WHERE product_id = :productId ORDER BY created_at DESC LIMIT 1")
    suspend fun getLatestBuyPriceByProductId(productId: Int): ProductBuyPriceEntity

    @Transaction
    @Query("SELECT * FROM product ORDER BY name ASC")
    fun readAllProductsWithVariantsAndStock(): Flow<List<ProductWithVariantsAndStock>>

    @Update
    suspend fun update(product: ProductEntity)

    @Query("DELETE FROM product WHERE id = :id")
    suspend fun delete(id: Int)
}