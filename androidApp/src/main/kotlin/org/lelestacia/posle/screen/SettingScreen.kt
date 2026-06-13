package org.lelestacia.posle.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.domain.component.SettingComponent
import org.lelestacia.posle.domain.state_event.SettingEvent
import org.lelestacia.posle.domain.state_event.SettingState
import org.lelestacia.posle.ui.theme.AppTheme
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.label_setting_amount_precise
import posle.shared.generated.resources.label_setting_customer_name
import posle.shared.generated.resources.label_setting_product_volatile
import posle.shared.generated.resources.label_setting_recap_transaction
import posle.shared.generated.resources.msg_setting_amount_precise
import posle.shared.generated.resources.msg_setting_customer_name
import posle.shared.generated.resources.msg_setting_product_volatile
import posle.shared.generated.resources.msg_setting_recap_transaction

@Composable
fun SettingScreen(
    component: SettingComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.subscribeAsState()
    SettingUI(
        state = state,
        onEvent = component::onEvent,
        modifier = modifier
    )
}

@Composable
fun SettingUI(
    state: SettingState,
    onEvent: (SettingEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SettingItem(
                title = stringResource(Res.string.label_setting_product_volatile),
                description = stringResource(Res.string.msg_setting_product_volatile),
                checked = state.settings.isProductVolatile,
                onCheckedChange = { onEvent(SettingEvent.OnToggleProductVolatile(it)) }
            )

            SettingItem(
                title = stringResource(Res.string.label_setting_amount_precise),
                description = stringResource(Res.string.msg_setting_amount_precise),
                checked = state.settings.isAmountPrecise,
                onCheckedChange = { onEvent(SettingEvent.OnToggleAmountPrecise(it)) }
            )

            SettingItem(
                title = stringResource(Res.string.label_setting_customer_name),
                description = stringResource(Res.string.msg_setting_customer_name),
                checked = state.settings.isCustomerNameNeeded,
                onCheckedChange = { onEvent(SettingEvent.OnToggleCustomerNameNeeded(it)) }
            )

            SettingItem(
                title = stringResource(Res.string.label_setting_recap_transaction),
                description = stringResource(Res.string.msg_setting_recap_transaction),
                checked = state.settings.isTransactionRecapNeeded,
                onCheckedChange = { onEvent(SettingEvent.OnToggleTransactionRecapNeeded(it)) }
            )
        }
    }
}

@Composable
private fun SettingItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSettingUI() {
    AppTheme {
        SettingUI(
            state = SettingState(
                settings = PosLeSettings(
                    isProductVolatile = true,
                    isAmountPrecise = false,
                    isCustomerNameNeeded = true
                )
            ),
            onEvent = {}
        )
    }
}
