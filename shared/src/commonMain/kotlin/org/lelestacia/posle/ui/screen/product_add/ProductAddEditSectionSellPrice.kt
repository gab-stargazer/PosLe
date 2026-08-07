package org.lelestacia.posle.ui.screen.product_add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SubdirectoryArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.OnProductSellPriceChange
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditEvent.OnProductSellPriceRequestValidation
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditState
import org.lelestacia.posle.navigation.AddEdit.Edit
import org.lelestacia.posle.ui.component.BorderedTextField
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.successLightHighContrast
import org.lelestacia.posle.util.RupiahVisualTransformation
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_product_sell_price
import posle.shared.generated.resources.title_sell_price_history

@Composable
fun ProductAddEditSectionSellPrice(
    state: ProductAddEditState,
    onEvent: (ProductAddEditEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    Column(modifier = modifier) {
        BorderedTextField(
            value = state.productSellPrice,
            onValueChange = { newSellPrice ->
                onEvent(OnProductSellPriceChange(newSellPrice))
            },
            label = stringResource(Res.string.label_product_sell_price),
            visualTransformation = RupiahVisualTransformation(),
            textStyle = MaterialTheme.typography.bodyMedium,
            trailingIcon = {
                AnimatedVisibility(
                    visible = state.isProductSellPriceValidated,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = successLightHighContrast
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus(true)
                    onEvent(OnProductSellPriceRequestValidation)
                }
            ),
            errorMessage = state.productSellPriceError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .padding(horizontal = 12.dp)
        )

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

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun PreviewProductAddEditSectionSellPrice() {
    AppTheme {
        ProductAddEditSectionSellPrice(
            state = ProductAddEditState(
                mode = Edit,
                productSellPrice = "15000"
            ),
            onEvent = {}
        )
    }
}