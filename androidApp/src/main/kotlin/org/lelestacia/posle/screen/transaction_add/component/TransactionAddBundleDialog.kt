package org.lelestacia.posle.screen.transaction_add.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.ui.components.Icon
import com.composables.ui.components.Text
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.domain.model.Bundle
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
    bundle: Bundle?,
    quantity: String,
    quantityError: String?,
    onQuantityChange: (String) -> Unit,
    noteState: TextFieldState,
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

            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(25F))
                    .border(
                        width = 2.dp,
                        color = CharcoalBlue,
                        shape = RoundedCornerShape(25F)
                    )
            ) {
                TextField(
                    value = bundle?.name?.value.orEmpty(),
                    onValueChange = {},
                    label = {
                        Text(
                            stringResource(Res.string.label_bundle_name),
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus(true)
                        }
                    ),
                    readOnly = true,
                    maxLines = 1,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    colors = Util.defaultTransparentTextFieldColor(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(25F))
                        .border(
                            width = 2.dp,
                            color = CharcoalBlue,
                            shape = RoundedCornerShape(25F)
                        )
                ) {
                    TextField(
                        value = quantity,
                        onValueChange = onQuantityChange,
                        label = {
                            Text(
                                stringResource(Res.string.label_product_amount),
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus(true)
                            }
                        ),
                        maxLines = 1,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        colors = Util.defaultTransparentTextFieldColor(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                AnimatedVisibility(
                    visible = quantityError != null,
                    enter = expandVertically(),
                    exit = shrinkVertically(),
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = Icons.Default.ErrorOutline.name,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )

                        quantityError?.let {
                            Text(
                                quantityError,
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.error)
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(25F))
                    .border(
                        width = 2.dp,
                        color = CharcoalBlue,
                        shape = RoundedCornerShape(25F)
                    )
            ) {
                TextField(
                    state = noteState,
                    label = {
                        Text(
                            stringResource(Res.string.label_optional_note),
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    lineLimits = TextFieldLineLimits.SingleLine,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    onKeyboardAction = {
                        focusManager.clearFocus(true)
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    colors = Util.defaultTransparentTextFieldColor(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 6.dp)
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
        TransactionAddBundleDialog(
            bundle = Bundle(
                id = 0,
                name = Name("Paket Combo"),
                imageUri = null,
                bundleProducts = emptyList(),
                createdAt = Clock.System.now().toEpochMilliseconds()
            ),
            quantity = "6",
            onQuantityChange = {},
            quantityError = null,
            noteState = rememberTextFieldState(),
            onCancel = {},
            onAddToCart = {}
        )
    }
}