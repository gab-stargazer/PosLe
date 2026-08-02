package org.lelestacia.posle.ui.screen.transaction_recap

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
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
import org.lelestacia.posle.domain.state_event.TransactionRecapEvent.OnNavigateToRecapProductView
import org.lelestacia.posle.domain.state_event.TransactionRecapEvent.OnNavigateToTransactionView
import org.lelestacia.posle.domain.state_event.TransactionRecapEvent.OnPrimaryTabChanged
import org.lelestacia.posle.domain.state_event.TransactionRecapState
import org.lelestacia.posle.ui.screen.transaction_history.TransactionItem
import org.lelestacia.posle.ui.screen.transaction_recap.component.TransactionRecapProductOutbound
import org.lelestacia.posle.ui.screen.transaction_recap.component.TransactionRecapTabRow
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.BurgundyRed
import org.lelestacia.posle.util.SampleData
import org.lelestacia.posle.util.toFormattedDate
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_confirm
import posle.shared.generated.resources.label_total_transaction
import posle.shared.generated.resources.txt_total_profit
import posle.shared.generated.resources.txt_total_profit_description
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionRecapScreen(
    component: TransactionRecapComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsStateWithLifecycle()

    if (state.isDateRangePickerShown) {
        DatePickerDialog(
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                dayInSelectionRangeContainerColor = BurgundyRed.copy(0.5F),
                selectedDayContainerColor = BurgundyRed
            ),
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
                        stringResource(Res.string.btn_confirm),
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
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
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
                    stringResource(Res.string.txt_total_profit, state.totalProfit.toRupiah()),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    stringResource(
                        Res.string.txt_total_profit_description,
                        state.totalProfit.toRupiah()
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = FontStyle.Italic
                    )
                )
            }

            Row {
                PrimaryTabRow(
                    divider = {},
                    indicator = {
                        TabRowDefaults.PrimaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(selectedTabIndex = state.selectedPrimaryTab),
                            color = BurgundyRed
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    selectedTabIndex = state.selectedPrimaryTab,
                    modifier = Modifier.weight(1F)
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
                            },
                            selectedContentColor = BurgundyRed,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    }
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

            HorizontalDivider()

            AnimatedContent(
                targetState = state.selectedPrimaryTab == 0,
                modifier = Modifier
                    .weight(1F)
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            ) { isProductOutbound ->
                when (isProductOutbound) {
                    true -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            items(count = state.listOfProducts.size) { index ->
                                TransactionRecapProductOutbound(
                                    transactionProducts = state.listOfProducts[index],
                                    onClick = {
                                        component.onEvent(
                                            OnNavigateToRecapProductView(
                                                state.listOfProducts[index]
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }

                    false -> {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
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
                            transactionHistory = listOf(SampleData.sampleTransaction),
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
