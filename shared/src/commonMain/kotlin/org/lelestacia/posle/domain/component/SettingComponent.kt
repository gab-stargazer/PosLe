package org.lelestacia.posle.domain.component

import androidx.compose.foundation.text.input.TextFieldState
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.lelestacia.posle.data.SettingManager
import org.lelestacia.posle.domain.state_event.SettingEvent
import org.lelestacia.posle.domain.state_event.SettingState
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.coroutineScope

class SettingComponent(
    componentContext: ComponentContext,
    private val settingManager: SettingManager
) : ComponentContext by componentContext {

    private val scope = coroutineScope(Dispatchers.Main.immediate)

    val state: Value<SettingState> = MutableValue(SettingState())

    init {
        scope.launch {
            settingManager.readSettings()
                .first()
                .run {
                    (state as MutableValue).update {
                        it.copy(
                            settings = this,
                            storeNameState = TextFieldState(this.storeName.value)
                        )
                    }
                }
        }
    }

    fun onEvent(event: SettingEvent) {
        scope.launch {
            when (event) {
                is SettingEvent.OnToggleProductVolatile -> {
                    settingManager.updateProductVolatile(event.newValue)
                }

                is SettingEvent.OnToggleAmountPrecise -> {
                    settingManager.updateAmountPrecise(event.newValue)
                }

                is SettingEvent.OnToggleCustomerNameNeeded -> {
                    settingManager.updateCustomerNameNeeded(event.newValue)
                }

                is SettingEvent.OnToggleTransactionRecapNeeded -> {
                    settingManager.updateTransactionRecapNeeded(event.newValue)
                }

                SettingEvent.OnStoreNameSaved -> {
                    settingManager.updateStoreName(Name(state.value.storeNameState.text.toString()))
                }
            }
        }
    }
}
