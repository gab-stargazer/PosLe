package org.lelestacia.posle.navigation

import kotlinx.serialization.Serializable
import org.lelestacia.posle.domain.component.TransactionListComponent
import org.lelestacia.posle.domain.component.TransactionProductConfigComponent
import org.lelestacia.posle.domain.component.TransactionSearchComponent
import org.lelestacia.posle.domain.component.TransactionViewComponent
import org.lelestacia.posle.domain.component.bundle_add_edit.BundleAddEditComponent
import org.lelestacia.posle.domain.component.dashboard.DashboardComponent
import org.lelestacia.posle.domain.component.product_add_edit.ProductAddEditComponent
import org.lelestacia.posle.domain.component.product_add_edit.ProductAddVariantsViewComponent
import org.lelestacia.posle.domain.component.qr_scanner.QrScannerComponent
import org.lelestacia.posle.domain.component.transaction_recap_product_view.TransactionRecapProductViewComponent
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.model.Variant
import org.lelestacia.posle.domain.model.TransactionProduct as TransactionProductModel

@Serializable
sealed interface Config {

    @Serializable
    data object Dashboard : Config

    @Serializable
    data object TransactionList : Config

    @Serializable
    data object TransactionSearch : Config

    @Serializable
    data class TransactionView(
        val transaction: Transaction
    ) : Config

    @Serializable
    data class TransactionRecapProductItem(
        val type: org.lelestacia.posle.data.entity.TransactionItemType,
        val product: TransactionProductModel
    )

    @Serializable
    data class TransactionRecapProductView(
        val items: List<TransactionRecapProductItem>
    ) : Config

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
    data class BundleAddEdit(
        val addEdit: AddEdit,
        val bundle: org.lelestacia.posle.domain.model.Bundle? = null
    ) : Config

    @Serializable
    data object QrScanner : Config

    @Serializable
    data class VariantView(
        val selectedVariants: List<Variant> = emptyList()
    ) : Config

}

sealed class Child {
    data class Dashboard(val component: DashboardComponent) : Child()
    data class TransactionList(val component: TransactionListComponent) : Child()
    data class TransactionSearch(val component: TransactionSearchComponent) : Child()
    data class TransactionView(val component: TransactionViewComponent) : Child()
    data class TransactionRecapProductView(val component: TransactionRecapProductViewComponent) : Child()
    data class TransactionProductConfig(val component: TransactionProductConfigComponent) : Child()
    data class BundleAddEdit(val component: BundleAddEditComponent) : Child()
    data class ProductAddEdit(val component: ProductAddEditComponent) : Child()
    data class QrScanner(val component: QrScannerComponent) : Child()
    data class VariantView(val component: ProductAddVariantsViewComponent) : Child()
}

@Serializable
enum class AddEdit {
    Add, Edit
}