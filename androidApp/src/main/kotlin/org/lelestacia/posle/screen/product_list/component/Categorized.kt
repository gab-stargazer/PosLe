package org.lelestacia.posle.screen.product_list.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.component.ProductListComponentEvent
import org.lelestacia.posle.domain.component.ProductListComponentEvent.CategoryEvent.OnAddProductToCategory
import org.lelestacia.posle.domain.component.ProductListComponentEvent.CategoryEvent.OnDeleteCategory
import org.lelestacia.posle.domain.component.ProductListComponentEvent.CategoryEvent.OnRemoveProductFromCategory
import org.lelestacia.posle.domain.component.ProductListComponentEvent.OnNavigateTo
import org.lelestacia.posle.domain.model.Category
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.navigation.AddEdit.Edit
import org.lelestacia.posle.navigation.Config.ProductAddEdit
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.title_add_product_to_category

fun LazyListScope.categorized(
    searchQuery: String,
    categories: LazyPagingItems<Category>,
    categorizedProducts: (String, Int) -> Flow<PagingData<Product>>,
    productsNotInCategory: (String, Int) -> Flow<PagingData<Product>>,
    onEvent: (ProductListComponentEvent) -> Unit,
) {
    items(
        categories.itemCount,
        categories.itemKey { it.id },
        categories.itemContentType()
    ) { index ->
        categories[index]?.let { category ->
            Column(modifier = Modifier.animateItem()) {
                var isExpanded by remember {
                    mutableStateOf(false)
                }

                var isAddProductToCategoryShown by remember { mutableStateOf(false) }

                if (isAddProductToCategoryShown) {
                    Dialog(
                        onDismissRequest = {
                            isAddProductToCategoryShown = false
                        }
                    ) {
                        ElevatedCard(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                            ),
                            shape = RoundedCornerShape(25F)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = stringResource(resource = Res.string.title_add_product_to_category),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                )

                                val filteredProducts = productsNotInCategory(
                                    searchQuery,
                                    category.id
                                ).collectAsLazyPagingItems()

                                ProductsLazyHorizontalGrid(
                                    products = filteredProducts,
                                    onClick = { product ->
                                        onEvent(
                                            OnAddProductToCategory(
                                                productId = product.id,
                                                categoryId = category.id
                                            )
                                        )

                                        isAddProductToCategoryShown = false
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp)
                                )
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = category.name.value,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Box {
                        IconButton(
                            onClick = {
                                isExpanded = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = null
                            )
                        }

                        CategoryMenu(
                            isExpanded = isExpanded,
                            onDismiss = {
                                isExpanded = false
                            },
                            onEditCategory = {
                                isExpanded = false
                            },
                            onDeleteCategory = {
                                isExpanded = false
                                onEvent(OnDeleteCategory(category.id))
                            }
                        )
                    }
                }

                val pagingData = categorizedProducts(searchQuery, category.id)
                    .collectAsLazyPagingItems()

                var isRemovedItemMenuShown by remember { mutableStateOf(false) }
                var selectedProduct: Product? by remember { mutableStateOf(null) }

                Box(
                    contentAlignment = Alignment.TopEnd
                ) {
                    ProductsLazyHorizontalGrid(
                        products = pagingData,
                        isCategorizedProduct = true,
                        onClick = { product ->
                            onEvent(OnNavigateTo(ProductAddEdit(Edit, product)))
                        },
                        onLongClick = { product ->
                            isRemovedItemMenuShown = true
                            selectedProduct = product
                        },
                        onAddProductToCategoryClicked = {
                            isAddProductToCategoryShown = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    DropdownMenu(
                        expanded = isRemovedItemMenuShown,
                        onDismissRequest = {
                            selectedProduct = null
                            isRemovedItemMenuShown = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Hapus Produk dari kategori",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            },
                            onClick = {
                                onEvent(OnRemoveProductFromCategory(selectedProduct?.id ?: return@DropdownMenuItem, category.id))
                                selectedProduct = null
                                isRemovedItemMenuShown = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryMenu(
    isExpanded: Boolean,
    onDismiss: () -> Unit,
    onEditCategory: () -> Unit,
    onDeleteCategory: () -> Unit
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismiss,
        offset = DpOffset(
            x = (-12).dp,
            y = 0.dp
        )
    ) {

        DropdownMenuItem(
            text = {
                Text(
                    "Edit Kategori",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            onClick = onEditCategory
        )

        DropdownMenuItem(
            text = {
                Text(
                    "Hapus Kategori",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            onClick = onDeleteCategory
        )
    }
}