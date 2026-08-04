package org.lelestacia.posle.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.lelestacia.posle.domain.model.Variant

/**
 * Repository interface for managing product variants and their pricing adjustments.
 */
interface VariantRepository {
    /**
     * Persists a new variant definition.
     *
     * @param variant The [Variant] model to create.
     */
    suspend fun createVariant(variant: Variant)

    /**
     * Provides a paginated stream of all defined variants.
     *
     * @return A [Flow] of [PagingData] containing [Variant]s.
     */
    fun getAllVariants(): Flow<PagingData<Variant>>

    /**
     * Retrieves all variants associated with a specific product.
     *
     * @param productId The ID of the product.
     * @return A [Flow] list of linked [Variant]s.
     */
    fun getVariantsByProductId(productId: String): Flow<List<Variant>>

    /**
     * Updates an existing variant's name or price adjustment.
     *
     * @param variant The [Variant] with updated values.
     */
    suspend fun updateVariant(variant: Variant)

    /**
     * Permanently deletes a variant and removes it from all associated products.
     *
     * @param variant The [Variant] to delete.
     */
    suspend fun deleteVariant(variant: Variant)
}
