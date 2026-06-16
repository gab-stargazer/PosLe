package org.lelestacia.posle.domain.component

import androidx.compose.foundation.text.input.TextFieldState
import androidx.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreateSimple
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import org.lelestacia.posle.domain.repository.ProductRepository
import kotlin.time.Duration.Companion.milliseconds

class ProductListComponent(
    componentContext: ComponentContext,
    private val repository: ProductRepository
) : ComponentContext by componentContext {

    val scope = instanceKeeper.getOrCreateSimple { CoroutineScope(Dispatchers.Main.immediate) }
    
    val searchQuery = TextFieldState()
    private val _searchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val products = _searchQuery
        .debounce(300.milliseconds)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            repository.readProduct(query)
        }
        .cachedIn(scope)

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}