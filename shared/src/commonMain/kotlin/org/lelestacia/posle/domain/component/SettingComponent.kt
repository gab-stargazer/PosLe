package org.lelestacia.posle.domain.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.instancekeeper.getOrCreateSimple
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.lelestacia.posle.data.SettingManager
import org.lelestacia.posle.domain.state_event.SettingEvent
import org.lelestacia.posle.domain.state_event.SettingState

class SettingComponent(
    componentContext: ComponentContext,
    private val settingManager: SettingManager
) : ComponentContext by componentContext {

    private val scope = instanceKeeper.getOrCreateSimple { CoroutineScope(Dispatchers.Main.immediate) }

    val state: Value<SettingState> = instanceKeeper.getOrCreateSimple { MutableValue(SettingState()) }

    init {
        settingManager.readSettings()
            .onEach { settings ->
                (state as MutableValue).update { it.copy(settings = settings) }
            }
            .launchIn(scope)
    }

    fun onEvent(event: SettingEvent) {
        scope.launch {
            when (event) {
                is SettingEvent.OnToggleProductVolatile -> settingManager.updateProductVolatile(event.newValue)
                is SettingEvent.OnToggleAmountPrecise -> settingManager.updateAmountPrecise(event.newValue)
                is SettingEvent.OnToggleCustomerNameNeeded -> settingManager.updateCustomerNameNeeded(event.newValue)
                is SettingEvent.OnToggleTransactionRecapNeeded -> settingManager.updateTransactionRecapNeeded(event.newValue)
            }
        }
    }
}
