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

class SettingComponentImpl(
    componentContext: ComponentContext,
    private val settingManager: SettingManager
) : ComponentContext by componentContext, SettingComponent {

    private val scope = coroutineScope(Dispatchers.Main.immediate)

    override val state: Value<SettingState>
        field = MutableValue(SettingState())

    init {
        scope.launch {
            settingManager.getSettings()
                .first()
                .run {
                    state.update {
                        it.copy(
                            settings = this,
                            storeNameState = TextFieldState(this.storeName.value)
                        )
                    }
                }
        }
    }

    override fun onEvent(event: SettingEvent) {
        scope.launch {
            when (event) {
                is SettingEvent.OnToggleProductVolatile -> {
                    state.update { currentState ->
                        currentState.copy(
                            settings = currentState.settings.copy(
                                isProductVolatile = event.newValue
                            )
                        )
                    }

                    settingManager.updateProductVolatile(event.newValue)
                }

                is SettingEvent.OnToggleAmountPrecise -> {
                    state.update { currentState ->
                        currentState.copy(
                            settings = currentState.settings.copy(
                                isAmountPrecise = event.newValue
                            )
                        )
                    }

                    settingManager.updateAmountPrecise(event.newValue)
                }

                is SettingEvent.OnToggleCustomerNameNeeded -> {
                    state.update { currentState ->
                        currentState.copy(
                            settings = currentState.settings.copy(
                                isCustomerNameNeeded = event.newValue
                            )
                        )
                    }

                    settingManager.updateCustomerNameNeeded(event.newValue)
                }

                is SettingEvent.OnToggleTransactionRecapNeeded -> {
                    state.update { currentState ->
                        currentState.copy(
                            settings = currentState.settings.copy(
                                isTransactionRecapNeeded = event.newValue
                            )
                        )
                    }

                    settingManager.updateTransactionRecapNeeded(event.newValue)
                }

                is SettingEvent.OnToggleStockTracked -> {
                    state.update { currentState ->
                        currentState.copy(
                            settings = currentState.settings.copy(
                                isProductStockTracked = event.newValue
                            )
                        )
                    }

                    settingManager.updateProductStockTracked(event.newValue)
                }

                SettingEvent.OnStoreNameSaved -> {
                    settingManager.updateStoreName(Name(state.value.storeNameState.text.toString()))
                }
            }
        }
    }
}
