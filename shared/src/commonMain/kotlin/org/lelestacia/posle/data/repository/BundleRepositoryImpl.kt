package org.lelestacia.posle.data.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.lelestacia.posle.data.TransactionRunner
import org.lelestacia.posle.data.dao.BundleDao
import org.lelestacia.posle.data.entity.BundleEntity
import org.lelestacia.posle.data.entity.BundleProductEntity
import org.lelestacia.posle.domain.component.bundle_add_edit.BundleProductState
import org.lelestacia.posle.domain.model.Bundle
import org.lelestacia.posle.domain.model.BundleProduct
import org.lelestacia.posle.domain.repository.BundleRepository
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.FileStorage
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.Util
import kotlin.time.Clock

class BundleRepositoryImpl(
    private val bundleDao: BundleDao,
    private val transactionRunner: TransactionRunner,
    private val storage: FileStorage
) : BundleRepository {

    override suspend fun insertBundle(
        bundleName: Name,
        bundleProducts: List<BundleProductState>,
        imageByteArray: ByteArray?
    ) {
        val bundle = BundleEntity(
            id = 0,
            name = bundleName,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )

        val bundleId = bundleDao.insertBundleAndGetId(bundle).toInt()

        val imageUri = imageByteArray?.let { bytes ->
            storage.saveImage(fileName = "bundle_$bundleId.png", bytes)
        }

        if (imageUri != null) {
            bundleDao.updateBundle(
                bundle.copy(
                    id = bundleId,
                    imageUri = imageUri
                )
            )
        }

        val bundleProductsEntity = bundleProducts.map { bundleProduct ->
            BundleProductEntity(
                id = 0,
                name = bundleProduct.product.name,
                bundleId = bundleId,
                productId = bundleProduct.product.id,
                quantity = Amount(bundleProduct.quantity.toBigDecimal()),
                unit = bundleProduct.product.unit,
                sellPrice = Price(bundleProduct.sellPrice.toBigDecimal()),
                createdAt = Clock.System.now().toEpochMilliseconds(),
                updatedAt = null
            )
        }

        bundleDao.insertBundleProducts(bundleProductsEntity)
    }

    override suspend fun updateBundle(
        bundleId: Int,
        bundleName: Name,
        bundleProducts: List<BundleProductState>,
        imageUri: String?,
        imageByteArray: ByteArray?
    ) {
        val currentTimeAsTimestamp = Clock.System.now().toEpochMilliseconds()

        val existingBundleWithProducts = bundleDao.getBundleWithProductsById(bundleId)
            ?: return

        val finalImageUri = when {
            imageByteArray != null -> storage.saveImage(
                fileName = "bundle_$bundleId.png",
                imageByteArray
            )

            imageUri == null -> null

            else -> existingBundleWithProducts.bundle.imageUri
        }

        transactionRunner.runTransaction {
            val bundle = BundleEntity(
                id = bundleId,
                name = bundleName,
                imageUri = finalImageUri,
                createdAt = existingBundleWithProducts.bundle.createdAt,
                updatedAt = Clock.System.now().toEpochMilliseconds()
            )

            bundleDao.updateBundle(bundle)

            // Surgical Update of Products
            val existingProductsMap = existingBundleWithProducts.products
                .associateBy { it.bundleProduct.productId }

            val newProductsMap = bundleProducts
                .associateBy { it.product.id }

            // 1. Delete removed products
            val removedProductIds = existingProductsMap.keys - newProductsMap.keys

            if (removedProductIds.isNotEmpty()) {
                bundleDao.deleteBundleProducts(bundleId, removedProductIds.toList())
            }

            // 2. Update existing or Insert new products
            val toUpdate = mutableListOf<BundleProductEntity>()
            val toInsert = mutableListOf<BundleProductEntity>()

            bundleProducts.forEach { productState ->
                val existing = existingProductsMap[productState.product.id]
                if (existing != null) {
                    toUpdate += existing.bundleProduct.copy(
                        name = productState.product.name,
                        quantity = Amount(productState.quantity.toBigDecimal()),
                        sellPrice = Price(productState.sellPrice.toBigDecimal()),
                        updatedAt = currentTimeAsTimestamp
                    )
                } else {
                    toInsert += BundleProductEntity(
                        id = 0,
                        name = productState.product.name,
                        bundleId = bundleId,
                        productId = productState.product.id,
                        quantity = Amount(productState.quantity.toBigDecimal()),
                        unit = productState.product.unit,
                        sellPrice = Price(productState.sellPrice.toBigDecimal()),
                        createdAt = currentTimeAsTimestamp,
                        updatedAt = null
                    )
                }
            }

            if (toUpdate.isNotEmpty()) bundleDao.updateBundleProducts(toUpdate)
            if (toInsert.isNotEmpty()) bundleDao.insertBundleProducts(toInsert)
        }
    }

    override suspend fun deleteBundle(bundleId: Int, bundleName: Name) {
        storage.deleteImage(fileName = "bundle_$bundleId.png")
        bundleDao.deleteBundleById(bundleId)
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
                            sellPriceIndividual = bundleProduct.sellPriceHistorical.maxBy { it.createdAt }.price,
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