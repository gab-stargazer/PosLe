package org.lelestacia.posle.ui.screen.transaction_recap_product_view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.lelestacia.posle.domain.component.transaction_recap_product_view.TransactionRecapProductViewComponent
import org.lelestacia.posle.domain.component.transaction_recap_product_view.TransactionRecapProductViewEvent
import org.lelestacia.posle.domain.component.transaction_recap_product_view.TransactionRecapProductViewState
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.BurgundyRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionRecapProductDetailScreen(
    component: TransactionRecapProductViewComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsStateWithLifecycle()
    val productName = state.transactionProducts.firstOrNull()?.product?.productName?.value ?: ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = productName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = BurgundyRed
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            component.onEvent(TransactionRecapProductViewEvent.OnPop)
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
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HorizontalDivider()
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(count = state.transactionProducts.size) { index ->
                    val item = state.transactionProducts[index]
                    TransactionProductDetailItem(
                        type = item.type,
                        product = item.product
                    )
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun PreviewTransactionRecapProductDetailScreen() {
    AppTheme {
        TransactionRecapProductDetailScreen(
            component = object : TransactionRecapProductViewComponent {
                override val state = kotlinx.coroutines.flow.MutableStateFlow(TransactionRecapProductViewState(emptyList()))
                override fun onEvent(event: TransactionRecapProductViewEvent) {}
            }
        )
    }
}
