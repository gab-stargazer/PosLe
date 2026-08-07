package org.lelestacia.posle.ui.screen.product_list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.skydoves.compose.stability.runtime.TraceRecomposition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.component.product_list.ProductListComponent
import org.lelestacia.posle.domain.component.product_list.ProductListComponentEvent
import org.lelestacia.posle.domain.component.product_list.ProductListComponentEvent.CategoryEvent.OnAddCategoryMenuClicked
import org.lelestacia.posle.domain.component.product_list.ProductListComponentEvent.CategoryEvent.OnAddCategoryMenuDismissed
import org.lelestacia.posle.domain.component.product_list.ProductListComponentEvent.OnNavigateTo
import org.lelestacia.posle.domain.component.product_list.ProductListComponentEvent.OnQueryChanged
import org.lelestacia.posle.domain.component.product_list.ProductListComponentState
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.navigation.AddEdit.Add
import org.lelestacia.posle.navigation.Config
import org.lelestacia.posle.navigation.Config.ProductAddEdit
import org.lelestacia.posle.ui.screen.product_list.component.AddCategoryDialog
import org.lelestacia.posle.ui.screen.product_list.component.bundles
import org.lelestacia.posle.ui.screen.product_list.component.categorized
import org.lelestacia.posle.ui.screen.product_list.component.lowStock
import org.lelestacia.posle.ui.screen.product_list.component.unCategorized
import org.lelestacia.posle.ui.component.AnimatedIcon
import org.lelestacia.posle.ui.theme.AppTheme
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_add_bundle
import posle.shared.generated.resources.btn_add_category
import posle.shared.generated.resources.btn_add_product
import posle.shared.generated.resources.label_search_product

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@TraceRecomposition
@Composable
fun ProductListScreen(
    component: ProductListComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsStateWithLifecycle()

    val categories = state.categories.collectAsLazyPagingItems()
    val lowStockProducts = state.productsLowStock.collectAsLazyPagingItems()
    val uncategorizedProducts = state.uncategorizedProducts.collectAsLazyPagingItems()
    val bundles = state.bundles.collectAsLazyPagingItems()
    val listState = rememberLazyListState()
    val isListScrolled by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }
    val searchBarElevation by animateDpAsState(
        targetValue = if (isListScrolled) 8.dp else 0.dp,
        label = "ProductListSearchBarElevation"
    )

    if (state.isAddCategoryDisplayed) {
        Dialog(
            onDismissRequest = {
                component.onEvent(OnAddCategoryMenuDismissed)
            }
        ) {
            AddCategoryDialog(
                state = state.addCategoryState,
                onEvent = component::onEvent
            )
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(),
        floatingActionButton = {
            FloatingActionButtonMenu(
                expanded = state.isFabMenuExpanded,
                button = {
                    FloatingActionButton(
                        onClick = {
                            component.onEvent(ProductListComponentEvent.OnToggleFabMenu)
                        }
                    ) {
                        AnimatedIcon(
                            condition = state.isFabMenuExpanded,
                            onTrue = Icons.Default.Close,
                            onFalse = Icons.Default.Menu
                        )
                    }
                }
            ) {
                AnimatedVisibility(state.isFabMenuExpanded) {
                    Column(horizontalAlignment = Alignment.End) {
                        ElevatedButton(
                            onClick = {
                                component.onEvent(OnNavigateTo(ProductAddEdit(Add, null)))
                            }
                        ) {
                            Text(
                                text = stringResource(resource = Res.string.btn_add_product),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        ElevatedButton(
                            onClick = {
                                component.onEvent(OnNavigateTo(Config.BundleAddEdit(Add, null)))
                            }
                        ) {
                            Text(
                                text = stringResource(resource = Res.string.btn_add_bundle),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        ElevatedButton(
                            onClick = {
                                component.onEvent(OnAddCategoryMenuClicked)
                            }
                        ) {
                            Text(
                                text = stringResource(Res.string.btn_add_category),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shadowElevation = searchBarElevation,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TextField(
                    value = state.searchQuery,
                    onValueChange = { newQuery ->
                        component.onEvent(OnQueryChanged(newQuery))
                    },
                    placeholder = {
                        Text(
                            text = stringResource(Res.string.label_search_product),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            12.dp
                        ),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(25F),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 12.dp)
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1F)
            ) {

                if (state.settings.isProductStockTracked && lowStockProducts.itemCount > 0) {
                    lowStock(
                        lowStockProducts = lowStockProducts,
                        isStockTracked = state.settings.isProductStockTracked,
                        onEvent = component::onEvent
                    )
                }

                bundles(
                    bundles = bundles,
                    onEvent = component::onEvent
                )

                categorized(
                    searchQuery = state.searchQuery,
                    categories = categories,
                    isStockTracked = state.settings.isProductStockTracked,
                    categorizedProducts = component::productsInCategory,
                    productsNotInCategory = component::productsNotInCategory,
                    onEvent = component::onEvent
                )

                if (uncategorizedProducts.itemCount > 0) {
                    unCategorized(
                        uncategorizedProducts = uncategorizedProducts,
                        isStockTracked = state.settings.isProductStockTracked,
                        onEvent = component::onEvent
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProductListScreen() {
    AppTheme {
        ProductListScreen(
            component = object : ProductListComponent {
                override val productPagingFlows: MutableMap<Pair<String, String>, Flow<PagingData<Product>>>
                    get() = mutableMapOf()

                override val state: StateFlow<ProductListComponentState>
                    get() = MutableStateFlow(ProductListComponentState())

                override fun onEvent(event: ProductListComponentEvent) {}

                override fun productsInCategory(searchQuery: String, categoryId: String): Flow<PagingData<Product>> =
                    flowOf(PagingData.from(emptyList()))

                override fun productsNotInCategory(searchQuery: String, categoryId: String): Flow<PagingData<Product>> =
                    flowOf(PagingData.from(emptyList()))
            }
        )
    }
}
