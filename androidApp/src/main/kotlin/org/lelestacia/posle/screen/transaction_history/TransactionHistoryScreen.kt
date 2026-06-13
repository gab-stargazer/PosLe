package org.lelestacia.posle.screen.transaction_history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import org.lelestacia.posle.domain.component.TransactionHistoryComponent
import org.lelestacia.posle.navigation.Config

@Composable
fun TransactionHistoryScreen(
    component: TransactionHistoryComponent,
    modifier: Modifier = Modifier
) {
    val transactionHistory = component.history.collectAsLazyPagingItems()
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
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    component.onNavigation(Config.TransactionAdd)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues = paddingValues)) {
            items(
                count = transactionHistory.itemCount,
                key = transactionHistory.itemKey { it.id }) {
                transactionHistory[it]?.let { transaction ->
                    Column(modifier = Modifier.animateItem()) {
                        TransactionItem(
                            transaction = transaction,
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