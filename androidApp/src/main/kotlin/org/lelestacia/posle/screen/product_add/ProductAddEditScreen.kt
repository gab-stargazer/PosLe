package org.lelestacia.posle.screen.product_add

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.meticha.permissions_compose.AppPermission
import com.meticha.permissions_compose.rememberAppPermissionState
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.component.product_add_edit.ProductAddEditComponent
import org.lelestacia.posle.domain.model.ProductPriceHistory
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.Navigation.OnNavigateToQrScanner
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.Navigation.OnPop
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.OnAddProductClicked
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.OnAddStockEvent
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.OnDeleteProductClicked
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.OnImageChanged
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.OnProductModalPriceChange
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.OnProductModalPriceRequestValidation
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.OnProductNameChange
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.OnProductNameRequestValidation
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.OnProductUnitChange
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.OnProductUnitRequestValidation
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditState
import org.lelestacia.posle.navigation.AddEdit.Add
import org.lelestacia.posle.navigation.AddEdit.Edit
import org.lelestacia.posle.ui.component.BorderedTextField
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.BurgundyRed
import org.lelestacia.posle.ui.theme.successLightHighContrast
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.Util
import org.lelestacia.posle.util.handleImagePick
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_add_product
import posle.shared.generated.resources.btn_delete_product
import posle.shared.generated.resources.btn_update_product
import posle.shared.generated.resources.label_camera_permission
import posle.shared.generated.resources.label_product_name
import posle.shared.generated.resources.label_product_sku_number
import posle.shared.generated.resources.label_product_unit
import posle.shared.generated.resources.title_add_product
import posle.shared.generated.resources.title_add_stock_movement
import posle.shared.generated.resources.title_edit_product
import java.math.BigDecimal
import kotlin.time.Clock

