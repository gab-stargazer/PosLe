package org.lelestacia.posle.data.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.filter
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
import org.lelestacia.posle.util.FileStorage
import org.lelestacia.posle.util.Util.pagingConfig
import java.math.BigDecimal
import kotlin.time.Clock

class ProductRepositoryImpl(
    private val storage: FileStorage,
    private val productDao: ProductDao,
    private val variantDao: VariantDao,
    private val stockDao: StockDao
) : ProductRepository {

    override suspend fun addProduct(product: Product, imageByteArray: ByteArray?) {
        val newImageUri = imageByteArray?.let { imageByteArray ->
            storage.saveImage(fileName = "${product.name.value}.png", imageByteArray)
        }

        val entity = ProductEntity(
            id = 0,
            name = product.name,
            unit = product.unit,
            skuNumber = product.skuNumber,
            imageUri = newImageUri,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )

        val productId = productDao.addProduct(entity).toInt()

        productDao.addBuyPrice(
            ProductBuyPriceEntity(
                productId = productId,
                price = product.buyPrice,
                changeType = PriceChangeType.ProductCreation,
                createdAt = Clock.System.now().toEpochMilliseconds()
            )
        )

        productDao.addSellPrice(
            ProductSellPriceEntity(
                productId = productId,
                price = product.sellPrice,
                changeType = PriceChangeType.ProductCreation,
                createdAt = Clock.System.now().toEpochMilliseconds()
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

    override fun readProductsWithLowStock(searchQuery: String): Flow<PagingData<Product>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { productDao.readProductWithLowStocks(searchQuery) }
        ).flow.map { it.filter { entity -> entity.stock
            .sumOf { stockMovement -> stockMovement.amount.value } < 12.toBigDecimal() }
            .map { entity -> entity.toDomain() } }
    }

    override fun readProductsByName(searchQuery: String): Flow<PagingData<Product>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { productDao.readProductWithVariants(searchQuery) }
        ).flow.map { it.map { entity -> entity.toDomain() } }
    }

    override fun readProductWithoutCategories(searchQuery: String): Flow<PagingData<Product>> {
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
    ) {
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
                createdAt = Clock.System.now().toEpochMilliseconds()
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
                    productId = product.id,
                    price = product.buyPrice,
                    changeType = PriceChangeType.Adjustment,
                    createdAt = Clock.System.now().toEpochMilliseconds()
                )
            )
        }

        if (product.sellPrice != productDao.getLatestSellPriceByProductId(product.id).price) {
            productDao.addSellPrice(
                ProductSellPriceEntity(
                    productId = product.id,
                    price = product.sellPrice,
                    changeType = PriceChangeType.Adjustment,
                    createdAt = Clock.System.now().toEpochMilliseconds()
                )
            )
        }
    }

    override fun readProductWithCategories(
        searchQuery: String,
        categoryId: Int
    ): PagingSource<Int, ProductWithVariantsAndStock> {
        return productDao.readProductWithCategories(searchQuery, categoryId)
    }

    override fun readProductNotInCategory(
        searchQuery: String,
        categoryId: Int
    ): PagingSource<Int, ProductWithVariantsAndStock> {
        return productDao.readProductNotInCategory(searchQuery, categoryId)
    }

    override fun readProductBuyPriceHistory(productId: Int): Flow<List<ProductPriceHistory>> {
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

    override fun readProductSellPriceHistory(productId: Int): Flow<List<ProductPriceHistory>> {
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

    override fun readAvailableProducts(searchQuery: String): Flow<List<Product>> {
        return productDao.getAvailableProducts(searchQuery)
            .map { it.map(ProductWithVariantsAndStock::toDomain) }
    }

    override suspend fun getProductAvailability(productId: Int): BigDecimal {
        return stockDao.getStockByProductId(productId)
    }

    override suspend fun deleteProduct(product: Product) {
        storage.deleteImage(fileName = "${product.name.value}.png")
        productDao.delete(product.id)
    }
}
