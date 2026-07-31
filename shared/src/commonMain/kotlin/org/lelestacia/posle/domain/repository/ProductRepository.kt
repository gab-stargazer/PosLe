package org.lelestacia.posle.domain.repository

import androidx.paging.PagingData
import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import org.lelestacia.posle.data.entity.ProductWithVariantsAndStock
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.ProductPriceHistory
import org.lelestacia.posle.domain.model.Variant
import java.math.BigDecimal

/**
 * Repository interface for managing the product catalog and related pricing/stock data.
 *
 * This repository is the source of truth for:
 * - Product definition and categorization.
 * - Pricing history (buy and sell prices).
 * - Real-time stock availability queries.
 *
 * @see org.lelestacia.posle.domain.model.Product
 */
interface ProductRepository {
    /**
     * Creates a new product and initializes its price history.
     *
     * @param product The [Product] data to be saved.
     * @param imageByteArray Optional raw bytes for the product image. If provided, the image is saved to local storage.
     * @throws Exception if product creation fails (e.g., duplicate SKU).
     */
    suspend fun createProduct(product: Product, imageByteArray: ByteArray?)

    /**
     * Retrieves a single product using its SKU (Stock Keeping Unit).
     *
     * @param skuNumber The unique SKU string.
     * @return The [Product] if found, null otherwise.
     */
    suspend fun getProductBySkuNumber(skuNumber: String): Product?

    /**
     * Retrieves a paginated list of products whose names match the search query.
     *
     * @param searchQuery The text to filter products by.
     * @return A [Flow] of [PagingData] containing the matched [Product]s.
     */
    fun getProductsByName(searchQuery: String): Flow<PagingData<Product>>

    /**
     * Retrieves a paginated list of products where current stock is below the defined threshold.
     *
     * @param searchQuery Optional query to filter the low-stock items.
     * @return A [Flow] of [PagingData] containing low-stock [Product]s.
     */
    fun getProductsWithLowStock(searchQuery: String): Flow<PagingData<Product>>

    /**
     * Retrieves products that have not been assigned to any category.
     *
     * @param searchQuery Optional filter query.
     * @return A [Flow] of [PagingData] containing uncategorized [Product]s.
     */
    fun getProductWithoutCategories(searchQuery: String): Flow<PagingData<Product>>

    /**
     * Provides a [PagingSource] for products within a specific category.
     *
     * @param searchQuery Filter query.
     * @param categoryId The unique ID of the category.
     * @return A [PagingSource] for efficient scrolling and database paging.
     */
    fun getProductWithCategories(
        searchQuery: String,
        categoryId: Int
    ): PagingSource<Int, ProductWithVariantsAndStock>

    /**
     * Provides a [PagingSource] for products that do NOT belong to the specified category.
     *
     * @param searchQuery Filter query.
     * @param categoryId The category ID to exclude.
     * @return A [PagingSource] of products available to be added to this category.
     */
    fun getProductNotInCategory(
        searchQuery: String,
        categoryId: Int
    ): PagingSource<Int, ProductWithVariantsAndStock>

    /**
     * Retrieves the chronological history of buy prices for a product.
     *
     * @param productId The ID of the product.
     * @return A [Flow] list of [ProductPriceHistory] entries.
     */
    fun getProductBuyPriceHistory(productId: Int): Flow<List<ProductPriceHistory>>

    /**
     * Retrieves the chronological history of sell prices for a product.
     *
     * @param productId The ID of the product.
     * @return A [Flow] list of [ProductPriceHistory] entries.
     */
    fun getProductSellPriceHistory(productId: Int): Flow<List<ProductPriceHistory>>

    /**
     * Returns a flow of all products currently considered "available" based on query.
     *
     * @param searchQuery Optional name filter.
     * @return A [Flow] list of available [Product]s.
     */
    fun getAvailableProducts(searchQuery: String = ""): Flow<List<Product>>

    /**
     * Calculates the current stock quantity for a product.
     *
     * @param productId The ID of the product.
     * @return The total current quantity as a [BigDecimal].
     */
    suspend fun getProductAvailability(productId: Int): BigDecimal

    /**
     * Updates an existing product's details and manages its variant associations.
     *
     * This operation handles:
     * 1. Updating base product info (name, unit, SKU).
     * 2. Saving a new image if provided.
     * 3. Linking new variants and unlinking removed ones.
     * 4. Recording price adjustments if buy/sell prices have changed.
     *
     * @param product The updated [Product] model.
     * @param variantsToAdd List of [Variant]s to associate.
     * @param variantsToRemove List of [Variant]s to remove.
     * @param imageByteArray Optional new raw image data.
     */
    suspend fun updateProduct(
        product: Product,
        variantsToAdd: List<Variant>,
        variantsToRemove: List<Variant>,
        imageByteArray: ByteArray?
    )

    /**
     * Retrieves all products with their variants and stock data.
     * Useful for export purposes.
     *
     * @return A [Flow] list of [Product]s.
     */
    fun getAllProducts(): Flow<List<Product>>

    /**
     * Imports a list of products into the database.
     *
     * @param products The list of products to import.
     */
    suspend fun importProducts(products: List<Product>)

    /**
     * Removes a product from the database and deletes its associated image file.
     *
     * @param product The [Product] to delete.
     */
    suspend fun deleteProduct(product: Product)
}
