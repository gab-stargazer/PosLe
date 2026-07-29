package org.lelestacia.posle.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.lelestacia.posle.domain.component.bundle_add_edit.BundleProductState
import org.lelestacia.posle.domain.model.Bundle
import org.lelestacia.posle.util.Name

/**
 * Repository interface for managing product bundles.
 *
 * Bundles allow grouping multiple products into a single SKU with a unique price,
 * typically used for promotions or packaged deals.
 */
interface BundleRepository {

    /**
     * Creates a new bundle containing multiple products.
     *
     * @param bundleName The name of the new bundle.
     * @param bundleProducts The products and quantities included in the bundle.
     * @param imageByteArray Optional raw bytes for the bundle's representative image.
     * @throws Exception if bundle creation fails.
     */
    suspend fun createBundle(
        bundleName: Name,
        bundleProducts: List<BundleProductState>,
        imageByteArray: ByteArray?
    )

    /**
     * Updates an existing bundle's name, products, or image.
     *
     * This operation performs a surgical update of the bundle's product list,
     * adding new products, updating existing quantities/prices, and removing deleted ones.
     *
     * @param bundleId The unique ID of the bundle to update.
     * @param bundleName The updated name.
     * @param bundleProducts The updated list of items in the bundle.
     * @param imageUri Current image path (if unchanged).
     * @param imageByteArray New image data (if changed).
     */
    suspend fun updateBundle(
        bundleId: Int,
        bundleName: Name,
        bundleProducts: List<BundleProductState>,
        imageUri: String?,
        imageByteArray: ByteArray?
    )

    /**
     * Permanently deletes a bundle and its associated image resource.
     *
     * @param bundleId The ID of the bundle.
     * @param bundleName The name of the bundle (used for log/file identification).
     */
    suspend fun deleteBundle(bundleId: Int, bundleName: Name)

    /**
     * Provides a paginated stream of bundles filtered by name.
     *
     * @param bundleName The search query to match against bundle names.
     * @return A [Flow] of [PagingData] containing matching [Bundle]s.
     */
    fun getBundlesByName(bundleName: String): Flow<PagingData<Bundle>>
}