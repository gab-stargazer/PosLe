package org.lelestacia.posle.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.skydoves.compose.stability.runtime.TraceRecomposition
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.domain.component.SettingComponent
import org.lelestacia.posle.domain.state_event.SettingEvent
import org.lelestacia.posle.domain.state_event.SettingState
import org.lelestacia.posle.ui.component.BorderedTextField
import org.lelestacia.posle.ui.platform.platformAppVersion
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.ui.theme.BurgundyRed
import org.lelestacia.posle.ui.theme.CharcoalBlue
import org.lelestacia.posle.ui.theme.MintCream
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.app_name
import posle.shared.generated.resources.ic_app_logo
import posle.shared.generated.resources.label_app_version
import posle.shared.generated.resources.label_setting_customer_name
import posle.shared.generated.resources.label_setting_product_volatile
import posle.shared.generated.resources.label_setting_recap_transaction
import posle.shared.generated.resources.label_setting_stock_tracked
import posle.shared.generated.resources.label_store_name
import posle.shared.generated.resources.msg_app_about
import posle.shared.generated.resources.msg_setting_customer_name
import posle.shared.generated.resources.msg_setting_product_volatile
import posle.shared.generated.resources.msg_setting_recap_transaction
import posle.shared.generated.resources.msg_setting_stock_tracked
import posle.shared.generated.resources.title_additional_information
import posle.shared.generated.resources.title_about_app

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

@TraceRecomposition
@Composable
fun SettingUI(
    state: SettingState,
    onEvent: (SettingEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            AboutApp(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(top = 16.dp)
            )

            SettingItem(
                title = stringResource(Res.string.label_setting_product_volatile),
                description = stringResource(Res.string.msg_setting_product_volatile),
                checked = state.settings.isProductVolatile,
                onCheckedChange = { onEvent(SettingEvent.OnToggleProductVolatile(it)) },
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp)
            )

            SettingItem(
                title = stringResource(Res.string.label_setting_customer_name),
                description = stringResource(Res.string.msg_setting_customer_name),
                checked = state.settings.isCustomerNameNeeded,
                onCheckedChange = { onEvent(SettingEvent.OnToggleCustomerNameNeeded(it)) },
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp)
            )

            SettingItem(
                title = stringResource(Res.string.label_setting_recap_transaction),
                description = stringResource(Res.string.msg_setting_recap_transaction),
                checked = state.settings.isTransactionRecapNeeded,
                onCheckedChange = { onEvent(SettingEvent.OnToggleTransactionRecapNeeded(it)) },
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp)
            )

            SettingItem(
                title = stringResource(Res.string.label_setting_stock_tracked),
                description = stringResource(Res.string.msg_setting_stock_tracked),
                checked = state.settings.isProductStockTracked,
                onCheckedChange = { onEvent(SettingEvent.OnToggleStockTracked(it)) },
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp)
            )

            Text(
                stringResource(Res.string.title_additional_information),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp)
            ) {
                BorderedTextField(
                    state = state.storeNameState,
                    label = stringResource(Res.string.label_store_name),
                    labelStyle = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    onKeyboardAction = {
                        focusManager.clearFocus(true)
                    },
                    modifier = Modifier.weight(1F)
                )

                FilledIconButton(
                    onClick = {
                        focusManager.clearFocus(true)
                        onEvent(SettingEvent.OnStoreNameSaved)
                    },
                    shape = RoundedCornerShape(25F),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = BurgundyRed,
                        contentColor = MintCream
                    ),
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1F)
                        .testTag("save_store_name_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutApp(
    modifier: Modifier = Modifier
) {
    val appVersion = platformAppVersion()
    val appName = stringResource(Res.string.app_name)

    Surface(
        color = CharcoalBlue.copy(0.25F),
        shape = RoundedCornerShape(25F),
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(128.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_app_logo),
                    contentDescription = appName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Column(
                modifier = Modifier.weight(1F)
            ) {
                Text(
                    text = appName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = stringResource(Res.string.label_app_version, appVersion),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = stringResource(Res.string.msg_app_about),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
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
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
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
                checkedTrackColor = BurgundyRed,
            ),
            modifier = Modifier.testTag("setting_switch_$title")
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
