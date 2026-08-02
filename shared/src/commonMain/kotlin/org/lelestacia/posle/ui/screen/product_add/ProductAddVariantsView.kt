package org.lelestacia.posle.ui.screen.product_add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.component.product_add_edit.ProductAddVariantsViewComponent
import org.lelestacia.posle.domain.state_event.product_add.ProductVariantViewEvent
import org.lelestacia.posle.domain.state_event.product_add.ProductVariantViewEvent.OnAddVariant
import org.lelestacia.posle.domain.state_event.product_add.ProductVariantViewEvent.OnCancel
import org.lelestacia.posle.domain.state_event.product_add.ProductVariantViewEvent.OnCheckedChange
import org.lelestacia.posle.domain.state_event.product_add.ProductVariantViewEvent.OnDeleteVariant
import org.lelestacia.posle.domain.state_event.product_add.ProductVariantViewEvent.OnEditVariant
import org.lelestacia.posle.domain.state_event.product_add.ProductVariantViewEvent.OnSaveVariant
import org.lelestacia.posle.domain.state_event.product_add.ProductVariantViewEvent.OnVariantDialogDismissed
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_add_variant
import posle.shared.generated.resources.btn_save_selection
import posle.shared.generated.resources.title_choose_variant

@Composable
fun ProductAddVariantsViewScreen(
    component: ProductAddVariantsViewComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.subscribeAsState()
    val variants = state.variants.collectAsLazyPagingItems()

    if (state.isAddEditVariantDialogShown) {
        Dialog(
            onDismissRequest = {
                component.onEvent(OnVariantDialogDismissed)
            },
            properties = DialogProperties(
                dismissOnClickOutside = false
            )
        ) {
            ProductAddVariantDialog(
                state = state.addEditVariantDialogState,
                onSaveClicked = {
                    component.onEvent(OnSaveVariant)
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.title_choose_variant),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            component.onEvent(OnCancel)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets(),
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1F)
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            ) {
                items(
                    count = variants.itemCount,
                    key = variants.itemKey { index -> index.id }) { index ->
                    variants[index]?.let { variant ->
                        val isSelected = state.selectedVariants.any { it.id == variant.id }
                        Column {
                            VariantViewItemAdd(
                                variant = variant,
                                isEnableContextMenu = true,
                                isSelected = isSelected,
                                onCheckedChange = { variant, isChecked ->
                                    component.onEvent(OnCheckedChange(variant, isChecked))
                                },
                                onEdit = {
                                    component.onEvent(OnEditVariant(variant))
                                },
                                onDelete = {
                                    component.onEvent(OnDeleteVariant(variant))
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
                    Text(stringResource(Res.string.btn_save_selection))
                }

                Button(
                    onClick = {
                        component.onEvent(OnAddVariant)
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
                    Text(stringResource(Res.string.btn_add_variant))
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun PreviewProductAddVariantsViewScreen() {
    org.lelestacia.posle.ui.theme.AppTheme {
        ProductAddVariantsViewScreen(
            component = object : ProductAddVariantsViewComponent {
                override val state = com.arkivanov.decompose.value.MutableValue(org.lelestacia.posle.domain.state_event.product_add.ProductVariantViewState())
                override fun onEvent(event: ProductVariantViewEvent) {}
            }
        )
    }
}