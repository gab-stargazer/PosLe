package org.lelestacia.posle.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import kotlinx.coroutines.launch
import org.lelestacia.posle.domain.component.TransactionViewComponent
import org.lelestacia.posle.util.toRupiah


@Composable
fun TransactionViewScreen(
    component: TransactionViewComponent,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val state by component.state.subscribeAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Transaksi")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {

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
                        onClick = {
                            scope.launch {
                                val printer = EscPosPrinter(
                                    BluetoothPrintersConnections.selectFirstPaired(),
                                    203,
                                    58f,
                                    32
                                )

                                val text =
                                    "[C]<u><font size='big'>Transaksi PosLe</font></u>\n" +
                                            "[L]\n" +
                                            "[C]================================\n" +
                                            "[L]\n"
                                val sb = StringBuilder(text)
                                state.transaction.items.forEach {
                                    sb.append(
                                        "[L]${it.productName.value}[R]${it.productPrice.value.toRupiah()}\n"
                                    )
                                    sb.append(
                                        "[L]  + ${
                                            it.productAmount.value.toBigDecimal()
                                                .stripTrailingZeros()
                                        } ${it.productUnit.value}[R]${(it.productAmount.value.toBigDecimal() * it.productPrice.value).toRupiah()}\n"
                                    )
                                    sb.append(
                                        "[L]\n"
                                    )
                                }

                                sb.append(
                                    "[C]--------------------------------\n" +
                                            "[R]TOTAL PRICE :[R]${
                                                state.transaction.items.sumOf { it.productAmount.value.toBigDecimal() * it.productPrice.value }
                                                    .toRupiah()
                                            }\n" +
                                            "[C]================================\n" +
                                            "[C]Terimakasih telah datang\n"
                                )

                                printer.printFormattedText(sb.toString())
                            }
                        }
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
        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(paddingValues)
        ) {
            items(items = state.transaction.items, key = { it.id }) { item ->
                Column(
                    modifier = Modifier.fillMaxWidth()
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

                    if (item != state.transaction.items.last()) {
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .padding(top = 6.dp)
                        )
                    }
                }
            }
        }
    }
}