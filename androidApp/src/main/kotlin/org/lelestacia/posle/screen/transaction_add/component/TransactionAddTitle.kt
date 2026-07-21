package org.lelestacia.posle.screen.transaction_add.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.BurgundyRed
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.title_bundle

@Composable
fun TransactionAddTitle(
    title: StringResource,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    topEnd = 25F,
                    bottomEnd = 25F,
                )
            )
            .background(BurgundyRed)
            .padding(horizontal = 12.dp)
    ) {
        Text(
            stringResource(title),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            modifier = Modifier.padding(vertical = 6.dp)
        )
    }
}

@Preview
@Composable
private fun PreviewTransactionAddTitle() {
    AppTheme {
        TransactionAddTitle(
            Res.string.title_bundle
        )
    }
}