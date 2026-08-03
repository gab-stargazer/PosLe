package org.lelestacia.posle.ui.screen.product_add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.OnAddStockEvent
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditState.ProductAddStockDialogState
import org.lelestacia.posle.ui.component.BorderedTextField
import org.lelestacia.posle.ui.theme.AppTheme
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_product_amount
import posle.shared.generated.resources.title_add_stock_movement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductAddStockDialog(
    state: ProductAddStockDialogState,
    onEvent: (OnAddStockEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                stringResource(Res.string.title_add_stock_movement),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            BorderedTextField(
                state = state.amountAdded,
                label = stringResource(Res.string.label_product_amount),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    onEvent(OnAddStockEvent.OnConfirm)
                },
                enabled = state.amountAdded.text.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = stringResource(Res.string.title_add_stock_movement))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProductAddStockDialog() {
    AppTheme {
        ProductAddStockDialog(
            state = ProductAddStockDialogState(),
            onEvent = {

            },
            modifier = Modifier.padding(12.dp)
        )
    }
}