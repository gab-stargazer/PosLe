package org.lelestacia.posle.screen.transaction_recap

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.component.transaction_recap.TransactionRecapComponent
import org.lelestacia.posle.domain.state_event.TransactionRecapEvent
import org.lelestacia.posle.domain.state_event.TransactionRecapEvent.OnDateRangePickerVisibilityChanged
import org.lelestacia.posle.domain.state_event.TransactionRecapEvent.OnNavigateToTransactionView
import org.lelestacia.posle.domain.state_event.TransactionRecapEvent.OnPrimaryTabChanged
import org.lelestacia.posle.domain.state_event.TransactionRecapState
import org.lelestacia.posle.screen.transaction_history.TransactionItem
import org.lelestacia.posle.screen.transaction_recap.component.TransactionRecapProductOutbound
import org.lelestacia.posle.screen.transaction_recap.component.TransactionRecapTabRow
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.SampleData
import org.lelestacia.posle.util.toFormattedDate
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_total_transaction
import posle.shared.generated.resources.txt_total_profit
import posle.shared.generated.resources.txt_total_profit_description
import kotlin.time.Clock

@Composable
fun TransactionRecapScreen(
    component: TransactionRecapComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsStateWithLifecycle()
    val productsOutbound =
        state.transactionHistory
            .flatMap { it.items }
            .groupBy { it.productId }
            .map { it.value }
            .toList()

    val totalProfit = state.transactionHistory
        .sumOf { transaction ->
            transaction.items
                .sumOf { product ->
                    product.productAmount.value.toBigDecimal() * (product.productSellPrice.value - product.productBuyPrice.value)
                }
        }


    if (state.isDateRangePickerShown) {
        DatePickerDialog(
            onDismissRequest = {
                component.onEvent(OnDateRangePickerVisibilityChanged(false))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        component.onEvent(
                            TransactionRecapEvent.OnDateRangeChanged(
                                Pair(
                                    state.dateRangePickerState.selectedStartDateMillis ?: 0,
                                    state.dateRangePickerState.selectedEndDateMillis ?: 0
                                )
                            )
                        )

                        component.onEvent(
                            OnDateRangePickerVisibilityChanged(
                                false
                            )
                        )
                    }
                ) {
                    Text(
                        "Konfirmasi",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        ) {
            DateRangePicker(state = state.dateRangePickerState)
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
            Row(
                modifier = Modifier.padding(12.dp)
            ) {
                Column {
                    Text(
                        when {
                            state.isSameDay -> "Rekap ${state.startDate.toFormattedDate()}"
                            else -> "${state.startDate.toFormattedDate()} - ${state.finishDate.toFormattedDate()}"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Text(
                        stringResource(
                            Res.string.label_total_transaction,
                            state.transactionHistory.size
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        stringResource(Res.string.txt_total_profit, totalProfit.toRupiah()),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        stringResource(
                            Res.string.txt_total_profit_description,
                            totalProfit.toRupiah()
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = FontStyle.Italic
                        )
                    )
                }

                IconButton(
                    onClick = {
                        component.onEvent(OnDateRangePickerVisibilityChanged(true))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = Icons.Default.CalendarMonth.name
                    )
                }
            }

            PrimaryTabRow(
                selectedTabIndex = state.selectedPrimaryTab
            ) {
                TransactionRecapTabRow.entries.forEachIndexed { index, destination ->
                    Tab(
                        selected = state.selectedPrimaryTab == index,
                        onClick = {
                            component.onEvent(OnPrimaryTabChanged(index))
                        },
                        text = {
                            Text(
                                text = stringResource(destination.title),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    )
                }
            }

            AnimatedContent(
                targetState = state.selectedPrimaryTab == 0,
                modifier = Modifier.weight(1F)
            ) { isProductOutbound ->
                when (isProductOutbound) {
                    true -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            items(count = productsOutbound.size) { index ->
                                TransactionRecapProductOutbound(
                                    transactionItems = productsOutbound[index],
                                    onClick = {

                                    }
                                )
                            }
                        }
                    }

                    false -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            items(count = state.transactionHistory.size) { index ->
                                val transaction = state.transactionHistory[index]
                                Column {
                                    TransactionItem(
                                        transaction = transaction,
                                        onClick = {
                                            component.onEvent(
                                                OnNavigateToTransactionView(
                                                    transaction
                                                )
                                            )
                                        },
                                        settings = state.settings
                                    )
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewTransactionRecapScreen() {
    AppTheme {
        TransactionRecapScreen(
            component = object : TransactionRecapComponent {
                override val state: MutableStateFlow<TransactionRecapState> =
                    MutableStateFlow(
                        TransactionRecapState(
                            transactionHistory = SampleData.largeTransaction,
                            startDate = Clock.System.now().toEpochMilliseconds(),
                            finishDate = Clock.System.now().toEpochMilliseconds(),
                            isSameDay = true
                        )
                    )

                override fun onEvent(event: TransactionRecapEvent) {

                }
            }
        )
    }
}