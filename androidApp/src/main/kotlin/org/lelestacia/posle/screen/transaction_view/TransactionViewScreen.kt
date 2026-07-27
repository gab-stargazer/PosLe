package org.lelestacia.posle.screen.transaction_view

import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.data.entity.TransactionItemType
import org.lelestacia.posle.domain.component.TransactionViewComponent
import org.lelestacia.posle.domain.component.TransactionViewNavigation
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.model.TransactionItem
import org.lelestacia.posle.domain.model.TransactionProduct
import org.lelestacia.posle.domain.state_event.TransactionViewEvent
import org.lelestacia.posle.domain.state_event.TransactionViewEvent.OnChangeSaveLoadingState
import org.lelestacia.posle.domain.state_event.TransactionViewEvent.OnRecapClicked
import org.lelestacia.posle.domain.state_event.TransactionViewEvent.OnShowMessage
import org.lelestacia.posle.domain.state_event.TransactionViewState
import org.lelestacia.posle.screen.transaction_history.component.TransactionReceipt
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.BurgundyRed
import org.lelestacia.posle.ui.theme.MintCream
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.Util
import org.lelestacia.posle.util.printTransaction
import org.lelestacia.posle.util.toFormattedDateTime
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_print_digital
import posle.shared.generated.resources.btn_print_physical
import posle.shared.generated.resources.btn_recap
import posle.shared.generated.resources.label_customer
import posle.shared.generated.resources.label_total
import posle.shared.generated.resources.label_transaction_date
import posle.shared.generated.resources.label_transaction_detail
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import org.lelestacia.posle.util.Unit as PosleUnit


@OptIn(ExperimentalUuidApi::class)
@Composable
fun TransactionViewScreen(
    isBluetoothPermissionGranted: Boolean,
    onRequestBluetoothPermission: () -> Unit,
    component: TransactionViewComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val graphicsLayer = rememberGraphicsLayer()
    val ioScope = rememberCoroutineScope { Dispatchers.IO }
    val mainScope = rememberCoroutineScope()

    LaunchedEffect(state.isSaveProcessing) {
        mainScope.launch {
            if (state.isSaveProcessing) {
                val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()

                val values = ContentValues().apply {
                    put(
                        MediaStore.Images.Media.DISPLAY_NAME,
                        "Transaksi-${Uuid.generateV7()}.png"
                    )
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + "/PosLe"
                    )
                }

                val uri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                )

                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        stream.close()

                        delay(500.milliseconds)
                        component.onEvent(OnChangeSaveLoadingState(isLoading = false))
                        component.onEvent(OnShowMessage("Struk berhasil disimpan"))
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.label_transaction_detail),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            component.onEvent(
                                TransactionViewEvent.OnNavigateTo(
                                    TransactionViewNavigation.OnPop
                                )
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets(),
        modifier = modifier
    ) { paddingValues ->

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
            item {
                TransactionReceipt(
                    storeName = state.settings.storeName,
                    customerName = state.transaction.customerName,
                    transactionProduct = state.transaction.items.toImmutableList(),
                    transactionDate = state.transaction.createdAt,
                    modifier = Modifier
                        .drawWithContent {
                            graphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }

                            drawLayer(graphicsLayer)
                        }
                )
            }

            item {
                ElevatedCard(
                    shape = Util.defaultShape,
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MintCream
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                "${stringResource(Res.string.label_transaction_date)}:",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                state.transaction.createdAt.toFormattedDateTime(),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        if (state.transaction.customerName.value.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    "${stringResource(Res.string.label_customer)}:",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    state.transaction.customerName.value,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }

                        val totalTransaction = state
                            .transaction
                            .items
                            .map { transactionItem ->
                                transactionItem.sellPrice.value * transactionItem.quantity.value
                            }
                            .sumOf { it }

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                "${stringResource(Res.string.label_total)}:",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                totalTransaction.toRupiah(),
                                style = MaterialTheme.typography.bodyMediumEmphasized.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        AnimatedVisibility(
                            visible = (!state.transaction.isRecapped && state.settings.isTransactionRecapNeeded),
                            enter = expandVertically(expandFrom = Alignment.Bottom) + fadeIn(),
                            exit = shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut(),
                            modifier = Modifier.padding(top = 12.dp)
                        ) {
                            Button(
                                onClick = {
                                    component.onEvent(OnRecapClicked)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BurgundyRed,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                shape = RoundedCornerShape(25F),
                                modifier = modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(Res.string.btn_recap))
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                component.onEvent(
                                    OnChangeSaveLoadingState(
                                        isLoading = true
                                    )
                                )
                            },
                            border = BorderStroke(1.dp, BurgundyRed),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = BurgundyRed
                            ),
                            shape = RoundedCornerShape(25F),
                            modifier = Modifier
                                .padding(
                                    top =
                                        when (state.transaction.isRecapped) {
                                            true -> 12.dp
                                            false -> 8.dp
                                        }
                                )
                                .fillMaxWidth()
                                .animateContentSize()
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.animateContentSize()
                            ) {
                                Text(stringResource(Res.string.btn_print_digital))

                                AnimatedVisibility(
                                    visible = state.isSaveProcessing,
                                    enter = fadeIn(),
                                    exit = fadeOut()
                                ) {
                                    LoadingIndicator(
                                        color = BurgundyRed,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (isBluetoothPermissionGranted) {
                                    ioScope.launch {
                                        printTransaction(
                                            transaction = state.transaction,
                                            storeName = state.settings.storeName
                                        )
                                    }
                                } else {
                                    onRequestBluetoothPermission.invoke()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            shape = RoundedCornerShape(25F),
                            modifier = Modifier
                                .padding(
                                    top =
                                        when (state.transaction.isRecapped) {
                                            true -> 12.dp
                                            false -> 8.dp
                                        }
                                )
                                .fillMaxWidth()
                        ) {
                            Text(stringResource(Res.string.btn_print_physical))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTransactionUI() {
    AppTheme {
        TransactionViewScreen(
            component = object : TransactionViewComponent {
                override val state: StateFlow<TransactionViewState> = MutableStateFlow(
                    TransactionViewState(
                        isSaveProcessing = true,
                        transaction = Transaction(
                            id = 0,
                            customerName = Name("Rudi"),
                            items = listOf(
                                TransactionItem(
                                    id = 1,
                                    type = TransactionItemType.Product,
                                    referenceId = 1,
                                    name = Name("Salak Pondoh"),
                                    quantity = Amount(java.math.BigDecimal("50")),
                                    sellPrice = Price(5000.toBigDecimal()),
                                    note = "2 Karung",
                                    products = listOf(
                                        TransactionProduct(
                                            productId = 1,
                                            productName = Name("Salak Pondoh"),
                                            skuNumber = null,
                                            imageUri = null,
                                            buyPrice = Price(0.toBigDecimal()),
                                            sellPrice = Price(5000.toBigDecimal()),
                                            unit = PosleUnit("Kg"),
                                            note = "2 Karung",
                                            quantity = Amount(java.math.BigDecimal("50")),
                                            variants = emptyList()
                                        )
                                    ),
                                    createdAt = Clock.System.now().toEpochMilliseconds(),
                                    updatedAt = null
                                )
                            ),
                            isRecapped = false,
                            createdAt = Clock.System.now().toEpochMilliseconds(),
                            updatedAt = null
                        ),
                        settings = PosLeSettings(isTransactionRecapNeeded = true)
                    )
                )

                override fun onEvent(event: TransactionViewEvent) {
                    TODO("Not yet implemented")
                }
            },
            isBluetoothPermissionGranted = true,
            onRequestBluetoothPermission = {}
        )
    }
}
