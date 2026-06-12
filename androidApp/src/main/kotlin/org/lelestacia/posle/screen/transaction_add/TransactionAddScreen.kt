package org.lelestacia.posle.screen.transaction_add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.lelestacia.posle.domain.component.TransactionAddComponent
import org.lelestacia.posle.domain.state_event.TransactionAddEvent
import org.lelestacia.posle.domain.state_event.TransactionAddEvent.OnAddTransactionClicked

@Composable
fun TransactionAddScreen(
    component: TransactionAddComponent,
    modifier: Modifier = Modifier
) {
    val products = component.products.collectAsLazyPagingItems()
    val state by component.state.subscribeAsState()

    Scaffold(modifier = modifier) { paddingValues ->
        Column {
            LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .weight(1F)
                    .padding(paddingValues),
            ) {
                items(count = products.itemCount, key = products.itemKey { it.id }) {
                    products[it]?.let { product ->
                        TransactionAddItem(
                            product = product,
                            productMap = state.products,
                            onAdd = {
                                component.onEvent(TransactionAddEvent.OnAddNewProduct(product))
                            },
                            onRemove = {
                                component.onEvent(TransactionAddEvent.OnRemoveProduct(product))
                            },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp))
            ) {
                Button(
                    onClick = {
                        component.onEvent(OnAddTransactionClicked)
                    },
                    shape = RoundedCornerShape(25F),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text("Simpan Transaksi")
                }
            }
        }
    }
}