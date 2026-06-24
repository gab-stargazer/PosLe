package org.lelestacia.posle.util

import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import org.lelestacia.posle.domain.model.Transaction

fun printTransaction(transaction: Transaction, storeName: Name) {
    val printer = EscPosPrinter(
        BluetoothPrintersConnections.selectFirstPaired(),
        203,
        58f,
        32
    )

    val storeName = if(storeName.value.isNotBlank()) {
        storeName.value
    } else {
        "Transaksi PosLe"
    }

    val text =
                "[C]<u><font size='big'>$storeName</font></u>\n" +
                "[L]\n" +
                "[L]\n ${transaction.createdAt.toFormattedDateTime()}\n" +
                "[C]================================\n" +
                "[L]\n"
    val sb = StringBuilder(text)
    transaction.items.forEach {
        sb.append(
            "[L]${it.productName.value}[R]${it.productSellPrice.value.toRupiah()}\n"
        )
        sb.append(
            "[L]  + ${
                it.productAmount.value.toBigDecimal()
                    .stripTrailingZeros()
            } ${it.productUnit.value}[R]${(it.productAmount.value.toBigDecimal() * it.productSellPrice.value).toRupiah()}\n"
        )
        sb.append(
            "[L]\n"
        )
    }

    sb.append(
        "[C]--------------------------------\n" +
        "[R]TOTAL HARGA :[R]${
            transaction.items
                .sumOf { it.productAmount.value.toBigDecimal() * it.productSellPrice.value }
                .toRupiah()}\n"
    )

    if (transaction.customerName.value.isNotBlank()) {
        sb.append(
            "[L]NAMA PELANGGAN :[R]${transaction.customerName.value}\n"
        )
    }

    sb.append(
        """
            [C]================================
            [C]Terimakasih telah datang
        """.trimIndent()
    )

    printer.printFormattedText(sb.toString())
}
