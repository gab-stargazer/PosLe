package org.lelestacia.posle.screen.transaction_history

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.domain.component.TransactionHistoryScreenComponent
import org.lelestacia.posle.domain.component.TransactionHistoryScreenEvent
import org.lelestacia.posle.domain.component.TransactionHistoryScreenEvent.OnNavigate
import org.lelestacia.posle.domain.component.TransactionHistoryScreenEvent.OnTabSelected
import org.lelestacia.posle.domain.component.TransactionHistoryScreenState
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.navigation.Config
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.SampleData
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_add_transaction
import posle.shared.generated.resources.label_all
import posle.shared.generated.resources.label_not_recapped

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun TransactionHistoryScreen(
    component: TransactionHistoryScreenComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsStateWithLifecycle()

    //  Paging
    val transactionHistory = component.allHistory.collectAsLazyPagingItems()
    val unRecappedTransactionHistory = component.unRecappedHistory.collectAsLazyPagingItems()



    Scaffold(
        contentWindowInsets = WindowInsets(),
        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        stringResource(Res.string.label_transaction_history),
//                        style = MaterialTheme.typography.titleMedium.copy(
//                            fontWeight = FontWeight.Bold
//                        )
//                    )
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
//                    titleContentColor = MaterialTheme.colorScheme.onSurface
//                )
//            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    component.onEvent(OnNavigate(Config.TransactionAdd))
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        stringResource(Res.string.btn_add_transaction),
                        style = MaterialTheme.typography.bodyMediumEmphasized.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
            TransactionHistoryScreenHeader(
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
            )

            AnimatedContent(
                state.settings.isTransactionRecapNeeded,
                modifier = Modifier.weight(1F)
            ) { isTransactionRecapNeeded ->
                when (isTransactionRecapNeeded) {
                    true -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            PrimaryTabRow(
                                selectedTabIndex = state.selectedTab,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            ) {
                                TransactionHistorySubscreen.entries.forEachIndexed { index, subscreen ->
                                    Tab(
                                        selected = state.selectedTab == index,
                                        onClick = {
                                            component.onEvent(OnTabSelected(index))
                                        },
                                        text = {
                                            Text(text = stringResource(resource = subscreen.display))
                                        }
                                    )
                                }
                            }

                            AnimatedContent(
                                state.selectedTab == 0,
                                modifier = Modifier.weight(1F)
                            ) { isNotRecappedSelected ->
                                when (isNotRecappedSelected) {
                                    true -> {
                                        LazyColumn(
                                            contentPadding = PaddingValues(bottom = 96.dp),
                                            modifier = Modifier
                                                .weight(1F)
                                        ) {
                                            items(
                                                count = unRecappedTransactionHistory.itemCount,
                                                key = unRecappedTransactionHistory.itemKey { it.id }
                                            ) {
                                                unRecappedTransactionHistory[it]?.let { transaction ->
                                                    Column(modifier = Modifier.animateItem()) {
                                                        TransactionItem(
                                                            transaction = transaction,
                                                            settings = state.settings,
                                                            onClick = {
                                                                component.onEvent(
                                                                    OnNavigate(
                                                                        Config.TransactionView(
                                                                            transaction = transaction
                                                                        )
                                                                    )
                                                                )
                                                            }
                                                        )

                                                        if (it != unRecappedTransactionHistory.itemCount - 1) {
                                                            HorizontalDivider(modifier = Modifier.fillMaxWidth())
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    false -> {
                                        LazyColumn(
                                            contentPadding = PaddingValues(bottom = 96.dp),
                                            modifier = Modifier
                                                .weight(1F)
                                        ) {
                                            items(
                                                count = transactionHistory.itemCount,
                                                key = transactionHistory.itemKey { it.id }) {
                                                transactionHistory[it]?.let { transaction ->
                                                    Column(modifier = Modifier.animateItem()) {
                                                        TransactionItem(
                                                            transaction = transaction,
                                                            settings = state.settings,
                                                            onClick = {
                                                                component.onEvent(
                                                                    OnNavigate(
                                                                        Config.TransactionView(
                                                                            transaction = transaction
                                                                        )
                                                                    )
                                                                )
                                                            }
                                                        )

                                                        if (it != transactionHistory.itemCount - 1) {
                                                            HorizontalDivider(modifier = Modifier.fillMaxWidth())
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    false -> {
                        Column {
                            HorizontalDivider()

                            LazyColumn(
                                contentPadding = PaddingValues(bottom = 96.dp),
                                modifier = Modifier
                                    .weight(1F)
                            ) {
                                items(
                                    count = transactionHistory.itemCount,
                                    key = transactionHistory.itemKey { it.id }) {
                                    transactionHistory[it]?.let { transaction ->
                                        Column(modifier = Modifier.animateItem()) {
                                            TransactionItem(
                                                transaction = transaction,
                                                settings = state.settings,
                                                onClick = {
                                                    component.onEvent(
                                                        OnNavigate(
                                                            Config.TransactionView(
                                                                transaction = transaction
                                                            )
                                                        )
                                                    )
                                                }
                                            )

                                            if (it != transactionHistory.itemCount - 1) {
                                                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTransactionHistoryScreen() {
    AppTheme {
        TransactionHistoryScreen(
            component = object : TransactionHistoryScreenComponent {

                override val unRecappedHistory: Flow<PagingData<Transaction>>
                    get() = flowOf(PagingData.from(SampleData.largeTransaction))

                override val allHistory: Flow<PagingData<Transaction>>
                    get() = flowOf(PagingData.from(SampleData.largeTransaction))

                override val state: StateFlow<TransactionHistoryScreenState>
                    get() = MutableStateFlow(
                        TransactionHistoryScreenState(
                            settings = PosLeSettings(isTransactionRecapNeeded = true)
                        )
                    )

                override fun onEvent(event: TransactionHistoryScreenEvent) {

                }
            }
        )
    }
}

enum class TransactionHistorySubscreen(val display: StringResource) {
    NOT_RECAPPED(Res.string.label_not_recapped),
    ALL(Res.string.label_all)
}