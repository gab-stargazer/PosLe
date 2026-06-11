package org.lelestacia.posle.domain.component

import com.arkivanov.decompose.ComponentContext

class TransactionComponent(
    componentContext: ComponentContext,
    onNavigateTo: () -> Unit
) : ComponentContext by componentContext {


}

