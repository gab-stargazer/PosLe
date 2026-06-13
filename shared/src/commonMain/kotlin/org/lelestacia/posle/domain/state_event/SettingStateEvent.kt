package org.lelestacia.posle.domain.state_event

import org.lelestacia.posle.data.PosLeSettings

data class SettingState(
    val settings: PosLeSettings = PosLeSettings()
)

sealed interface SettingEvent {
    data class OnToggleProductVolatile(val newValue: Boolean) : SettingEvent
    data class OnToggleAmountPrecise(val newValue: Boolean) : SettingEvent
    data class OnToggleCustomerNameNeeded(val newValue: Boolean) : SettingEvent
    data class OnToggleTransactionRecapNeeded(val newValue: Boolean) : SettingEvent
}
