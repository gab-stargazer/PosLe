package org.lelestacia.posle.ui.screen.transaction_add.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.ui.theme.AppTheme
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.btn_confirm
import posle.shared.generated.resources.msg_error_product_not_found

@Composable
fun TransactionAddProductNotFound(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = Icons.Default.Notifications.name
            )

            Text(
                stringResource(Res.string.msg_error_product_not_found),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(top = 12.dp),
            )

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        stringResource(Res.string.btn_confirm),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewTransactionAddProductNotFound() {
    AppTheme {
        TransactionAddProductNotFound(
            onDismiss = {}
        )
    }
}