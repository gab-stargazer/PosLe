package org.lelestacia.posle.navigation

import kotlinx.serialization.Serializable
import org.lelestacia.posle.domain.component.AddTransactionComponent
import org.lelestacia.posle.domain.component.DashboardComponent
import org.lelestacia.posle.domain.component.ProductAddEditComponent
import org.lelestacia.posle.domain.model.Product

@Serializable
sealed interface Config {

    @Serializable
    data object Dashboard : Config

    @Serializable
    data object AddTransaction : Config

    @Serializable
    data class AddEditProduct(
        val addEdit: AddEdit,
        val product: Product?,
    ) : Config
}

sealed class Child {
    data class Dashboard(val component: DashboardComponent) : Child()
    data class AddEditTransaction(val component: AddTransactionComponent) : Child()

    data class AddProduct(val component: ProductAddEditComponent) : Child()
}

@Serializable
enum class AddEdit {
    Add, Edit
}