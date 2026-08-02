package org.lelestacia.posle.ui.screen.transaction_history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.BurgundyRed
import org.lelestacia.posle.ui.theme.CharcoalBlue
import org.lelestacia.posle.ui.theme.successLight
import org.lelestacia.posle.util.SampleData
import org.lelestacia.posle.util.Util
import org.lelestacia.posle.util.toFormattedDate
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_view_receipt
import posle.shared.generated.resources.label_customer
import posle.shared.generated.resources.label_not_recapped
import posle.shared.generated.resources.label_other_products
import posle.shared.generated.resources.label_product
import posle.shared.generated.resources.label_recapped
import posle.shared.generated.resources.label_total
import posle.shared.generated.resources.title_receipt

@Composable
fun TransactionItem(
    transaction: Transaction,
    settings: PosLeSettings,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 3.dp,
                shape = Util.defaultShape,
                clip = false
            )
            .clip(Util.defaultShape)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(1.dp, CharcoalBlue, Util.defaultShape)
            .clickable(onClick = onClick)
    ) {
        Column {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        stringResource(Res.string.title_receipt),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    if (transaction.customerName.value.isNotBlank()) {
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

                        Text(text = customerStringBuilder)
                    }
                }

                Text(
                    text = transaction.createdAt.toFormattedDate(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            HorizontalDivider()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1F)
                ) {


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
                                    .map { transactionItem ->
                                        transactionItem.quantity.value * transactionItem.sellPrice.value
                                    }
                                    .sumOf { it }
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
                                .map { transactionItem ->
                                    transactionItem.name
                                }
                                .groupBy { it }
                                .map { it.key }
                                .first()

                            append(name.value)
                        }

                        withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                            if ((transaction.items.size > 1)) {
                                val uniqueName = transaction.items
                                    .map { transactionItem ->
                                        transactionItem.name
                                    }
                                    .groupBy { it }
                                    .map { it.key }

                                append(
                                    if (uniqueName.size == 1) {
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                    }
                } else {
                    TextButton(
                        onClick = onClick
                    ) {
                        Text(
                            text = stringResource(resource = Res.string.btn_view_receipt),
                            style = MaterialTheme
                                .typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BurgundyRed
                                )
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewTransactionItem() {
    AppTheme {
        TransactionItem(
            SampleData.sampleTransaction,
            PosLeSettings(
                isTransactionRecapNeeded = false
            ),
            {},
            modifier = Modifier.padding(12.dp)
        )
    }
}