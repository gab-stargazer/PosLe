package org.lelestacia.posle.data.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.lelestacia.posle.data.dao.BundleDao
import org.lelestacia.posle.data.entity.BundleEntity
import org.lelestacia.posle.data.entity.BundleProductEntity
import org.lelestacia.posle.domain.component.bundle_add_edit.BundleProductState
import org.lelestacia.posle.domain.model.Bundle
import org.lelestacia.posle.domain.model.BundleProduct
import org.lelestacia.posle.domain.repository.BundleRepository
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.Util
import kotlin.time.Clock

class BundleRepositoryImpl(
    private val bundleDao: BundleDao
) : BundleRepository {

    override suspend fun insertBundle(
        bundleName: Name,
        bundleProducts: List<BundleProductState>
    ) {
        val bundle = BundleEntity(
            id = 0,
            name = bundleName,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )

        val bundleId = bundleDao.insertBundleAndGetId(bundle)
        val bundleProductsEntity = bundleProducts.map { bundleProduct ->
            BundleProductEntity(
                id = 0,
                name = bundleProduct.product.name,
                bundleId = bundleId.toInt(),
                productId = bundleProduct.product.id,
                quantity = Amount(bundleProduct.quantity.toFloat()),
                unit = bundleProduct.product.unit,
                sellPrice = Price(bundleProduct.sellPrice.toBigDecimal()),
                createdAt = Clock.System.now().toEpochMilliseconds(),
                updatedAt = null
            )
        }

        bundleDao.insertBundleProducts(bundleProductsEntity)
    }

    override fun readBundleByName(bundleName: String): Flow<PagingData<Bundle>> {
        return Pager(
            config = Util.pagingConfig,
            pagingSourceFactory = { bundleDao.readAllBundlesByName(bundleName) }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                Bundle(
                    id = entity.bundle.id,
                    name = entity.bundle.name,
                    imageUri = entity.bundle.imageUri,
                    bundleProducts = entity.products.map { bundleProduct ->
                        BundleProduct(
                            productId = bundleProduct.bundleProduct.productId,
                            productName = bundleProduct.bundleProduct.name,
                            skuNumber = bundleProduct.product.skuNumber,
                            imageUri = bundleProduct.product.imageUri,
                            sellPrice = bundleProduct.bundleProduct.sellPrice,
                            buyPrice = bundleProduct.buyPriceHistorical.maxBy { it.createdAt }.price,
                            quantity = bundleProduct.bundleProduct.quantity,
                            unit = bundleProduct.product.unit,
                            createdAt = bundleProduct.bundleProduct.createdAt,
                            updatedAt = bundleProduct.bundleProduct.updatedAt
                        )
                    },
                    createdAt = entity.bundle.createdAt,
                    updatedAt = entity.bundle.updatedAt
                )
            }
        }
    }
}