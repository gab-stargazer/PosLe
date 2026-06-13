package org.lelestacia.posle.screen.transaction_history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.component.TransactionHistoryComponent
import org.lelestacia.posle.navigation.Config
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_add_transaction

@Composable
fun TransactionHistoryScreen(
    component: TransactionHistoryComponent,
    modifier: Modifier = Modifier
) {
    val transactionHistory = component.history.collectAsLazyPagingItems()
    val settings by component.settings.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = WindowInsets(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Riwayat Transaksi",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    component.onNavigation(Config.TransactionAdd)
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
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues = paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
            items(
                count = transactionHistory.itemCount,
                key = transactionHistory.itemKey { it.id }) {
                transactionHistory[it]?.let { transaction ->
                    Column(modifier = Modifier.animateItem()) {
                        TransactionItem(
                            transaction = transaction,
                            settings = settings,
                            onClick = {
                                component.onNavigation(
                                    Config.TransactionView(
                                        transaction = transaction
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