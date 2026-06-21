package org.lelestacia.posle.screen

import android.Manifest
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meticha.permissions_compose.AppPermission
import com.meticha.permissions_compose.rememberAppPermissionState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.component.TransactionViewComponent
import org.lelestacia.posle.domain.component.TransactionViewNavigation
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.model.TransactionItem
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.domain.state_event.TransactionViewEvent
import org.lelestacia.posle.domain.state_event.TransactionViewEvent.OnRecapClicked
import org.lelestacia.posle.domain.state_event.TransactionViewState
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.printTransaction
import org.lelestacia.posle.util.toFormattedDateTime
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_print
import posle.shared.generated.resources.btn_recap
import posle.shared.generated.resources.label_customer
import posle.shared.generated.resources.label_total
import posle.shared.generated.resources.label_transaction_date
import posle.shared.generated.resources.label_transaction_detail
import java.math.BigDecimal
import kotlin.math.roundToInt


@Composable
fun TransactionViewScreen(
    component: TransactionViewComponent,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val state by component.state.collectAsStateWithLifecycle()

    val permissions = rememberAppPermissionState(
        permissions = listOf(
            AppPermission(
                permission = Manifest.permission.BLUETOOTH_SCAN,
                description = "Camera access is needed to take photos. Please grant this permission.",
                isRequired = true
            ),
            AppPermission(
                permission = Manifest.permission.BLUETOOTH_CONNECT,
                description = "Microphone access is needed for voice recording. Please grant this permission.",
                isRequired = false
            ),
        )
    )

    TransactionUI(
        state = state,
        onNavigation = component::onAction,
        onEvent = component::onEvent,
        onPrint = {
            if (Build.VERSION.SDK_INT >= 31 && permissions.allRequiredGranted()) {
                scope.launch {
                    printTransaction(transaction = state.transaction, storeName = state.settings.storeName)
                }
            } else if (Build.VERSION.SDK_INT >= 31) {
                permissions.requestPermission()
            } else {
                scope.launch {
                    printTransaction(transaction = state.transaction, storeName = state.settings.storeName)
                }
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionUI(
    state: TransactionViewState,
    onNavigation: (TransactionViewNavigation) -> Unit,
    onEvent: (TransactionViewEvent) -> Unit,
    onPrint: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onNavigation(TransactionViewNavigation.OnPop)
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
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .padding(paddingValues)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 12.dp,
                    top = 12.dp,
                    end = 12.dp,
                    bottom = 100.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.matchParentSize()
            ) {
                items(items = state.transaction.items, key = { it.id }) { item ->
                    TransactionViewItem(
                        item = item
                    )
                }
            }

            ElevatedCard(
                shape = RoundedCornerShape(50F),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
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
                            state
                                .transaction
                                .items
                                .sumOf { it.productAmount.value.toBigDecimal() * it.productPrice.value }
                                .toRupiah(),
                            style = MaterialTheme.typography.bodyMediumEmphasized.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    AnimatedVisibility(
                        !state.transaction.isRecapped && state.settings.isTransactionRecapNeeded,
                        enter = expandVertically(expandFrom = Alignment.Bottom) + fadeIn(),
                        exit = shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut(),
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Button(
                            onClick = {
                                onEvent(OnRecapClicked)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = RoundedCornerShape(25F),
                            modifier = modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(Res.string.btn_recap))
                        }
                    }

                    Button(
                        onClick = onPrint,
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
                                        false -> 6.dp
                                    }
                            )
                            .fillMaxWidth()
                    ) {
                        Text(stringResource(Res.string.btn_print))
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionViewItem(
    item: TransactionItem,
    modifier: Modifier = Modifier
) {

    val amount =
        if (item.productAmount.value % 1 == 0F) {
            item.productAmount.value.roundToInt()
        } else {
            item.productAmount.value
        }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = item.productName.value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = "$amount ${item.productUnit.value}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = item.productPrice.value.toRupiah(),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Text(
            text = (item.productAmount.value.toBigDecimal() * item.productPrice.value).toRupiah(),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (item.variants.isNotEmpty()) {
            TransactionViewVariantSection(
                variants = item.variants,
                amount = amount,
                totalPrice = (item.variants.sumOf {
                    item.productAmount.value.toBigDecimal() * it.priceAdjustment.value
                } + (item.productAmount.value.toBigDecimal() * item.productPrice.value)).toRupiah()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTransactionUI() {
    AppTheme {
        TransactionUI(
            state = TransactionViewState(
                transaction = Transaction(
                    id = 1,
                    customerName = Name("Budi"),
                    items = listOf(
                        TransactionItem(
                            id = 1,
                            productId = 1,
                            productName = Name("Sate Ayam"),
                            productPrice = Price(BigDecimal("15000")),
                            productUnit = org.lelestacia.posle.util.Unit("Porsi"),
                            variants = listOf(
                                Variant(
                                    id = 0,
                                    name = Name("Kerupuk"),
                                    priceAdjustment = Price(BigDecimal(5000))
                                ),
                                Variant(
                                    id = 0,
                                    name = Name("Extra Bawang"),
                                    priceAdjustment = Price(BigDecimal(5000))
                                )
                            ),
                            productAmount = Amount(2f)
                        ),
                        TransactionItem(
                            id = 2,
                            productId = 2,
                            productName = Name("Es Teh Manis"),
                            productPrice = Price(BigDecimal("5000")),
                            productUnit = org.lelestacia.posle.util.Unit("Gelas"),
                            productAmount = Amount(2f)
                        )
                    ),
                    createdAt = 1718236800000L
                )
            ),
            onNavigation = {},
            onEvent = {},
            onPrint = {}
        )
    }
}
