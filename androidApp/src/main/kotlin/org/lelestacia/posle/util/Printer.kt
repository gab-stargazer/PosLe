package org.lelestacia.posle.util

import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import org.lelestacia.posle.domain.model.Transaction

fun printTransaction(customername: String, transaction: Transaction) {
    val printer = EscPosPrinter(
        BluetoothPrintersConnections.selectFirstPaired(),
        203,
        58f,
        32
    )

    val text =
                "[C]<u><font size='big'>Transaksi PosLe</font></u>\n" +
                "[L]\n" +
                "[L]\n ${transaction.createdAt.toFormattedDateTime()}\n" +
                "[C]================================\n" +
                "[L]\n"
    val sb = StringBuilder(text)
    transaction.items.forEach {
        sb.append(
            "[L]${it.productName.value}[R]${it.productPrice.value.toRupiah()}\n"
        )
        sb.append(
            "[L]  + ${
                it.productAmount.value.toBigDecimal()
                    .stripTrailingZeros()
            } ${it.productUnit.value}[R]${(it.productAmount.value.toBigDecimal() * it.productPrice.value).toRupiah()}\n"
        )
        sb.append(
            "[L]\n"
        )
    }

    sb.append(
        "[C]--------------------------------\n" +
        "[R]TOTAL HARGA :[R]${
            transaction.items
                .sumOf { it.productAmount.value.toBigDecimal() * it.productPrice.value }
                .toRupiah()}\n"
    )

    if (customername.isNotBlank()) {
        sb.append(
            "[L]NAMA PELANGGAN :[R]$customername\n"
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
