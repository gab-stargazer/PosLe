package org.lelestacia.posle.screen.transaction_add

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.util.Util
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_search_product

@Composable
fun TransactionAddSearchBar(
    state: TextFieldState,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    TextField(
        state = state,
        placeholder = {
            Text(
                text = stringResource(Res.string.label_search_product),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        colors = Util.defaultTextFieldColor(),
        shape = Util.defaultShape,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Done
        ),
        onKeyboardAction = {
            focusManager.clearFocus(true)
        },
        modifier = modifier
    )
}
