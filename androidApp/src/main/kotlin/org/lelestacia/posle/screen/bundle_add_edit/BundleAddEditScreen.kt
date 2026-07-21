package org.lelestacia.posle.screen.bundle_add_edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.component.bundle_add_edit.BundleAddEditComponent
import org.lelestacia.posle.domain.component.bundle_add_edit.BundleAddEditEvent
import org.lelestacia.posle.domain.component.bundle_add_edit.BundleAddEditEvent.BundleProductEvent.OnQuantityChanged
import org.lelestacia.posle.domain.component.bundle_add_edit.BundleAddEditEvent.BundleProductEvent.OnSellPriceChanged
import org.lelestacia.posle.domain.component.bundle_add_edit.BundleAddEditEvent.OnBundleAddClicked
import org.lelestacia.posle.domain.component.bundle_add_edit.BundleAddEditEvent.OnBundleNameChanged
import org.lelestacia.posle.domain.component.bundle_add_edit.BundleAddEditEvent.OnBundleProductRemoved
import org.lelestacia.posle.domain.component.bundle_add_edit.BundleAddEditEvent.OnProductNameChanged
import org.lelestacia.posle.domain.component.bundle_add_edit.BundleAddEditState
import org.lelestacia.posle.domain.component.bundle_add_edit.BundleProductState
import org.lelestacia.posle.screen.bundle_add_edit.component.BundleItem
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.BurgundyRed
import org.lelestacia.posle.ui.theme.MintCream
import org.lelestacia.posle.util.SampleData
import org.lelestacia.posle.util.Util
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_save_bundle
import posle.shared.generated.resources.label_bundle_name
import posle.shared.generated.resources.label_product_name
import posle.shared.generated.resources.txt_add_product_to_bundle
import posle.shared.generated.resources.txt_add_product_to_bundle_description

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BundleAddEditScreen(
    component: BundleAddEditComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsStateWithLifecycle()
    val keyboardManager = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    Scaffold(
        contentWindowInsets = WindowInsets(),
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MintCream)
                .padding(paddingValues)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1F)
            ) {

                items(count = state.bundleProducts.size, key = { it }) { index ->
                    val bundledProduct = state.bundleProducts[index]
                    Column(
                        modifier = Modifier.animateItem()
                    ) {
                        BundleItem(
                            bundledProduct = bundledProduct,
                            onQuantityChange = { newQuantity ->
                                component.onEvent(OnQuantityChanged(index, newQuantity))
                            },
                            onPriceChange = { newSellPrice ->
                                component.onEvent(OnSellPriceChanged(index, newSellPrice))
                            },
                            onDelete = {
                                component.onEvent(OnBundleProductRemoved(bundledProduct.product))
                            },
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }

                item(key = "add_product") {
                    var isExpanded by remember {
                        mutableStateOf(false)
                    }

                    Column(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(horizontal = 12.dp)
                            .animateItem()
                    ) {
                        Text(
                            stringResource(Res.string.txt_add_product_to_bundle),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Text(
                            stringResource(Res.string.txt_add_product_to_bundle_description),
                            style = MaterialTheme.typography.bodySmall
                        )

                        ExposedDropdownMenuBox(
                            expanded = isExpanded,
                            onExpandedChange = { newState ->
                                isExpanded = newState
                            },
                            modifier = Modifier
                                .padding(top = 6.dp)
                        ) {
                            OutlinedTextField(
                                value = state.productName,
                                onValueChange = { newProductName ->
                                    component.onEvent(OnProductNameChanged(newProductName))
                                },
                                label = {
                                    Text(
                                        stringResource(Res.string.label_product_name),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                },
                                textStyle = MaterialTheme.typography.bodyMedium,
                                shape = Util.defaultShape,
                                modifier = Modifier
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = isExpanded,
                                onDismissRequest = {
                                    isExpanded = false
                                },
                            ) {
                                state.availableProducts.forEach { product ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = product.name.value,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        },
                                        onClick = {
                                            isExpanded = false
                                            keyboardManager?.hide()
                                            focusManager.clearFocus(true)
                                            component.onEvent(
                                                BundleAddEditEvent.OnBundleProductAdded(product)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(topStart = 25F, topEnd = 25F),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceDim
                ),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .navigationBarsPadding()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(25F))
                            .background(MintCream)
                    ) {
                        TextField(
                            value = state.bundleName,
                            onValueChange = { newBundleName ->
                                component.onEvent(OnBundleNameChanged(newBundleName))
                            },
                            label = {
                                Text(
                                    stringResource(Res.string.label_bundle_name),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            },
                            textStyle = MaterialTheme.typography.bodyMedium,
                            colors = Util.defaultTransparentTextFieldColor(),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus(true)
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }

                    Button(
                        onClick = {
                            component.onEvent(OnBundleAddClicked)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BurgundyRed
                        ),
                        shape = Util.defaultShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.btn_save_bundle),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewBundleAddEditScreen() {
    AppTheme {
        BundleAddEditScreen(
            component = object : BundleAddEditComponent {
                override val state = MutableStateFlow(
                    BundleAddEditState(
                        bundleProducts = listOf(
                            BundleProductState(
                                product = SampleData.products.first(),
                            )
                        )
                    )
                )

                override fun onEvent(event: BundleAddEditEvent) {}
            }
        )
    }
}