package org.lelestacia.posle.screen.product_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import org.lelestacia.posle.domain.component.ProductListComponent
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.navigation.AddEdit
import org.lelestacia.posle.navigation.AddEdit.Add
import org.lelestacia.posle.navigation.AddEdit.Edit

@Composable
fun ProductListScreen(
    component: ProductListComponent,
    onNavigateToAddProduct: (AddEdit, Product?) -> Unit,
    modifier: Modifier = Modifier
) {
    ProductListUi(
        onNavigateToAddProduct,
        component.products.collectAsLazyPagingItems(),
        modifier
    )
}

@Composable
private fun ProductListUi(
    onNavigateToAddEditProduct: (AddEdit, Product?) -> Unit,
    products: LazyPagingItems<Product>,
    modifier: Modifier = Modifier
) {
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

        LazyColumn(
            contentPadding = PaddingValues(
                horizontal = 12.dp,
                vertical = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
            items(products.itemCount) {
                products[it]?.let { product ->
                    ProductItemUI(
                        product = product,
                        onEdit = {
                            onNavigateToAddEditProduct(Edit, product)
                        }
                    )
                }
            }
        }
    }
}