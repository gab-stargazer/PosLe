package org.lelestacia.posle.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.lelestacia.posle.domain.component.bundle_add_edit.BundleProductState
import org.lelestacia.posle.domain.model.Bundle
import org.lelestacia.posle.util.Name

interface BundleRepository {

    suspend fun insertBundle(bundleName: Name, bundleProducts: List<BundleProductState>)
    fun readBundleByName(bundleName: String): Flow<PagingData<Bundle>>
}