package org.lelestacia.posle.util

import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import org.lelestacia.posle.data.entity.TransactionItemType
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.state_event.TransactionRecapState
import kotlin.time.Clock

fun printRecap(state: TransactionRecapState) {
    val printer = EscPosPrinter(
        BluetoothPrintersConnections.selectFirstPaired(),
        203,
        58f,
        32
    )

    val storeName = state.settings.storeName.value
        .ifBlank {
            "Rekap PosLe"
        }

    val dateRange = if (state.isSameDay) {
        state.startDate.toFormattedDate()
    } else {
        "${state.startDate.toFormattedDate()} - ${state.finishDate.toFormattedDate()}"
    }

    val sb = StringBuilder()
    sb.append("[C]<u><font size='big'>$storeName</font></u>\n")
    sb.append("[C]<font size='small'>REKAP TRANSAKSI</font>\n")
    sb.append("[C]<font size='small'>$dateRange</font>\n")
    sb.append("[C]================================\n")

    sb.append("[L]Total Transaksi:[R]${state.transactionHistory.size}\n")
    sb.append("[L]Total Keuntungan:[R]${state.totalProfit.toRupiah()}\n")
    sb.append("[C]--------------------------------\n")
    sb.append("[C]RINGKASAN PRODUK TERJUAL\n")
    sb.append("[C]--------------------------------\n")

    state.listOfProducts.forEach { products ->
        val first = products.first()
        val totalQty = products.sumOf { it.product.quantity.value }
        val totalProfit = products.sumOf {
            (it.product.sellPrice.value.subtract(it.product.buyPrice.value)).multiply(it.product.quantity.value)
        }

        sb.append("[L]<b>${first.product.productName.value}</b>\n")
        sb.append("[L] Terjual: ${totalQty.toDisplayText()} ${first.product.unit.value}\n")
        sb.append("[L] Profit: [R]${totalProfit.toRupiah()}\n")
    }

    sb.append("[C]================================\n")
    sb.append(
        "[C]Dicetak pada: ${
            Clock.System.now().toEpochMilliseconds().toFormattedDateTime()
        }\n"
    )

    printer.printFormattedText(sb.toString())
}

fun printTransaction(transaction: Transaction, storeName: Name) {
    val printer = EscPosPrinter(
        BluetoothPrintersConnections.selectFirstPaired(),
        203,
        58f,
        32
    )

    val storeName = storeName.value
        .ifBlank {
            "Transaksi PosLe"
        }

    val text =
        """
            [C]<u><font size='big'>$storeName</font></u>
            [L]
            [L]
            [L]<font size='small'>${transaction.createdAt.toFormattedDateTime()}</font>
        """.trimIndent()

    val sb = StringBuilder(text)
    sb.append("\n")
    if (transaction.customerName.value.isNotBlank()) {
        sb.append(
            "[L]NAMA PELANGGAN :${transaction.customerName.value}\n"
        )
    }
    sb.append("\n")
    sb.append("[C]================================")
    transaction.items.forEach { transactionItem ->
        when (transactionItem.type) {
            TransactionItemType.Product -> {
                val productName = transactionItem.products.first().productName.value
                val productSellPrice = transactionItem.products.first().sellPrice.value
                val productQuantity = transactionItem.products.first().quantity
                val productUnit = transactionItem.products.first().unit.value
                val subtotal = productQuantity.value * productSellPrice

                sb.append(
                    """
                        [L]$productName[R]${productSellPrice.toRupiah()}
                        [L] ${productQuantity.value.toDisplayText()} $productUnit
                        [R]Subtotal: ${subtotal.toRupiah()}
                    """.trimIndent()
                )
                sb.append("\n")
                if (transactionItem.note.orEmpty().isNotBlank()) {
                    sb.append("[L]Catatan: ${transactionItem.note}\n")
                }
            }

            TransactionItemType.Bundle -> {
                val bundleName = transactionItem.name.value
                val bundleSellPrice = transactionItem.sellPrice.value
                val bundleQuantity = transactionItem.quantity.value
                val subtotal = bundleQuantity * bundleSellPrice
                sb.append(
                    """
                        [L]$bundleName x${bundleQuantity.toDisplayText()}[R]${bundleSellPrice.toRupiah()}${"\n"}
                    """.trimIndent()
                )
                transactionItem.products.forEach { product ->
                    sb.append("[L]${product.productName.value} ${product.quantity.value.toDisplayText()}${product.unit.value}\n")
                }
                sb.append("[R]Subtotal: ${subtotal.toRupiah()}\n")
                if(transactionItem.note.orEmpty().isNotBlank()) {
                    sb.append("[L]Catatan: ${transactionItem.note}\n")
                }
            }
        }
    }

    sb.append("\n")
    val totalPrice = transaction
        .items
        .map { cartItems ->
            val subtotal = cartItems
                .products
                .map {
                    it.sellPrice.value * it.quantity.value
                }
                .sumOf { it }

            cartItems.quantity.value * subtotal
        }
        .sumOf { it }

    sb.append(
        """
            [C]================================
            [R]TOTAL HARGA :
            [R]${totalPrice.toRupiah()}
        """.trimIndent()
    )

    sb.append("\n")
    sb.append(
        """
            [C]================================
            [C]Terimakasih telah berbelanja
        """.trimIndent()
    )

    printer.printFormattedText(sb.toString())
}
