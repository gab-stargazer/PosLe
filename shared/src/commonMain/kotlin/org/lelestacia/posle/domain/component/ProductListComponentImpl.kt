package org.lelestacia.posle.domain.component

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Immutable
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.lelestacia.posle.data.entity.toDomain
import org.lelestacia.posle.domain.component.ProductListComponentEvent.CategoryEvent.OnAddCategoryMenuClicked
import org.lelestacia.posle.domain.component.ProductListComponentEvent.CategoryEvent.OnDeleteCategory
import org.lelestacia.posle.domain.component.ProductListComponentState.AddCategoryState
import org.lelestacia.posle.domain.model.Category
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.repository.CategoryRepository
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.navigation.Config
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.coroutineScope

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class ProductListComponentImpl(
    componentContext: ComponentContext,
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val onNavigate: (Config) -> Unit,
) : ComponentContext by componentContext, ProductListComponent {

    private val scope = coroutineScope(Dispatchers.Main.immediate)
    private val searchQuery = MutableStateFlow("")

    private val _state = MutableStateFlow(ProductListComponentState())

    val categories = categoryRepository.readCategories()
        .cachedIn(scope)

    override val productPagingFlows: MutableMap<Pair<String, Int>, Flow<PagingData<Product>>> =
        mutableMapOf()

    val uncategorizedProducts: Flow<PagingData<Product>> = searchQuery
        .flatMapLatest { query ->
            productRepository.readProductWithoutCategories(query)
        }.cachedIn(scope)

    override val state: StateFlow<ProductListComponentState> =
        combine(
            flow = _state,
            flow2 = searchQuery,
        ) { state, searchQuery ->
            ProductListComponentState(
                searchQuery = searchQuery,
                isFabMenuExpanded = state.isFabMenuExpanded,
                isAddCategoryDisplayed = state.isAddCategoryDisplayed,
                addCategoryState = state.addCategoryState,

                categories = categories,
                uncategorizedProducts = uncategorizedProducts
            )
        }.stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = ProductListComponentState()
        )


    override fun onEvent(event: ProductListComponentEvent) {
        when (event) {
            is ProductListComponentEvent.OnQueryChanged -> searchQuery.update { event.newQuery }

            is ProductListComponentEvent.OnNavigateTo -> {
                _state.update { currentState ->
                    currentState.copy(
                        isFabMenuExpanded = false
                    )
                }

                onNavigate(event.config)
            }


            ProductListComponentEvent.OnToggleFabMenu -> {
                _state.update {
                    it.copy(
                        isFabMenuExpanded = !it.isFabMenuExpanded
                    )
                }
            }

            //=====Category Finish=====

            is ProductListComponentEvent.AddCategoryEvent.OnSaveClicked -> {
                scope.launch {
                    categoryRepository.addCategory(
                        Category(
                            id = 0,
                            name = Name(state.value.addCategoryState.categoryName.text.toString())
                        )
                    )

                    _state.update { currentState ->
                        currentState.copy(
                            addCategoryState = AddCategoryState(),
                            isAddCategoryDisplayed = false
                        )
                    }
                }
            }

            is ProductListComponentEvent.CategoryEvent -> onCategoryEvent(event)
        }
    }

    private fun onCategoryEvent(event: ProductListComponentEvent.CategoryEvent) {
        when (event) {
            OnAddCategoryMenuClicked -> {
                _state.update { currentState ->
                    currentState.copy(
                        isFabMenuExpanded = false,
                        isAddCategoryDisplayed = true
                    )
                }
            }

            ProductListComponentEvent.CategoryEvent.OnAddCategoryMenuDismissed -> {
                _state.update { currentState ->
                    currentState.copy(
                        isAddCategoryDisplayed = false,
                        addCategoryState = AddCategoryState()
                    )
                }
            }

            is OnDeleteCategory -> {
                scope.launch {
                    categoryRepository.deleteCategory(categoryId = event.categoryId)
                }
            }

            is ProductListComponentEvent.CategoryEvent.OnAddProductToCategory -> {
                scope.launch {
                    categoryRepository.addProductToCategory(
                        productId = event.productId,
                        categoryId = event.categoryId
                    )
                }
            }

            is ProductListComponentEvent.CategoryEvent.OnRemoveProductFromCategory -> {
                scope.launch {
                    categoryRepository.removeProductFromCategory(
                        productId = event.productId,
                        categoryId = event.categoryId
                    )
                }
            }
        }
    }

    override fun productsInCategory(
        searchQuery: String,
        categoryId: Int
    ): Flow<PagingData<Product>> {
        return productPagingFlows.getOrPut(Pair(searchQuery, categoryId)) {
            Pager(
                config = PagingConfig(pageSize = 20)
            ) {
                productRepository.readProductWithCategories(searchQuery, categoryId)
            }.flow.map { it.map { it.toDomain() } }.cachedIn(scope)
        }
    }

    override fun productsNotInCategory(
        searchQuery: String,
        categoryId: Int
    ): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(pageSize = 20)
        ) {
            productRepository.readProductNotInCategory(searchQuery, categoryId)
        }.flow.map { it.map { it.toDomain() } }.cachedIn(scope)
    }
}

interface ProductListComponent {
    val state: StateFlow<ProductListComponentState>
    val productPagingFlows: MutableMap<Pair<String, Int>, Flow<PagingData<Product>>>
    fun onEvent(event: ProductListComponentEvent)
    fun productsInCategory(searchQuery: String, categoryId: Int): Flow<PagingData<Product>>
    fun productsNotInCategory(searchQuery: String, categoryId: Int): Flow<PagingData<Product>>
}

@Immutable
data class ProductListComponentState(
    val searchQuery: String = "",
    val isFabMenuExpanded: Boolean = false,

    //  Category
    val isAddCategoryDisplayed: Boolean = false,
    val addCategoryState: AddCategoryState = AddCategoryState(),

    //  Paging
    val uncategorizedProducts: Flow<PagingData<Product>> = flowOf(),
    val categories: Flow<PagingData<Category>> = flowOf()
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


    sealed interface CategoryEvent : ProductListComponentEvent {
        data object OnAddCategoryMenuClicked : CategoryEvent
        data object OnAddCategoryMenuDismissed : CategoryEvent
        data class OnAddProductToCategory(val productId: Int, val categoryId: Int) :
            CategoryEvent

        data class OnRemoveProductFromCategory(val productId: Int, val categoryId: Int) :
            CategoryEvent

        data class OnDeleteCategory(val categoryId: Int) : CategoryEvent
    }


    sealed interface AddCategoryEvent : ProductListComponentEvent {
        data class OnSaveClicked(val categoryName: String) : AddCategoryEvent
    }
}