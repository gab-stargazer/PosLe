package org.lelestacia.posle.navigation

import kotlinx.serialization.Serializable
import org.lelestacia.posle.domain.component.TransactionAddComponent
import org.lelestacia.posle.domain.component.DashboardComponent
import org.lelestacia.posle.domain.component.ProductAddEditComponent
import org.lelestacia.posle.domain.component.TransactionListComponent
import org.lelestacia.posle.domain.component.TransactionViewComponent
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.Transaction

@Serializable
sealed interface Config {

    @Serializable
    data object Dashboard : Config

    @Serializable
    data object TransactionAdd : Config

    @Serializable
    data object TransactionList: Config

    @Serializable
    data class TransactionView(
        val transaction: Transaction
    ): Config

    @Serializable
    data class AddEditProduct(
        val addEdit: AddEdit,
        val product: Product?,
    ) : Config
}

sealed class Child {
    data class Dashboard(val component: DashboardComponent) : Child()
    data class TransactionAdd(val component: TransactionAddComponent) : Child()
    data class TransactionList(val component: TransactionListComponent) : Child()
    data class TransactionView(val component: TransactionViewComponent) : Child()
    data class AddProduct(val component: ProductAddEditComponent) : Child()
}

@Serializable
enum class AddEdit {
    Add, Edit
}