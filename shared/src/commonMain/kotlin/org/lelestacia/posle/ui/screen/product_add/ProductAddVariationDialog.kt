package org.lelestacia.posle.ui.screen.product_add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.state_event.product_add.ProductVariantViewState.VariantAddEditState
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.RupiahOutputTransformation
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_add_variant
import posle.shared.generated.resources.label_variation_and_addition
import posle.shared.generated.resources.label_variation_name
import posle.shared.generated.resources.label_variation_price_adjustment

@Composable
fun ProductAddVariantDialog(
    state: VariantAddEditState,
    onSaveClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    ElevatedCard(
        shape = RoundedCornerShape(25F),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(all = 12.dp)
        ) {
            Text(
                stringResource(Res.string.label_variation_and_addition),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            TextField(
                state = state.variantNameState,
                label = {
                    Text(
                        stringResource(Res.string.label_variation_name),
                        style = MaterialTheme.typography.labelMediumEmphasized.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                shape = RoundedCornerShape(25F),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                onKeyboardAction = {
                    focusManager.clearFocus(true)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            TextField(
                state = state.variantPriceState,
                label = {
                    Text(
                        stringResource(Res.string.label_variation_price_adjustment),
                        style = MaterialTheme.typography.labelMediumEmphasized.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                shape = RoundedCornerShape(25F),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                onKeyboardAction = {
                    focusManager.clearFocus(true)
                },
                outputTransformation = RupiahOutputTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            Button(
                onClick = onSaveClicked,
                shape = RoundedCornerShape(25F),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Text(
                    stringResource(Res.string.btn_add_variant),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewProductAddVariantDialog() {
    AppTheme {
        ProductAddVariantDialog(
            state = VariantAddEditState(
                variantNameState = TextFieldState("Karung"),
                variantPriceState = TextFieldState("0")
            ),
            onSaveClicked = {}
        )
    }
}