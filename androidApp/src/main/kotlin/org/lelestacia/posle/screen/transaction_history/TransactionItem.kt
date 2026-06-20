package org.lelestacia.posle.screen.transaction_history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.model.TransactionItem
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.successLight
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.SampleData
import org.lelestacia.posle.util.toFormattedDateTime
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_customer
import posle.shared.generated.resources.label_not_recapped
import posle.shared.generated.resources.label_other_products
import posle.shared.generated.resources.label_product
import posle.shared.generated.resources.label_recapped
import posle.shared.generated.resources.label_total
import posle.shared.generated.resources.label_transaction_time

@Composable
fun TransactionItem(
    transaction: Transaction,
    settings: PosLeSettings,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .clickable(onClick = onClick)
            .padding(all = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1F)
        ) {
            Text(
                "${stringResource(Res.string.label_transaction_time)}: ${transaction.createdAt.toFormattedDateTime()}",
                style = MaterialTheme.typography.bodyMedium
            )

            val customerStringBuilder = buildAnnotatedString {
                withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                    append("${stringResource(Res.string.label_customer)}: ")
                }

                withStyle(
                    MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ).toSpanStyle()
                ) {
                    append(transaction.customerName.value)
                }
            }

            if (transaction.customerName.value.isNotBlank()) {
                Text(text = customerStringBuilder)
            }

            val totalStringBuilder = buildAnnotatedString {
                withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                    append("${stringResource(Res.string.label_total)}: ")
                }

                withStyle(
                    MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ).toSpanStyle()
                ) {
                    append(
                        transaction
                            .items
                            .sumOf { it.productAmount.value.toBigDecimal() * it.productPrice.value }
                            .toRupiah()
                    )
                }
            }

            Text(text = totalStringBuilder)

            val productsStringBuilder = buildAnnotatedString {
                withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                    append("${stringResource(Res.string.label_product)}: ")
                }

                withStyle(
                    MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ).toSpanStyle()
                ) {
                    val name = transaction.items
                        .groupBy { it.productName }
                        .map {
                            it.key
                        }
                        .first()

                    append(name.value)
                }

                withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                    if ((transaction.items.size > 1)) {
                        val uniqueName = transaction.items
                            .groupBy { it.productName }
                            .map {
                                it.key
                            }

                        append(
                            if(uniqueName.size == 1) {
                                ""
                            } else {
                                stringResource(
                                    Res.string.label_other_products,
                                    uniqueName.size - 1
                                )
                            }
                        )
                    }
                }
            }

            Text(
                productsStringBuilder,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 12.dp)
            )
        }

        if (settings.isTransactionRecapNeeded) {
            OutlinedCard(
                colors = CardDefaults.outlinedCardColors(
                    containerColor = Color.Transparent
                ),
                border = BorderStroke(
                    1.dp,
                    color = when (transaction.isRecapped) {
                        true -> successLight
                        false -> MaterialTheme.colorScheme.error
                    }
                )
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(
                            when (transaction.isRecapped) {
                                true -> Res.string.label_recapped
                                false -> Res.string.label_not_recapped
                            }
                        ),
                        style = MaterialTheme.typography.labelMediumEmphasized.copy(
                            fontWeight = FontWeight.Bold,
                            color = when (transaction.isRecapped) {
                                true -> successLight
                                false -> MaterialTheme.colorScheme.error
                            }
                        ),
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        )
                    )
                }
            }
        } else {
            Icon(
                imageVector = Icons.Default.ArrowRight,
                contentDescription = null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTransactionItem() {
    AppTheme {
        TransactionItem(
            transaction = Transaction(
                id = 0,
                customerName = Name("Syidik"),
                items =
                    SampleData.products.map {
                        TransactionItem(
                            id = it.id,
                            productName = it.name,
                            productPrice = it.price,
                            productUnit = it.unit,
                            productAmount = Amount(1F)
                        )
                    },
                createdAt = kotlin.time.Clock.System.now().toEpochMilliseconds()
            ),
            settings = PosLeSettings(
                isTransactionRecapNeeded = true
            ),
            onClick = {},
            modifier = Modifier
        )
    }
}