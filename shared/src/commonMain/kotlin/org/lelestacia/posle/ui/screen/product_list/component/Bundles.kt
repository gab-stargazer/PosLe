package org.lelestacia.posle.ui.screen.product_list.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.component.product_list.ProductListComponentEvent
import org.lelestacia.posle.domain.model.Bundle
import org.lelestacia.posle.navigation.AddEdit.Edit
import org.lelestacia.posle.navigation.Config
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.title_bundle

fun LazyListScope.bundles(
    bundles: LazyPagingItems<Bundle>,
    onEvent: (ProductListComponentEvent) -> Unit
) {
    if (bundles.itemCount > 0) {
        item {
            Text(
                text = stringResource(Res.string.title_bundle),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(12.dp)
            )
        }

        items(count = bundles.itemCount) { index ->
            val bundle = bundles[index]
            if (bundle != null) {
                BundleItem(
                    bundle = bundle,
                    onClick = {
                        onEvent(
                            ProductListComponentEvent.OnNavigateTo(
                                Config.BundleAddEdit(
                                    addEdit = Edit,
                                    bundle = bundle
                                )
                            )
                        )
                    },
                    onLongClick = {
                        onEvent(
                            ProductListComponentEvent.OnNavigateTo(
                                Config.BundleAddEdit(
                                    addEdit = Edit,
                                    bundle = bundle
                                )
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
