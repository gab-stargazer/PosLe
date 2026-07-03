package org.lelestacia.posle.screen.product_add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SubdirectoryArrowRight
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.coerceIn
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.OnSellPriceTheSameAsBuyPriceCheckedChange
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditState
import org.lelestacia.posle.navigation.AddEdit.Edit
import org.lelestacia.posle.util.RupiahOutputTransformation
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_product_sell_price
import posle.shared.generated.resources.label_sell_price_equal_to_buy_price
import posle.shared.generated.resources.title_sell_price_history

@Composable
fun ProductAddEditSectionSellPrice(
    state: ProductAddEditState,
    onEvent: (ProductAddEditEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    Column(modifier = modifier) {
        OutlinedTextField(
            state = state.sellPriceState,
            label = {
                Text(
                    text = stringResource(Res.string.label_product_sell_price),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            outputTransformation = RupiahOutputTransformation(),
            textStyle = MaterialTheme.typography.bodyMedium,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            onKeyboardAction = {
                focusManager.clearFocus(true)
            },
            enabled = !state.isSellPriceAndBuyPriceTheSame,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .padding(horizontal = 12.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = state.isSellPriceAndBuyPriceTheSame,
                onCheckedChange = { newState ->
                    onEvent(OnSellPriceTheSameAsBuyPriceCheckedChange(newState))
                    state.sellPriceState.edit {
                        val oldSelection = selection
                        replace(0, length, state.buyPriceState.text.toString())
                        selection =
                            oldSelection.coerceIn(0, state.buyPriceState.text.toString().length)
                    }
                }
            )

            Text(
                stringResource(Res.string.label_sell_price_equal_to_buy_price),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }

        if (state.mode == Edit) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .padding(start = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SubdirectoryArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )

                Text(
                    text = stringResource(Res.string.title_sell_price_history),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(start = 3.dp)
                )
            }

            ProductAddEditPriceHistory(
                priceHistoryPaging = state.sellPriceHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )
        }
    }
}