@Composable
fun ProductAddEditScreen(
    component: ProductAddEditComponent,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    val state by component.state.collectAsStateWithLifecycle()
    val cameraPermission = rememberAppPermissionState(
        listOf(AppPermission(Manifest.permission.CAMERA, stringResource(Res.string.label_camera_permission), isRequired = true))
    )

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val isScrolled by remember {
        derivedStateOf {
            scrollState.value > 0
        }
    }

    val appBarContainerColor by animateColorAsState(
        targetValue = if (isScrolled) {
            BurgundyRed
        } else {
            MaterialTheme.colorScheme.surfaceContainerLowest
        },
        label = "ProductAddEditAppBarContainerColor"
    )

    val appBarContentColor by animateColorAsState(
        targetValue = if (isScrolled) {
            MaterialTheme.colorScheme.surfaceContainerLowest
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        label = "ProductAddEditAppBarContainerColor"
    )

    val imagePicker = rememberFilePickerLauncher(
        type = FileKitType.Image
    ) {
        scope.launch {
            context.handleImagePick(
                file = it,
                onPicked = { uri, bytes ->
                    component.onEvent(OnImageChanged(uri, bytes))
                }
            )
        }
    }

    if (state.isDialogAddStockShown) {
        Dialog(
            onDismissRequest = {
                component.onEvent(OnAddStockEvent.OnDismiss)
            }
        ) {
            ProductAddStockDialog(
                state = state.dialogAddStockState,
                onEvent = component::onEvent
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (state.mode) {
                            Add -> stringResource(Res.string.title_add_product)
                            Edit -> stringResource(Res.string.title_edit_product)
                        },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = appBarContainerColor,
                    titleContentColor = appBarContentColor,
                    navigationIconContentColor = appBarContentColor
                ),
                actions = {
                    var isExpanded by remember { mutableStateOf(false) }
                    if (state.mode == Edit) {
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
                    }

                    DropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = {
                            isExpanded = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    stringResource(Res.string.title_add_stock_movement),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            onClick = {
                                component.onEvent(OnAddStockEvent.OnShown)
                            }
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            component.onEvent(OnPop)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = Icons.AutoMirrored.Filled.ArrowBack.name
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
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {

            BorderedTextField(
                value = state.productName,
                onValueChange = { newProductName ->
                    component.onEvent(OnProductNameChange(newProductName))
                },
                label = stringResource(Res.string.label_product_name),
                trailingIcon = {
                    AnimatedVisibility(
                        visible = state.isProductNameValidated,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = successLightHighContrast
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        component.onEvent(OnProductNameRequestValidation)
                        focusManager.clearFocus(true)
                    }
                ),
                errorMessage = state.productNameError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .padding(horizontal = 12.dp)
            )

            BorderedTextField(
                value = state.skuNumber,
                onValueChange = {},
                readOnly = true,
                label = stringResource(Res.string.label_product_sku_number),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (cameraPermission.allRequiredGranted()) {
                                component.onEvent(OnNavigateToQrScanner)
                            } else {
                                cameraPermission.requestPermission()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = Icons.Default.QrCodeScanner.name
                        )
                    }
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .padding(horizontal = 12.dp)
            )

            BorderedTextField(
                value = state.productUnit,
                onValueChange = { newProductUnit ->
                    component.onEvent(OnProductUnitChange(newProductUnit))
                },
                label = stringResource(Res.string.label_product_unit),
                trailingIcon = {
                    AnimatedVisibility(
                        visible = state.isProductUnitValidated,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = successLightHighContrast
                        )
                    }
                },
                errorMessage = state.productUnitError,
                textStyle = MaterialTheme.typography.bodyMedium,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        component.onEvent(OnProductUnitRequestValidation)
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .padding(horizontal = 12.dp)
            )


            ProductAddEditSectionModalPrice(
                state = state,
                onModalPriceChange = { newModalPrice ->
                    component.onEvent(OnProductModalPriceChange(newModalPrice))
                },
                onModalPriceRequestValidation = {
                    component.onEvent(OnProductModalPriceRequestValidation)
                },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(horizontal = 12.dp)
            )

            ProductAddEditSectionSellPrice(state, component::onEvent)

            AnimatedVisibility(state.productImageUri != null) {
                AsyncImage(
                    model = state.productImageUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(horizontal = 12.dp)
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(25F))
                )
            }

            ProductAddEditDeleteImageButton(
                isEditMode = state.productImageUri != null,
                onAddOrChange = {
                    imagePicker.launch()
                },
                onDelete = {
                    component.onEvent(OnImageChanged(null, null))
                },
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp)
            )

            Button(
                onClick = {
                    component.onEvent(OnAddProductClicked)
                },
                shape = Util.defaultShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurgundyRed
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp)
            ) {
                Text(
                    text =
                        when (state.mode) {
                            Add -> stringResource(resource = Res.string.btn_add_product)
                            Edit -> stringResource(resource = Res.string.btn_update_product)
                        },
                    style = MaterialTheme.typography.labelMediumEmphasized.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.surfaceContainerLowest
                    )
                )
            }

            if (state.mode == Edit) {
                Button(
                    onClick = {
                        component.onEvent(OnDeleteProductClicked)
                    },
                    shape = RoundedCornerShape(25F),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(top = 6.dp, bottom = 128.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.btn_delete_product),
                        style = MaterialTheme.typography.labelMediumEmphasized.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProductAddEditUI() {
    AppTheme {
        var state by remember {
            mutableStateOf(
                ProductAddEditState(
                    mode = Edit,
                    productName = "Nasi Goreng",
                    productUnit = "Porsi",
                    productSellPrice = "10000",
                    variants = listOf(
                        Variant(
                            id = 2,
                            name = Name("Pedas"),
                            priceAdjustment = Price(BigDecimal(2000))
                        ),
                        Variant(
                            id = 3,
                            name = Name("Ekstra Nasi"),
                            priceAdjustment = Price(BigDecimal(5000))
                        ),
                        Variant(
                            id = 4,
                            name = Name("Telur Ceplok"),
                            priceAdjustment = Price(BigDecimal(3000))
                        ),
                        Variant(
                            id = 5,
                            name = Name("Tanpa Bawang"),
                            priceAdjustment = Price(BigDecimal.ZERO)
                        )
                    ),
                    buyPriceHistory = List(3) {
                        ProductPriceHistory(
                            id = it,
                            price = Price(it.toBigDecimal() * 1000.toBigDecimal()),
                            changes = BigDecimal.ZERO,
                            createdAt = Clock.System.now().toEpochMilliseconds()
                        )
                    },
                    sellPriceHistory = List(3) {
                        ProductPriceHistory(
                            id = it,
                            price = Price(it.toBigDecimal() * 1000.toBigDecimal()),
                            changes = BigDecimal.ZERO,
                            createdAt = Clock.System.now().toEpochMilliseconds()
                        )
                    }
                )
            )
        }

        ProductAddEditScreen(
            component = object : ProductAddEditComponent {
                override val state: StateFlow<ProductAddEditState>
                    get() = MutableStateFlow(state)

                override fun onEvent(event: ProductAddEditEvent) {

                }
            }
        )
    }
}
