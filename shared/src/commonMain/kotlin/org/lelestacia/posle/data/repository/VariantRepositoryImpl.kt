package org.lelestacia.posle.data.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.lelestacia.posle.data.dao.VariantDao
import org.lelestacia.posle.data.entity.VariantEntity
import org.lelestacia.posle.data.entity.toEntity
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.domain.model.toDomain
import org.lelestacia.posle.domain.repository.VariantRepository
import org.lelestacia.posle.util.Util.pagingConfig

class VariantRepositoryImpl(
    private val variantDao: VariantDao
) : VariantRepository {

    override suspend fun addVariant(variant: Variant) {
        variantDao.insertVariant(variant.toEntity())
    }

    override fun readVariant(): Flow<PagingData<Variant>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { variantDao.readVariant() }
        ).flow.map { it.map(VariantEntity::toDomain) }
    }

    override fun readVariantByProductId(productId: Int): Flow<List<Variant>> {
        return variantDao.readVariantByProductId(productId).map { it.map(VariantEntity::toDomain) }
    }

    override suspend fun updateVariant(variant: Variant) {
        variantDao.updateVariant(variant.toEntity())
    }

    override suspend fun deleteVariant(variant: Variant) {
        variantDao.deleteVariantJunction(variant.id)
        variantDao.deleteVariant(variant.toEntity())
    }
}
