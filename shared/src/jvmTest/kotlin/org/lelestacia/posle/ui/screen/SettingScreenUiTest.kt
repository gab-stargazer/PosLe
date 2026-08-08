package org.lelestacia.posle.ui.screen

import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arkivanov.decompose.value.MutableValue
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.lelestacia.posle.data.PosLeSettings
import org.lelestacia.posle.data.SettingManager
import org.lelestacia.posle.domain.component.SettingComponent
import org.lelestacia.posle.domain.component.SettingComponentImpl
import org.lelestacia.posle.domain.component.product_list.ProductListComponentEvent
import org.lelestacia.posle.domain.component.product_list.ProductListComponentImpl
import org.lelestacia.posle.domain.state_event.SettingEvent
import org.lelestacia.posle.domain.state_event.SettingState
import org.lelestacia.posle.ui.theme.AppTheme
import org.lelestacia.posle.util.Name
import kotlin.test.assertTrue

class FakeSettingComponent(
    initial: SettingState = SettingState()
) : SettingComponent {
    val events = mutableListOf<SettingEvent>()
    override val state = MutableValue(initial)
    override fun onEvent(event: SettingEvent) {
        events += event
    }
}

class SettingScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun toggle_product_volatile_fires_event() {
        val component = FakeSettingComponent()
        composeRule.setContent {
            AppTheme {
                SettingScreen(component = component)
            }
        }

        composeRule
            .onNodeWithTag("setting_switch_Harga Produk Volatil")
            .performClick()

        composeRule.waitForIdle()

        assertTrue(component.events.any { it is SettingEvent.OnToggleProductVolatile && it.newValue })
    }

    @Test
    fun save_store_name_calls_setting_manager() {
        val manager = mockk<SettingManager>(relaxed = true)
        every { manager.getSettings() } returns MutableStateFlow(PosLeSettings())
        val component = SettingComponentImpl(
            componentContext = testComponentContext(),
            settingManager = manager
        )
        composeRule.setContent {
            AppTheme {
                SettingScreen(component = component)
            }
        }

        composeRule.waitForIdle()

        //  The store-name field is the only text field; type into it
        composeRule.onNode(hasSetTextAction()).performTextInput("Toko Baru")
        composeRule.waitForIdle()

        //  The save button is an icon button (Icons.Default.Save)
        composeRule.onNodeWithTag("save_store_name_button").performClick()
        composeRule.waitForIdle()

        coVerify { manager.updateStoreName(Name("Toko Baru")) }
    }

    @Test
    fun export_product_fires_export_event() {
        //  Export IO moved to the WorkManager worker — the component only
        //  forwards the event to the platform-level callback.
        var exportTriggered = false
        val onExportProducts: () -> Unit = { exportTriggered = true }
        val onImportProducts: (String) -> Unit = {}

        val component = ProductListComponentImpl(
            componentContext = testComponentContext(),
            settingManager = mockk(relaxed = true),
            productRepository = mockk(relaxed = true),
            categoryRepository = mockk(relaxed = true),
            bundleRepository = mockk(relaxed = true),
            onExportProducts = onExportProducts,
            onImportProducts = onImportProducts,
            onNavigate = {}
        )

        component.onEvent(ProductListComponentEvent.OnExportProducts)

        assertTrue(exportTriggered)
    }
}
