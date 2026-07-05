package org.lelestacia.posle.domain.component

import com.arkivanov.decompose.value.Value
import org.lelestacia.posle.domain.state_event.SettingEvent
import org.lelestacia.posle.domain.state_event.SettingState

interface SettingComponent {
    val state: Value<SettingState>
    fun onEvent(event: SettingEvent)
}
