package org.lelestacia.posle.screen.transaction_add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.model.TransactionItem
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.Util
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_save_transaction
import posle.shared.generated.resources.label_customer_name
import java.math.BigDecimal
import org.lelestacia.posle.util.Unit as PosLeUnit

@Composable
fun TransactionAddCartContent(
    cartItems: List<TransactionItem>,
    customerNameState: TextFieldState,
    isCustomerNameNeeded: Boolean,
    onRemoveItem: (TransactionItem) -> Unit,
    onSaveTransaction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1F),
        ) {
            items(items = cartItems) { item ->
                Column(
                    modifier = Modifier.animateItem()
                ) {
                    TransactionAddItemView(
                        transactionItem = item,
                        onRemove = { onRemoveItem(item) }
                    )
                }
            }
        }

        TransactionAddTotalSection(
            totalPrice = cartItems.sumOf {
                val variants =
                    it.variants.sumOf { variant -> variant.priceAdjustment.value }
                (variants * it.productAmount.value.toBigDecimal()) + (it.productSellPrice.value * it.productAmount.value.toBigDecimal())
            }.toRupiah(),
            customerNameState = customerNameState,
            isCustomerNameNeeded = isCustomerNameNeeded,
            onSaveTransaction = onSaveTransaction
        )
    }
}

@Composable
private fun TransactionAddTotalSection(
    totalPrice: String,
    customerNameState: TextFieldState,
    isCustomerNameNeeded: Boolean,
    onSaveTransaction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 12.dp)
            .clip(RoundedCornerShape(25F))
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp))
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "Total Harga:",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Text(
                text = totalPrice,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                )
            )
        }

        if (isCustomerNameNeeded) {
            TextField(
                state = customerNameState,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done
                ),
                label = {
                    Text(
                        text = stringResource(Res.string.label_customer_name),
                        style = MaterialTheme.typography.labelMediumEmphasized.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = Util.defaultTextFieldColor(),
                shape = Util.defaultShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )
        }

        Button(
            onClick = onSaveTransaction,
            shape = RoundedCornerShape(25F),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(stringResource(Res.string.btn_save_transaction))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTransactionAddCartContent() {
    val sampleCartItems = remember {
        listOf(
            TransactionItem(
                id = 1,
                productId = 1,
                productName = Name("Sate Ayam Madura"),
                productBuyPrice = Price(BigDecimal("15000")),
                productSellPrice = Price(BigDecimal("25000")),
                productUnit = PosLeUnit("Porsi"),
                productAmount = Amount(2F),
                productNote = null,
                variants = listOf(
                    Variant(
                        id = 1,
                        name = Name("Pedas"),
                        priceAdjustment = Price(BigDecimal("2000"))
                    )
                )
            ),
            TransactionItem(
                id = 2,
                productId = 2,
                productName = Name("Es Teh Manis"),
                productBuyPrice = Price(BigDecimal("3000")),
                productSellPrice = Price(BigDecimal("5000")),
                productUnit = PosLeUnit("Gelas"),
                productAmount = Amount(3F),
                productNote = "Es batu dikit",
                variants = emptyList()
            ),
            TransactionItem(
                id = 3,
                productId = 3,
                productName = Name("Nasi Putih"),
                productBuyPrice = Price(BigDecimal("4000")),
                productSellPrice = Price(BigDecimal("7000")),
                productUnit = PosLeUnit("Porsi"),
                productAmount = Amount(1F),
                productNote = null,
                variants = emptyList()
            )
        )
    }

    val customerNameState = remember { TextFieldState("Budi") }

    AppTheme {
        TransactionAddCartContent(
            cartItems = sampleCartItems,
            customerNameState = customerNameState,
            isCustomerNameNeeded = true,
            onRemoveItem = {},
            onSaveTransaction = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}
