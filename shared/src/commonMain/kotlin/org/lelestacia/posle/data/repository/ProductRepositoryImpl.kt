package org.lelestacia.posle.data.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.filter
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.lelestacia.posle.data.dao.CategoryDao
import org.lelestacia.posle.data.dao.ProductDao
import org.lelestacia.posle.data.dao.StockDao
import org.lelestacia.posle.data.dao.VariantDao
import org.lelestacia.posle.data.entity.PriceChangeType
import org.lelestacia.posle.data.entity.ProductBuyPriceEntity
import org.lelestacia.posle.data.entity.ProductEntity
import org.lelestacia.posle.data.entity.ProductSellPriceEntity
import org.lelestacia.posle.data.entity.ProductWithVariantsAndStock
import org.lelestacia.posle.data.entity.VariantJunction
import org.lelestacia.posle.data.entity.toDomain
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.ProductPriceHistory
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.FileStorage
import org.lelestacia.posle.util.UuidProvider
import org.lelestacia.posle.util.Util.pagingConfig
import java.math.BigDecimal
import kotlin.time.Clock

class ProductRepositoryImpl(
    private val storage: FileStorage,
    private val productDao: ProductDao,
    private val variantDao: VariantDao,
    private val stockDao: StockDao,
    private val categoryDao: CategoryDao,
    private val transactionRunner: org.lelestacia.posle.data.TransactionRunner
) : ProductRepository {

    override suspend fun createProduct(product: Product, imageByteArray: ByteArray?) = transactionRunner.runTransaction {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val newImageUri = imageByteArray?.let { bytes ->
            storage.saveImage(fileName = "${product.name.value}.png", bytes)
        }

        val entity = ProductEntity(
            id = UuidProvider.newUuid(),
            name = product.name,
            unit = product.unit,
            skuNumber = product.skuNumber,
            imageUri = newImageUri,
            createdAt = currentTime
        )

        val productId = entity.id

        productDao.addProduct(entity)

        productDao.addBuyPrice(
            ProductBuyPriceEntity(
                id = UuidProvider.newUuid(),
                productId = productId,
                price = product.buyPrice,
                changeType = PriceChangeType.ProductCreation,
                createdAt = currentTime
            )
        )

        productDao.addSellPrice(
            ProductSellPriceEntity(
                id = UuidProvider.newUuid(),
                productId = productId,
                price = product.sellPrice,
                changeType = PriceChangeType.ProductCreation,
                createdAt = currentTime
            )
        )

        product.variants.forEach { variant ->
            variantDao.insertVariantToProduct(
                VariantJunction(
                    productId = productId,
                    variantId = variant.id
                )
            )
        }
    }

    override suspend fun getProductBySkuNumber(skuNumber: String): Product? {
        return productDao.getProductBySkuNumber(skuNumber)?.toDomain()
    }

    override fun getProductsWithLowStock(searchQuery: String): Flow<PagingData<Product>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { productDao.readProductWithLowStocks(searchQuery) }
        ).flow.map { it.filter { entity -> entity.stock
            .sumOf { stockMovement -> stockMovement.amount.value } < 12.toBigDecimal() }
            .map { entity -> entity.toDomain() } }
    }

    override fun getProductsByName(searchQuery: String): Flow<PagingData<Product>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { productDao.readProductWithVariants(searchQuery) }
        ).flow.map { it.map { entity -> entity.toDomain() } }
    }

    override fun getAllProducts(): Flow<List<Product>> {
        return productDao.readAllProductsWithVariantsAndStock().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun importProducts(products: List<Product>) =
        transactionRunner.runTransaction {
            val currentTime = Clock.System.now().toEpochMilliseconds()
            val allCategories = categoryDao.getAllCategories().first().associateBy { it.name.value }
            val categoryMap = allCategories.toMutableMap()

            products.forEach { product ->
                // 1. Find or Create Product (match existing records by SKU — imported ids are meaningless)
                var productId = ""
                val existingProduct = if (product.skuNumber != null) {
                    productDao.getProductBySkuNumber(product.skuNumber.value)?.product
                } else {
                    null
                }

                if (existingProduct != null) {
                    productId = existingProduct.id
                    productDao.update(
                        ProductEntity(
                            id = productId,
                            name = product.name,
                            unit = product.unit,
                            skuNumber = product.skuNumber,
                            imageUri = existingProduct.imageUri, // Preserve image
                            createdAt = existingProduct.createdAt,
                            updatedAt = currentTime
                        )
                    )
                } else {
                    val newProduct = ProductEntity(
                        id = UuidProvider.newUuid(),
                        name = product.name,
                        unit = product.unit,
                        skuNumber = product.skuNumber,
                        imageUri = null,
                        createdAt = currentTime
                    )
                    productId = newProduct.id
                    productDao.addProduct(newProduct)
                }

                // 2. Pricing
                val latestBuyPrice = productDao.getLatestBuyPriceByProductId(productId).price
                if (product.buyPrice != latestBuyPrice) {
                    productDao.addBuyPrice(
                        ProductBuyPriceEntity(
                            id = UuidProvider.newUuid(),
                            productId = productId,
                            price = product.buyPrice,
                            changeType = PriceChangeType.Adjustment,
                            createdAt = currentTime
                        )
                    )
                }

                val latestSellPrice = productDao.getLatestSellPriceByProductId(productId).price
                if (product.sellPrice != latestSellPrice) {
                    productDao.addSellPrice(
                        ProductSellPriceEntity(
                            id = UuidProvider.newUuid(),
                            productId = productId,
                            price = product.sellPrice,
                            changeType = PriceChangeType.Adjustment,
                            createdAt = currentTime
                        )
                    )
                }

                // 3. Categories
                product.categories.forEach { category ->
                    var categoryEntity = categoryMap[category.name.value]
                    if (categoryEntity == null) {
                        val newCategory = org.lelestacia.posle.data.entity.CategoryEntity(
                            id = UuidProvider.newUuid(),
                            name = category.name
                        )
                        categoryDao.insertCategory(newCategory)
                        categoryEntity = newCategory
                        categoryMap[category.name.value] = categoryEntity
                    }

                    // Try to insert connection, ignore if exists
                    try {
                        categoryDao.insertConnection(
                            org.lelestacia.posle.data.entity.ProductCategoryJunction(
                                productId = productId,
                                categoryId = categoryEntity.id
                            )
                        )
                    } catch (e: Exception) {
                        // Link might already exist
                    }
                }

                // 4. Stock
                val currentStock = stockDao.getStockByProductId(productId)
                val diff = product.stock.value.subtract(currentStock)
                if (diff.compareTo(BigDecimal.ZERO) != 0) {
                    val movementType = if (diff.compareTo(BigDecimal.ZERO) > 0) {
                        org.lelestacia.posle.data.entity.StockMovementType.AdjustmentIncrease
                    } else {
                        org.lelestacia.posle.data.entity.StockMovementType.AdjustmentDecrease
                    }

                    stockDao.insertStockMovement(
                        org.lelestacia.posle.data.entity.StockMovementEntity(
                            id = UuidProvider.newUuid(),
                            productId = productId,
                            productName = product.name,
                            productUnit = product.unit,
                            movementType = movementType,
                            amount = Amount(diff.abs()),
                            note = "Import from Excel",
                            createdAt = currentTime
                        )
                    )
                }
            }
        }

    override fun getProductWithoutCategories(searchQuery: String): Flow<PagingData<Product>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { productDao.readProductWithoutCategories(searchQuery) }
        ).flow.map { it.map { entity -> entity.toDomain() } }
    }

    override suspend fun updateProduct(
        product: Product,
        variantsToAdd: List<Variant>,
        variantsToRemove: List<Variant>,
        imageByteArray: ByteArray?
    ) = transactionRunner.runTransaction {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val finalImageUri = when {
            imageByteArray != null -> storage.saveImage(
                fileName = "${product.name.value}.png",
                imageByteArray
            )

            product.imageUri != null -> product.imageUri
            else -> product.imageUri
        }

        productDao.update(
            ProductEntity(
                id = product.id,
                name = product.name,
                unit = product.unit,
                skuNumber = product.skuNumber,
                imageUri = finalImageUri,
                createdAt = currentTime
            )
        )

        variantsToAdd.forEach { variant ->
            variantDao.insertVariantToProduct(
                VariantJunction(
                    productId = product.id,
                    variantId = variant.id
                )
            )
        }

        variantsToRemove.forEach { variant ->
            variantDao.deleteVariantToProduct(
                variantId = variant.id,
                productId = product.id
            )
        }


        if (product.buyPrice != productDao.getLatestBuyPriceByProductId(product.id).price) {
            productDao.addBuyPrice(
                ProductBuyPriceEntity(
                    id = UuidProvider.newUuid(),
                    productId = product.id,
                    price = product.buyPrice,
                    changeType = PriceChangeType.Adjustment,
                    createdAt = currentTime
                )
            )
        }

        if (product.sellPrice != productDao.getLatestSellPriceByProductId(product.id).price) {
            productDao.addSellPrice(
                ProductSellPriceEntity(
                    id = UuidProvider.newUuid(),
                    productId = product.id,
                    price = product.sellPrice,
                    changeType = PriceChangeType.Adjustment,
                    createdAt = currentTime
                )
            )
        }
    }

    override fun getProductWithCategories(
        searchQuery: String,
        categoryId: String
    ): PagingSource<Int, ProductWithVariantsAndStock> {
        return productDao.readProductWithCategories(searchQuery, categoryId)
    }

    override fun getProductNotInCategory(
        searchQuery: String,
        categoryId: String
    ): PagingSource<Int, ProductWithVariantsAndStock> {
        return productDao.readProductNotInCategory(searchQuery, categoryId)
    }

    override fun getProductBuyPriceHistory(productId: String): Flow<List<ProductPriceHistory>> {
        return productDao.readProductBuyPriceHistory(productId).map { list ->
            list.map {
                ProductPriceHistory(
                    it.id,
                    price = it.price,
                    changes = BigDecimal.ZERO,
                    createdAt = it.createdAt
                )
            }
        }
    }

    override fun getProductSellPriceHistory(productId: String): Flow<List<ProductPriceHistory>> {
        return productDao.readProductSellPriceHistory(productId).map { list ->
            list.map {
                ProductPriceHistory(
                    it.id,
                    price = it.price,
                    changes = BigDecimal.ZERO,
                    createdAt = it.createdAt
                )
            }
        }
    }

    override fun getAvailableProducts(searchQuery: String): Flow<List<Product>> {
        return productDao.getAvailableProducts(searchQuery)
            .map { it.map(ProductWithVariantsAndStock::toDomain) }
    }

    override suspend fun getProductAvailability(productId: String): BigDecimal {
        return stockDao.getStockByProductId(productId)
    }

    override suspend fun deleteProduct(product: Product) {
        storage.deleteImage(fileName = "${product.name.value}.png")
        productDao.delete(product.id)
    }
}
