package org.lelestacia.posle.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.lelestacia.posle.data.dao.ProductDao
import org.lelestacia.posle.data.dao.VariantDao
import org.lelestacia.posle.data.entity.ProductEntity
import org.lelestacia.posle.data.entity.VariantEntity
import org.lelestacia.posle.data.entity.VariantJunction
import org.lelestacia.posle.data.entity.toDomain
import org.lelestacia.posle.data.entity.toEntity
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.domain.model.toDomain
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.util.FileStorage
import kotlin.time.Clock


class ProductRepositoryImpl(
    private val storage: FileStorage,
    private val productDao: ProductDao,
    private val variantDao: VariantDao,
) : ProductRepository {

    override suspend fun addProduct(product: Product, imageByteArray: ByteArray?) {
        val newImageUri = imageByteArray?.let {
            storage.saveImage(fileName = "${product.name.value}.png", imageByteArray)
        }

        val productId = productDao.addProduct(
            ProductEntity(
                id = 0,
                name = product.name,
                price = product.price,
                unit = product.unit,
                imageUri = newImageUri,
                createdAt = Clock.System.now().toEpochMilliseconds()
            )
        ).toInt()

        product.variants.forEach { variant ->
            variantDao.insertVariantToProduct(
                VariantJunction(
                    productId = productId,
                    variantId = variant.id
                )
            )
        }
    }

    override suspend fun addVariant(variant: Variant) {
        variantDao.insertVariant(variant.toEntity())
    }

    override fun readProduct(searchQuery: String): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                prefetchDistance = 10,
                initialLoadSize = 30
            ),
            pagingSourceFactory = {
                productDao.readProductWithVariants(searchQuery)
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override fun readVariant(): Flow<PagingData<Variant>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                prefetchDistance = 10,
                initialLoadSize = 30
            ),
            pagingSourceFactory = {
                variantDao.readVariant()
            }
        ).flow.map { pagingData ->
            pagingData.map(VariantEntity::toDomain)
        }
    }

    override suspend fun updateProduct(
        product: Product,
        imageByteArray: ByteArray?
    ) {
        var newImageUri = imageByteArray?.let {
            storage.saveImage(fileName = "${product.name.value}.png", imageByteArray)
        }

        if (product.imageUri != null && imageByteArray == null) {
            newImageUri = product.imageUri
        }

        productDao.update(
            ProductEntity(
                id = product.id,
                name = product.name,
                price = product.price,
                unit = product.unit,
                imageUri = newImageUri,
                createdAt = Clock.System.now().toEpochMilliseconds()
            )
        )

        //  SHOULD BE UPDATED SOON, NOT AN EFFICIENT WAY ON DB USAGE
        variantDao.clearProductVariants(product.id)
        product.variants.forEach { variant ->
            variantDao.insertVariantToProduct(
                VariantJunction(
                    id = 0,
                    productId = product.id,
                    variantId = variant.id
                )
            )
        }
    }

    override suspend fun deleteProduct(product: Product) {
        storage.deleteImage(fileName = "${product.name.value}.png")
        productDao.delete(product.id)
    }
}