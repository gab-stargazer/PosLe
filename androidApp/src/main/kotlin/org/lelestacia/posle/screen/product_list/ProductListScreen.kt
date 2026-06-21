package org.lelestacia.posle.screen.product_list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.skydoves.compose.stability.runtime.TraceRecomposition
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.component.ProductListComponent
import org.lelestacia.posle.domain.component.ProductListComponentEvent
import org.lelestacia.posle.domain.component.ProductListComponentEvent.CategoryEvent.OnAddCategoryMenuClicked
import org.lelestacia.posle.domain.component.ProductListComponentEvent.CategoryEvent.OnAddCategoryMenuDismissed
import org.lelestacia.posle.domain.component.ProductListComponentEvent.OnNavigateTo
import org.lelestacia.posle.domain.component.ProductListComponentEvent.OnQueryChanged
import org.lelestacia.posle.navigation.AddEdit.Add
import org.lelestacia.posle.navigation.Config.ProductAddEdit
import org.lelestacia.posle.screen.product_list.component.AddCategoryDialog
import org.lelestacia.posle.screen.product_list.component.categorized
import org.lelestacia.posle.screen.product_list.component.unCategorized
import org.lelestacia.posle.ui.component.AnimatedIcon
import posle.shared.generated.resources.Res
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
    val uncategorizedProducts = state.uncategorizedProducts.collectAsLazyPagingItems()

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
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(25F),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1F)
            ) {

                categorized(
                    searchQuery = state.searchQuery,
                    categories = categories,
                    categorizedProducts = component::productsInCategory,
                    productsNotInCategory = component::productsNotInCategory,
                    onEvent = component::onEvent
                )

                unCategorized(
                    uncategorizedProducts = uncategorizedProducts,
                    onEvent = component::onEvent
                )
            }
        }
    }
}
