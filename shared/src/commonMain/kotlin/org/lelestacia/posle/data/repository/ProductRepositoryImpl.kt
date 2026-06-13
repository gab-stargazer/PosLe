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
                name = product.name,
                price = product.price,
                unit = product.unit,
                imageUri = newImageUri,
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
                    name = it.name,
                    price = it.price,
                    unit = it.unit,
                    imageUri = it.imageUri
                )
            }
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

        dao.update(
            ProductEntity(
                id = product.id,
                name = product.name,
                price = product.price,
                unit = product.unit,
                imageUri = newImageUri,
                createdAt = Clock.System.now().toEpochMilliseconds()
            )
        )
    }

    override suspend fun deleteProduct(product: Product) {
        storage.deleteImage(fileName = "${product.name.value}.png")
        dao.delete(product.id)
    }
}