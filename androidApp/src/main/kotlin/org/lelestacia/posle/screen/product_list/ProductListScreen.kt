package org.lelestacia.posle.screen.product_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.component.ProductListComponent
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.navigation.AddEdit
import org.lelestacia.posle.navigation.AddEdit.Add
import org.lelestacia.posle.navigation.AddEdit.Edit
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_search_product

@Composable
fun ProductListScreen(
    component: ProductListComponent,
    onNavigateToAddProduct: (AddEdit, Product?) -> Unit,
    modifier: Modifier = Modifier
) {
    ProductListUi(
        component = component,
        onNavigateToAddEditProduct = onNavigateToAddProduct,
        products = component.products.collectAsLazyPagingItems(),
        modifier = modifier
    )
}

@Composable
private fun ProductListUi(
    component: ProductListComponent,
    onNavigateToAddEditProduct: (AddEdit, Product?) -> Unit,
    products: LazyPagingItems<Product>,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(component.searchQuery) {
        snapshotFlow { component.searchQuery.text }
            .collect { query ->
                component.onSearchQueryChanged(query.toString())
            }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onNavigateToAddEditProduct(Add, null)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
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
                state = component.searchQuery,
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

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(count = products.itemCount, key = products.itemKey { it.id }) { index ->
                    products[index]?.let { product ->
                        ProductItem(
                            product = product,
                            onEdit = {
                                onNavigateToAddEditProduct(
                                    Edit,
                                    product
                                )
                            },
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }
        }
    }
}