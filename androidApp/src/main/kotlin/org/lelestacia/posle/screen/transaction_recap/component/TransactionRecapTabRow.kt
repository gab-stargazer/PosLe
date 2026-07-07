package org.lelestacia.posle.screen.transaction_recap.component

import org.jetbrains.compose.resources.StringResource
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.title_product_outbound
import posle.shared.generated.resources.title_transaction

enum class TransactionRecapTabRow(
    val title: StringResource
) {
    ProductOutbound(Res.string.title_product_outbound),
    Transaction(Res.string.title_transaction)
}