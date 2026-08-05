package org.lelestacia.posle.domain.component.product_list

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.domain.model.Category
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.navigation.Config

interface ProductListComponent {
    val state: StateFlow<ProductListComponentState>
    val productPagingFlows: MutableMap<Pair<String, String>, Flow<PagingData<Product>>>
    fun onEvent(event: ProductListComponentEvent)
    fun productsInCategory(searchQuery: String, categoryId: String): Flow<PagingData<Product>>
    fun productsNotInCategory(searchQuery: String, categoryId: String): Flow<PagingData<Product>>
}

@Immutable
data class ProductListComponentState(
    val searchQuery: String = "",
    val isFabMenuExpanded: Boolean = false,

    //  Category
    val isAddCategoryDisplayed: Boolean = false,
    val addCategoryState: AddCategoryState = AddCategoryState(),

    //  Paging
    val productsLowStock: Flow<PagingData<Product>> = flowOf(),
    val uncategorizedProducts: Flow<PagingData<Product>> = flowOf(),
    val bundles: Flow<PagingData<org.lelestacia.posle.domain.model.Bundle>> = flowOf(),
    val categories: Flow<PagingData<Category>> = flowOf(),

    //  Setting
    val settings: PosLeSettings = PosLeSettings()
) {

    @Immutable
    data class AddCategoryState(
        val categoryName: TextFieldState = TextFieldState()
    )
}

sealed interface ProductListComponentEvent {
    //  Navigation
    data class OnNavigateTo(val config: Config) : ProductListComponentEvent

    //  State
    data class OnQueryChanged(val newQuery: String) : ProductListComponentEvent
    data object OnToggleFabMenu : ProductListComponentEvent

    //  Export/Import
    data class OnExportProducts(val onExport: (ByteArray) -> Unit) : ProductListComponentEvent
    data class OnImportProducts(
        val fileBytes: ByteArray,
        val onImportResult: (Boolean) -> Unit
    ) : ProductListComponentEvent

    sealed interface CategoryEvent : ProductListComponentEvent {
        data object OnAddCategoryMenuClicked : CategoryEvent
        data object OnAddCategoryMenuDismissed : CategoryEvent
        data class OnAddProductToCategory(val productId: String, val categoryId: String) :
            CategoryEvent

        data class OnRemoveProductFromCategory(val productId: String, val categoryId: String) :
            CategoryEvent

        data class OnDeleteCategory(val categoryId: String) : CategoryEvent
    }


    sealed interface AddCategoryEvent : ProductListComponentEvent {
        data class OnSaveClicked(val categoryName: String) : AddCategoryEvent
    }
}