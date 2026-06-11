package org.lelestacia.posle.domain.component

import androidx.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.lelestacia.posle.domain.repository.ProductRepository

class ProductListComponent (
    componentContext: ComponentContext,
    private val repository: ProductRepository
): ComponentContext by componentContext {

    val scope = CoroutineScope(Dispatchers.Main.immediate)

    val products = repository
        .readProduct("")
        .cachedIn(scope)


}