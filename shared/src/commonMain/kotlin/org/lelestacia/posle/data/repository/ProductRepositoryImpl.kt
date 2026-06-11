package org.lelestacia.posle.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.lelestacia.posle.data.dao.ProductDao
import org.lelestacia.posle.data.entity.ProductEntity
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.util.FileStorage
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.Unit
import kotlin.time.Clock


class ProductRepositoryImpl(
    private val storage: FileStorage,
    private val dao: ProductDao
) : ProductRepository {

    override suspend fun addProduct(product: Product, imageByteArray: ByteArray?) {
        val newImageUri = imageByteArray?.let {
            storage.saveImage(fileName = "${product.name.value}.png", imageByteArray)
        }

        dao.addProduct(
            ProductEntity(
                id = 0,
                name = product.name.value,
                price = product.price.value,
                unit = product.unit.value,
                imageUri = newImageUri,
                isProductVolatile = product.isProductVolatile,
                createdAt = Clock.System.now().toEpochMilliseconds()
            )
        )
    }

    override fun readProduct(searchQuery: String): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                prefetchDistance = 10,
                initialLoadSize = 30
            ),
            pagingSourceFactory = {
                dao.readProduct(searchQuery)
            }
        ).flow.map { pagingData ->
            pagingData.map {
                Product(
                    id = it.id,
                    name = Name(it.name),
                    price = Price(it.price),
                    unit = Unit(it.unit),
                    imageUri = it.imageUri,
                    isProductVolatile = it.isProductVolatile
                )
            }
        }
    }

    override suspend fun updateProduct(
        product: Product,
        imageByteArray: ByteArray?
    ) {
        val newImageUri = imageByteArray?.let {
            storage.saveImage(fileName = "${product.name.value}.png", imageByteArray)
        }

        dao.update(
            ProductEntity(
                id = 0,
                name = product.name.value,
                price = product.price.value,
                unit = product.unit.value,
                imageUri = newImageUri,
                isProductVolatile = product.isProductVolatile,
                createdAt = Clock.System.now().toEpochMilliseconds()
            )
        )
    }

    override suspend fun deleteProduct(product: Product) {
        storage.deleteImage(fileName = "${product.name.value}.png")
        dao.delete(product.id)
    }
}