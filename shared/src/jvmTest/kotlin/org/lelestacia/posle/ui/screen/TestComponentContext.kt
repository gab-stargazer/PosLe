package org.lelestacia.posle.ui.screen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry

/**
 * Minimal [ComponentContext] for tests — wraps [DefaultComponentContext] with a
 * [LifecycleRegistry], mirroring what Decompose would create for a real component.
 */
fun testComponentContext(): ComponentContext =
    DefaultComponentContext(LifecycleRegistry())
