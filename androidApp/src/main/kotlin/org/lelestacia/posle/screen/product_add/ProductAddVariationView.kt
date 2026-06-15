package org.lelestacia.posle.screen.product_add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.lelestacia.posle.domain.component.product_add_edit.ProductAddVariantViewComponent
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.domain.state_event.product_add.ProductVariantViewEvent
import org.lelestacia.posle.domain.state_event.product_add.ProductVariantViewEvent.OnVariantClicked
import org.lelestacia.posle.domain.state_event.product_add.ProductVariationAddState
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price

@Composable
fun ProductAddVariationViewScreen(
    component: ProductAddVariantViewComponent,
    modifier: Modifier = Modifier
) {
    val variants = component.variant.collectAsLazyPagingItems()
    val state by component.state.subscribeAsState()

    var dialogState by remember { mutableStateOf(ProductVariationAddState()) }
    var isDialogOpened by remember { mutableStateOf(false) }

    if (isDialogOpened) {
        Dialog(
            onDismissRequest = {
                isDialogOpened = false
            },
            properties = DialogProperties(
                dismissOnClickOutside = false
            )
        ) {
            ProductAddVariationDialog(
                state = dialogState,
                onSaveClicked = {
                    component.onEvent(
                        ProductVariantViewEvent.OnAddVariant(
                            Variant(
                                id = 0,
                                name = Name(dialogState.variantNameState.text.toString()),
                                priceAdjustment = Price(
                                    dialogState.variantPriceState.text
                                        .toString()
                                        .ifEmpty { "0" }
                                        .toBigDecimal()
                                )
                            )
                        )
                    )

                    isDialogOpened = false
                }
            )
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(),
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PrimaryTabRow(
                selectedTabIndex = state.currentTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = state.currentTab == 0,
                    onClick = {
                        component.onEvent(ProductVariantViewEvent.OnTabChanged(0))
                    },
                    text = {
                        Text(
                            text = "Semua",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                )

                Tab(
                    selected = state.currentTab == 1,
                    onClick = {
                        component.onEvent(ProductVariantViewEvent.OnTabChanged(1))
                    },
                    text = {
                        Text(
                            text = "Dipilih (${state.selectedVariant.size})",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                )
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1F)
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            ) {
                if (state.currentTab == 0) {
                    items(
                        count = variants.itemCount,
                        key = variants.itemKey { index -> index.id }) { index ->
                        variants[index]?.let { variant ->
                            val isSelected = state.selectedVariant.any { it.id == variant.id }
                            Column {
                                VariantViewItemAdd(
                                    variant = variant,
                                    isSelected = isSelected,
                                    onAdd = {
                                        component.onEvent(OnVariantClicked(variant))
                                    }
                                )
                                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                } else {
                    items(
                        items = state.selectedVariant,
                        key = { it.id }
                    ) { variant ->
                        Column(
                            modifier = Modifier.animateItem()
                        ) {
                            VariantViewItemRemove(
                                variant = variant,
                                onRemove = {
                                    component.onEvent(OnVariantClicked(variant))
                                }
                            )
                            HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Button(
                    onClick = {
                        component.onEvent(ProductVariantViewEvent.OnConfirmed)
                    },
                    shape = RoundedCornerShape(25F),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 12.dp)
                ) {
                    Text("Simpan Pilihan")
                }

                Button(
                    onClick = {
                        dialogState = ProductVariationAddState()
                        isDialogOpened = true
                    },
                    shape = RoundedCornerShape(25F),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp)
                ) {
                    Text("Tambah Varian")
                }
            }
        }
    }
}