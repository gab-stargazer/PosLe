package org.lelestacia.posle.domain.component

import com.arkivanov.decompose.value.Value
import org.lelestacia.posle.domain.state_event.SettingEvent
import org.lelestacia.posle.domain.state_event.SettingState

/**
 * Component interface for the App Settings screen.
 *
 * Provides control over application behavior such as pricing models,
 * stock tracking preferences, and store identification.
 */
interface SettingComponent {
    /**
     * Observable UI state representing the current configured settings and form state.
     */
    val state: Value<SettingState>

    /**
     * Handles setting changes initiated by the user.
     *
     * @param event The setting update event.
     */
    fun onEvent(event: SettingEvent)
}
