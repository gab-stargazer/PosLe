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
import org.lelestacia.posle.domain.state_event.product_add.ProductAddEditState
import org.lelestacia.posle.navigation.AddEdit.Edit
import org.lelestacia.posle.ui.component.BorderedTextField
import org.lelestacia.posle.ui.theme.successLightHighContrast
import org.lelestacia.posle.util.RupiahVisualTransformation
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_product_buy_price
import posle.shared.generated.resources.title_buy_price_history

@Composable
fun ProductAddEditSectionModalPrice(
    state: ProductAddEditState,
    onModalPriceChange: (String) -> Unit,
    onModalPriceRequestValidation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    Column(modifier = modifier) {
        BorderedTextField(
            value = state.productModalPrice,
            onValueChange = { newModalPrice ->
                onModalPriceChange(newModalPrice)
            },
            label = stringResource(Res.string.label_product_buy_price),
            trailingIcon = {
                AnimatedVisibility(
                    visible = state.isProductModalPriceValidated,
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
            visualTransformation = RupiahVisualTransformation(),
            errorMessage = state.productModalPriceError,
            textStyle = MaterialTheme.typography.bodyMedium,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus(true)
                    onModalPriceRequestValidation.invoke()
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
        )

        if (state.mode == Edit) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .padding(start = 12.dp, top = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SubdirectoryArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )

                Text(
                    text = stringResource(Res.string.title_buy_price_history),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(start = 3.dp)
                )
            }

            ProductAddEditPriceHistory(
                priceHistoryPaging = state.buyPriceHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun PreviewProductAddEditSectionModalPrice() {
    org.lelestacia.posle.ui.theme.AppTheme {
        ProductAddEditSectionModalPrice(
            state = ProductAddEditState(
                mode = Edit,
                productModalPrice = "10000"
            ),
            onModalPriceChange = {},
            onModalPriceRequestValidation = {}
        )
    }
}