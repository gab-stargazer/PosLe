package org.lelestacia.posle.domain.component.product_list

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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.lelestacia.posle.data.SettingManager
import org.lelestacia.posle.data.entity.toDomain
import org.lelestacia.posle.domain.component.product_list.ProductListComponentEvent.CategoryEvent.OnAddCategoryMenuClicked
import org.lelestacia.posle.domain.component.product_list.ProductListComponentEvent.CategoryEvent.OnDeleteCategory
import org.lelestacia.posle.domain.component.product_list.ProductListComponentState.AddCategoryState
import org.lelestacia.posle.domain.model.Category
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.repository.CategoryRepository
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.navigation.Config
import org.lelestacia.posle.util.ExcelManager
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.coroutineScope

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class ProductListComponentImpl(
    componentContext: ComponentContext,
    settingManager: SettingManager,
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val bundleRepository: org.lelestacia.posle.domain.repository.BundleRepository,
    private val onNavigate: (Config) -> Unit,
) : ComponentContext by componentContext, ProductListComponent {

    private val scope = coroutineScope(Dispatchers.Main.immediate)
    private val searchQuery = MutableStateFlow("")

    private val _state = MutableStateFlow(ProductListComponentState())

    private val bundles: Flow<PagingData<org.lelestacia.posle.domain.model.Bundle>> = searchQuery
        .flatMapLatest { query ->
            bundleRepository.getBundlesByName(query)
        }.cachedIn(scope)

    val categories: Flow<PagingData<Category>> = categoryRepository
        .getCategories()
        .cachedIn(scope)

    override val productPagingFlows: MutableMap<Pair<String, Int>, Flow<PagingData<Product>>> =
        mutableMapOf()

    val lowStocksProducts: Flow<PagingData<Product>> = searchQuery
        .flatMapLatest { query ->
            productRepository.getProductsWithLowStock(query)
        }.cachedIn(scope)

    val uncategorizedProducts: Flow<PagingData<Product>> = searchQuery
        .flatMapLatest { query ->
            productRepository.getProductWithoutCategories(query)
        }.cachedIn(scope)

    override val state: StateFlow<ProductListComponentState> =
        combine(
            flow = _state,
            flow2 = searchQuery,
            flow3 = settingManager.getSettings()
        ) { state, searchQuery, settings ->
            ProductListComponentState(
                searchQuery = searchQuery,
                isFabMenuExpanded = state.isFabMenuExpanded,
                isAddCategoryDisplayed = state.isAddCategoryDisplayed,
                addCategoryState = state.addCategoryState,

                categories = categories,
                uncategorizedProducts = uncategorizedProducts,
                productsLowStock = lowStocksProducts,
                bundles = bundles,

                settings = settings
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

            is ProductListComponentEvent.OnExportProducts -> {
                scope.launch {
                    val products = productRepository.getAllProducts().first()
                    val excelBytes = ExcelManager.exportProductsToExcel(products)
                    event.onExport(excelBytes)
                }
            }

            is ProductListComponentEvent.OnImportProducts -> {
                scope.launch {
                    val products = ExcelManager.importProductsFromExcel(event.fileBytes)
                    productRepository.importProducts(products)
                }
            }

            //=====Category Finish=====

            is ProductListComponentEvent.AddCategoryEvent.OnSaveClicked -> {
                scope.launch {
                    categoryRepository.createCategory(
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
                    categoryRepository.createProductCategoryLink(
                        productId = event.productId,
                        categoryId = event.categoryId
                    )
                }
            }

            is ProductListComponentEvent.CategoryEvent.OnRemoveProductFromCategory -> {
                scope.launch {
                    categoryRepository.deleteProductCategoryLink(
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
                productRepository.getProductWithCategories(searchQuery, categoryId)
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
            productRepository.getProductNotInCategory(searchQuery, categoryId)
        }.flow.map { it.map { it.toDomain() } }.cachedIn(scope)
    }
}
