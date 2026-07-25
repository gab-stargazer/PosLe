package org.lelestacia.posle.util

import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import org.lelestacia.posle.data.entity.TransactionItemType
import org.lelestacia.posle.domain.model.Transaction

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
            [C]================================
        """.trimIndent()

    val sb = StringBuilder(text)
    sb.append("\n")
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
                        [L]Catatan: ${transactionItem.note}
                    """.trimIndent()
                )
                sb.append("\n")
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
                sb.append("[L]Catatan: ${transactionItem.note}\n")
                sb.append("[R]Subtotal: ${subtotal.toRupiah()}\n")
            }
        }
    }

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

    if (transaction.customerName.value.isNotBlank()) {
        sb.append(
            "[L]NAMA PELANGGAN :[R]${transaction.customerName.value}\n"
        )
    }

    sb.append("\n")
    sb.append(
        """
            [C]================================
            [C]Terimakasih telah berbelanja
        """.trimIndent()
    )

    printer.printFormattedText(sb.toString())
}
