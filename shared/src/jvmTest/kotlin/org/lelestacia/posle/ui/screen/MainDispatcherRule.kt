package org.lelestacia.posle.ui.screen

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Installs a test [Dispatchers.Main] backed by [UnconfinedTestDispatcher] for the
 * duration of a test, so components that launch on `Dispatchers.Main` (e.g. real
 * `*ComponentImpl` instances created inside `composeRule.setContent`) work in the
 * JVM test environment, which has no platform main dispatcher.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
