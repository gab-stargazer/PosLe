package org.lelestacia.posle.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.lelestacia.posle.App
import org.lelestacia.posle.domain.component.TransactionViewComponent
import org.lelestacia.posle.domain.component.TransactionViewNavigation
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.model.TransactionItem
import org.lelestacia.posle.domain.state_event.TransactionViewState
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.printTransaction
import org.lelestacia.posle.util.toFormattedDateTime
import org.lelestacia.posle.util.toRupiah
import java.math.BigDecimal


@Composable
fun TransactionViewScreen(
    component: TransactionViewComponent,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val state by component.state.collectAsStateWithLifecycle()
    TransactionUI(
        state = state,
        onNavigation = component::onAction,
        onPrint = {
            scope.launch {
                printTransaction(
                    transaction = state.transaction
                )
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
    onPrint: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Transaksi")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onNavigation(TransactionViewNavigation.OnPop)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onPrint
                    ) {
                        Icon(
                            imageVector = Icons.Default.Print,
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
                .padding(paddingValues)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 12.dp,
                    top = 12.dp,
                    end = 12.dp,
                    bottom = 100.dp
                ),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.matchParentSize()
            ) {
                items(items = state.transaction.items, key = { it.id }) { item ->
                    TransactionViewItem(
                        item = item,
                        isLast = item == state.transaction.items.last()
                    )
                }
            }

            ElevatedCard(
                shape = RoundedCornerShape(25F),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            "Total Transaksi:",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            state
                                .transaction
                                .items
                                .sumOf { it.productAmount.value.toBigDecimal() * it.productPrice.value }
                                .toRupiah(),
                            style = MaterialTheme.typography.bodyMediumEmphasized.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            "Tanggal Transaksi:",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            state.transaction.createdAt.toFormattedDateTime(),
                            style = MaterialTheme.typography.bodySmallEmphasized.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionViewItem(
    item: TransactionItem,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
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
                text = item.productPrice.value.toRupiah(),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(Modifier.weight(1F))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.weight(2F)
            ) {
                Text(
                    text = "${
                        item.productAmount.value.toBigDecimal().stripTrailingZeros()
                    } ${item.productUnit.value}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = (item.productAmount.value.toBigDecimal() * item.productPrice.value).toRupiah(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        if (!isLast) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 6.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTransactionUI() {
    App {
        TransactionUI(
            state = TransactionViewState(
                transaction = Transaction(
                    id = 1,
                    customerName = Name("Budi"),
                    items = listOf(
                        TransactionItem(
                            id = 1,
                            productName = Name("Sate Ayam"),
                            productPrice = Price(BigDecimal("15000")),
                            productUnit = org.lelestacia.posle.util.Unit("Porsi"),
                            productAmount = Amount(2f)
                        ),
                        TransactionItem(
                            id = 2,
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
            onPrint = {}
        )
    }
}
