package org.lelestacia.posle.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import org.lelestacia.posle.ui.theme.CharcoalBlue
import org.lelestacia.posle.util.Util

@Composable
fun BorderedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    maxLines: Int = 1,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    errorMessage: String? = null,
    cornerRadius: Float = 25F,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    labelStyle: TextStyle = MaterialTheme.typography.labelMedium,
    colors: TextFieldColors = Util.defaultTransparentTextFieldColor(),
) {
    val errorShakeOffset = remember { Animatable(0f) }
    LaunchedEffect(errorMessage) {
        if (errorMessage!= null) {
            errorShakeOffset.animateTo(2f, tween(50))
            errorShakeOffset.animateTo(-2f, tween(50))
            errorShakeOffset.animateTo(0f, tween(50))
        }
    }

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(cornerRadius))
                .border(
                    width = 1.dp,
                    color = CharcoalBlue,
                    shape = RoundedCornerShape(cornerRadius)
                )
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                label = {
                    Text(
                        text = label,
                        style = labelStyle
                    )
                },
                visualTransformation = visualTransformation,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                readOnly = readOnly,
                maxLines = maxLines,
                textStyle = textStyle,
                colors = colors,
                modifier = Modifier.fillMaxWidth().offset {
                    IntOffset(
                        x = errorShakeOffset.value.toInt(),
                        y = 0
                    )
                }
            )
        }

        AnimatedVisibility(
            visible = errorMessage != null,
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

                errorMessage?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.error)
                    )
                }
            }
        }
    }
}

@Composable
fun BorderedTextField(
    state: TextFieldState,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
    borderColor: Color = CharcoalBlue,
    borderWidth: Dp = 1.dp,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    labelStyle: TextStyle = MaterialTheme.typography.labelMedium,
    colors: TextFieldColors = Util.defaultTransparentTextFieldColor(),
) {
    Box(
        modifier = modifier
            .clip(Util.defaultShape)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = Util.defaultShape
            )
    ) {
        TextField(
            state = state,
            label = {
                Text(
                    text = label,
                    style = labelStyle
                )
            },
            leadingIcon = leadingIcon,
            lineLimits = lineLimits,
            keyboardOptions = keyboardOptions,
            onKeyboardAction = onKeyboardAction,
            textStyle = textStyle,
            colors = colors,
            modifier = Modifier.fillMaxWidth()
        )
    }
}