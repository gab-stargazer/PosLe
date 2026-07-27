package org.lelestacia.posle.screen.transaction_add.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.model.Bundle
import org.lelestacia.posle.domain.state_event.TransactionAddState
import org.lelestacia.posle.ui.component.BorderedTextField
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.BurgundyRed
import org.lelestacia.posle.ui.theme.CharcoalBlue
import org.lelestacia.posle.ui.theme.MintCream
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Util
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_add_to_cart
import posle.shared.generated.resources.btn_cancel
import posle.shared.generated.resources.label_bundle_name
import posle.shared.generated.resources.label_optional_note
import posle.shared.generated.resources.label_product_amount
import posle.shared.generated.resources.title_add_bundle_to_cart
import kotlin.time.Clock

@Composable
fun TransactionAddBundleDialog(
    state: TransactionAddState.DialogBundleState,
    onQuantityChange: (String) -> Unit,
    onQuantityValidationRequest: () -> Unit,
    onCancel: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                stringResource(Res.string.title_add_bundle_to_cart),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = CharcoalBlue
                )
            )

            BorderedTextField(
                value = state.selectedBundle?.name?.value.orEmpty(),
                onValueChange = {},
                label = stringResource(Res.string.label_bundle_name),
                readOnly = true,
                modifier = Modifier.padding(top = 12.dp)
            )

            BorderedTextField(
                value = state.quantity,
                onValueChange = onQuantityChange,
                label = stringResource(Res.string.label_product_amount),
                errorMessage = state.quantityError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus(true)
                        onQuantityValidationRequest.invoke()
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            BorderedTextField(
                state = state.noteState,
                label = stringResource(Res.string.label_optional_note),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = Icons.Default.EditNote.name
                    )
                },
                lineLimits = TextFieldLineLimits.SingleLine,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                onKeyboardAction = { focusManager.clearFocus(true) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 12.dp)
            ) {
                OutlinedButton(
                    shape = Util.defaultShape,
                    onClick = onCancel,
                    border = BorderStroke(2.dp, BurgundyRed),
                    modifier = Modifier
                        .weight(1F)
                ) {
                    Text(
                        stringResource(Res.string.btn_cancel),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BurgundyRed
                        )
                    )
                }

                Button(
                    shape = Util.defaultShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BurgundyRed,
                    ),
                    onClick = onAddToCart,
                    modifier = Modifier.weight(1F)
                ) {
                    Text(
                        stringResource(Res.string.btn_add_to_cart),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MintCream
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTransactionAddBundleDialog() {
    AppTheme {
        val state by remember {
            mutableStateOf(
                TransactionAddState.DialogBundleState(
                    selectedBundle = Bundle(
                        id = 0,
                        name = Name("Paket Kombo"),
                        imageUri = null,
                        bundleProducts = emptyList(),
                        createdAt = Clock.System.now().toEpochMilliseconds()
                    ),
                    quantity = "6",
                    noteState = TextFieldState("Lorem Ipsum")
                )
            )
        }
        TransactionAddBundleDialog(
            state = state,
            onQuantityChange = {},
            onQuantityValidationRequest = {},
            onCancel = {},
            onAddToCart = {}
        )
    }
}