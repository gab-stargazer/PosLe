package org.lelestacia.posle.domain.state_event

import androidx.compose.foundation.text.input.TextFieldState
import org.lelestacia.posle.data.PosLeSettings

data class SettingState(
    val settings: PosLeSettings = PosLeSettings(),
    val storeNameState: TextFieldState = TextFieldState()
)

sealed interface SettingEvent {
    data class OnToggleProductVolatile(val newValue: Boolean) : SettingEvent
    data class OnToggleAmountPrecise(val newValue: Boolean) : SettingEvent
    data class OnToggleCustomerNameNeeded(val newValue: Boolean) : SettingEvent
    data class OnToggleTransactionRecapNeeded(val newValue: Boolean) : SettingEvent
    data class OnToggleStockTracked(val newValue: Boolean) : SettingEvent

    data object OnStoreNameSaved : SettingEvent
}
