package org.lelestacia.posle.util

import android.content.Context
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.EscPosPrinterCommands
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import cz.multiplatform.escpos4k.bluetooth.BluetoothPrinterManager
import cz.multiplatform.escpos4k.core.LineSegment
import cz.multiplatform.escpos4k.core.PrintError
import cz.multiplatform.escpos4k.core.PrinterConfiguration
import cz.multiplatform.escpos4k.core.TextAlignment
import cz.multiplatform.escpos4k.core.print
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
                val subtotal = productQuantity.value.toBigDecimal() * productSellPrice

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
                val subtotal = bundleQuantity.toBigDecimal() * bundleSellPrice
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
                    it.sellPrice.value * it.quantity.value.toBigDecimal()
                }
                .sumOf { it }

            cartItems.quantity.value.toBigDecimal() * subtotal
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

suspend fun Context.printTransactionV2(transaction: Transaction, storeName: Name) {
    val btManager = BluetoothPrinterManager(this)
    val device = btManager.pairedPrinters().getOrNull()?.first() ?: return
    val connection = btManager.openConnection(device).getOrNull() ?: return // (1)

    val config = PrinterConfiguration(charactersPerLine = 32)
    val libraryError: PrintError? = connection.print(PrinterConfiguration(32)) {       // (2)
        // MULTIPLE TEXT ALIGNMENTS PER LINE
        withTextSize(4, 4) {
            withUnderline(enabled = true) {
                line(storeName.value.ifBlank { "Transaksi Posle" })
            }
        }

        line("")
        line("")
        withTextSize(1, 1) {
            line("Waktu: ${transaction.createdAt.toFormattedDateTime()}")
        }

        segmentedLine(
            LineSegment("================================", TextAlignment.CENTER)
        )

        transaction.items.forEach { transactionItem ->
            when (transactionItem.type) {
                TransactionItemType.Product -> {
                    val productName = transactionItem.products.first().productName.value
                    val productSellPrice = transactionItem.products.first().sellPrice
                    val productQuantity = transactionItem.products.first().quantity
                    val productUnit = transactionItem.products.first().unit.value
                    val subtotal = productQuantity.value.toBigDecimal() * productSellPrice.value
                    withTextSize(1, 1) {
                        segmentedLine(
                            LineSegment(
                                text = productName,
                                alignment = TextAlignment.LEFT
                            ),
                            LineSegment(
                                text = "Rp${productSellPrice.value.stripTrailingZeros()}",
                                alignment = TextAlignment.RIGHT
                            )
                        )
                        segmentedLine(
                            LineSegment(
                                text = "\t${productQuantity.value.toDisplayText()} $productUnit",
                                alignment = TextAlignment.LEFT
                            ),
                            LineSegment(
                                text = "Subtotal: Rp${subtotal.stripTrailingZeros()}",
                                alignment = TextAlignment.RIGHT
                            )
                        )
                    }
                }

                TransactionItemType.Bundle -> {
                    val bundleName = transactionItem.name.value
                    val bundleSellPrice = transactionItem.sellPrice.value
                    val bundleQuantity = transactionItem.quantity.value
                    val subtotal = bundleQuantity.toBigDecimal() * bundleSellPrice

                    withTextSize(1, 1) {
                        segmentedLine(
                            LineSegment(
                                text = "$bundleName x${bundleQuantity.toDisplayText()}",
                                alignment = TextAlignment.LEFT
                            ),
                            LineSegment(
                                text = bundleSellPrice.toRupiah(),
                                alignment = TextAlignment.RIGHT
                            )
                        )
                        segmentedLine(
                            LineSegment(
                                text = subtotal.toRupiah(),
                                alignment = TextAlignment.RIGHT
                            )
                        )
                    }
                }
            }

            val totalPrice = transaction
                .items
                .map { cartItems ->
                    val subtotal = cartItems
                        .products
                        .map {
                            it.sellPrice.value * it.quantity.value.toBigDecimal()
                        }
                        .sumOf { it }

                    cartItems.quantity.value.toBigDecimal() * subtotal
                }
                .sumOf { it }

            withTextSize(3, 3) {
                segmentedLine(
                    LineSegment(
                        text = "TOTAL HARGA :",
                        alignment = TextAlignment.RIGHT
                    )
                )
                segmentedLine(
                    LineSegment(
                        text = totalPrice.toRupiah(),
                        alignment = TextAlignment.RIGHT
                    )
                )
                line("================================")
            }

            if (transaction.customerName.value.isNotBlank()) {
                withTextSize(3, 3) {
                    segmentedLine(
                        LineSegment(
                            text = "NAMA PELANGGAN :",
                            alignment = TextAlignment.LEFT
                        ),
                        LineSegment(
                            text = transaction.customerName.value,
                            alignment = TextAlignment.RIGHT
                        )
                    )
                }
            }

            line("")
            line("================================")
            line("Terimakasih telah Berbelanja")
        }
    }
}

fun printV3() {
    val connection = BluetoothPrintersConnections.selectFirstPaired()

    val commands = EscPosPrinterCommands(connection)
    commands.connect()

// Reset printer
    commands.reset()

// Select Font B
    connection?.write(
        byteArrayOf(
            0x1B, // ESC
            0x4D, // M
            0x01  // Font B
        )
    )

// Print some text
    connection?.write("THIS SHOULD BE FONT B\n".toByteArray())

// Back to Font A
    connection?.write(
        byteArrayOf(
            0x1B,
            0x4D,
            0x00
        )
    )

    connection?.write("THIS SHOULD BE FONT A\n".toByteArray())

    commands.feedPaper(100)
    commands.disconnect()
}
