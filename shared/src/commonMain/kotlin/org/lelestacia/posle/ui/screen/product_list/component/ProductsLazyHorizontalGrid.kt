package org.lelestacia.posle.ui.screen.product_list.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import androidx.compose.ui.tooling.preview.Preview
import org.lelestacia.posle.ui.theme.AppTheme
import com.skydoves.compose.stability.runtime.TraceRecomposition
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.util.Util.GridItemHeight
import org.lelestacia.posle.util.Util.GridItemSpacing
import org.jetbrains.compose.resources.stringResource
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_no_data
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.flow.flowOf

@Composable
private fun EmptyProductsCard(modifier: Modifier = Modifier) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(GridItemSpacing)
        ) {
            Text(
                stringResource(Res.string.label_no_data),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}


@TraceRecomposition
@Composable
fun ProductsLazyHorizontalGrid(
    modifier: Modifier = Modifier,
    products: LazyPagingItems<Product>,
    isCategorizedProduct: Boolean = false,
    isStockShown: Boolean,
    onClick: (Product) -> Unit,
    onLongClick: ((Product) -> Unit)? = null,
    onAddProductToCategoryClicked: (() -> Unit)? = null
) {
    val rowCount = if (products.itemCount < 7) 1 else 2
    val height = GridItemHeight * rowCount + GridItemSpacing * (rowCount - 1)

    LazyHorizontalGrid(
        rows = GridCells.Fixed(rowCount),
        horizontalArrangement = Arrangement.spacedBy(GridItemSpacing),
        verticalArrangement = Arrangement.spacedBy(GridItemSpacing),
        contentPadding = PaddingValues(horizontal = GridItemSpacing),
        modifier = modifier.height(height)
    ) {
        if (products.itemCount == 0 && !isCategorizedProduct) {
            item { EmptyProductsCard(modifier = Modifier.fillMaxSize()) }
        }

        items(
            count = products.itemCount,
            key = products.itemKey { it.id }
        ) { index ->
            products[index]?.let { product ->
                ProductItem(
                    product = product,
                    isStockTracked = isStockShown,
                    onClick = { onClick(product) },
                    onLongClick = { onLongClick?.invoke(product) },
                    modifier = Modifier
                        .animateItem()
                )
            }
        }

        item {
            if(isCategorizedProduct) {
                ProductAddItem(
                    onClick = {
                        onAddProductToCategoryClicked?.invoke()
                    },
                    modifier = Modifier.widthIn(128.dp)
                        .animateItem()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProductsLazyHorizontalGrid() {
    val products = flowOf(PagingData.from(emptyList<Product>()))
        .cachedIn(rememberCoroutineScope())
        .collectAsLazyPagingItems()
    AppTheme {
        ProductsLazyHorizontalGrid(
            products = products,
            isStockShown = true,
            onClick = {},
        )
    }
}
