package org.lelestacia.posle.navigation

import kotlinx.serialization.Serializable
import org.lelestacia.posle.domain.component.DashboardComponentImpl
import org.lelestacia.posle.domain.component.TransactionListComponent
import org.lelestacia.posle.domain.component.TransactionProductConfigComponentImpl
import org.lelestacia.posle.domain.component.TransactionViewComponent
import org.lelestacia.posle.domain.component.product_add_edit.ProductAddEditComponent
import org.lelestacia.posle.domain.component.product_add_edit.ProductAddVariantsViewComponent
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.model.Variant

@Serializable
sealed interface Config {

    @Serializable
    data object Dashboard : Config

    @Serializable
    data object TransactionList: Config

    @Serializable
    data class TransactionView(
        val transaction: Transaction
    ): Config

    @Serializable
    data class TransactionProduct(
        val product: Product
    ) : Config

    @Serializable
    data class ProductAddEdit(
        val addEdit: AddEdit,
        val product: Product?,
    ) : Config

    @Serializable
    data class VariantView(
        val selectedVariants: List<Variant> = emptyList()
    ) : Config

}

sealed class Child {
    data class Dashboard(val component: DashboardComponentImpl) : Child()
    data class TransactionList(val component: TransactionListComponent) : Child()
    data class TransactionView(val component: TransactionViewComponent) : Child()
    data class TransactionProductConfig(val component: TransactionProductConfigComponentImpl) : Child()
    data class ProductAdd(val component: ProductAddEditComponent) : Child()
    data class VariantView(val component: ProductAddVariantsViewComponent): Child()
}

@Serializable
enum class AddEdit {
    Add, Edit
}