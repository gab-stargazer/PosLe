package org.lelestacia.posle.domain.component

import com.arkivanov.decompose.ComponentContext
import org.lelestacia.posle.navigation.Config

class TransactionListComponent(
    componentContext: ComponentContext,
    val onNavigateTo: (Config) -> Unit
) : ComponentContext by componentContext {


}

