package org.lelestacia.posle.screen.product_list.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.component.ProductListComponentEvent
import org.lelestacia.posle.domain.component.ProductListComponentEvent.OnNavigateTo
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.navigation.AddEdit.Edit
import org.lelestacia.posle.navigation.Config.ProductAddEdit
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.title_no_category

fun LazyListScope.unCategorized(
    uncategorizedProducts: LazyPagingItems<Product>,
    onEvent: (ProductListComponentEvent) -> Unit,
) {
    item {
        Column(modifier = Modifier.animateItem()) {
            Text(
                text = stringResource(Res.string.title_no_category),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(12.dp)
            )

            ProductsLazyHorizontalGrid(
                products = uncategorizedProducts,
                onClick = { product ->
                    onEvent(OnNavigateTo(ProductAddEdit(Edit, product)))
                }
            )
        }
    }
}