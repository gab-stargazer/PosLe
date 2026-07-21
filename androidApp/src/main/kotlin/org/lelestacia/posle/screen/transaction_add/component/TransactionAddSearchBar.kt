package org.lelestacia.posle.screen.transaction_add.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.BurgundyRed
import org.lelestacia.posle.ui.theme.CharcoalBlue
import org.lelestacia.posle.util.Util
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_search_product

@Composable
fun TransactionAddSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .border(2.dp, CharcoalBlue, Util.defaultShape)
    ) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = {
                Text(
                    text = stringResource(Res.string.label_search_product),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = CharcoalBlue
                    )
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = BurgundyRed
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            colors = Util.defaultTransparentTextFieldColor(),
            shape = Util.defaultShape,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus(true)
                }
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSearchBar() {
    AppTheme {
        Surface {
            TransactionAddSearchBar(
                searchQuery = "Hello",
                onSearchQueryChange = {},
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}