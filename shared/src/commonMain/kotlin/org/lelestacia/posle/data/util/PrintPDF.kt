package org.lelestacia.posle.data.util

import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.LineSeparator
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.getSystemResourceEnvironment
import org.lelestacia.posle.data.entity.TransactionItemType
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.model.TransactionItem
import org.lelestacia.posle.util.toDisplayText
import org.lelestacia.posle.util.toFormattedDate
import org.lelestacia.posle.util.toFormattedDateTime
import org.lelestacia.posle.util.toRupiah
import posle.shared.generated.resources.Res
import posle.shared.generated.resources.title_total
import java.io.OutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TransactionReportGenerator {

    private val HEADER_BG = DeviceRgb(0xF0, 0xF0, 0xF0)
    private val GRAY_TEXT = DeviceRgb(0x88, 0x88, 0x88)
    private val LIGHT_LINE = DeviceRgb(0xCC, 0xCC, 0xCC)
    private val BLUE_BADGE = DeviceRgb(0x1E, 0x5C, 0x99)

    suspend fun generate(
        outputStream: OutputStream,
        storeName: String,
        transactionId: String,
        startDate: Long,
        finishDate: Long,
        transactions: List<Transaction>
    ): Boolean {
        return try {
            val pdfDoc = PdfDocument(PdfWriter(outputStream))
            val document = Document(pdfDoc, PageSize.A4)
            document.setMargins(40f, 40f, 40f, 40f)


            val regular = PdfFontFactory.createFont(StandardFonts.HELVETICA)
            val bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)

            addHeader(document, bold, regular, storeName)
            addTransactionInfo(document, regular, bold, transactionId, startDate, finishDate - 1)
            addDivider(document, 1.5f, 20f)

            transactions.forEach { transaction ->
                addTransactionItemBlock(
                    document,
                    regular,
                    bold,
                    transaction
                )
            }

            addDivider(document, 1f, 10f)

            document.close() // flushes into outputStream; do not also call outputStream.close() here
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // -- Title -----------------------------------------------------------

    private fun addHeader(document: Document, bold: PdfFont, regular: PdfFont, storeName: String) {
        document.add(
            Paragraph("LAPORAN TRANSAKSI")
                .setFont(bold)
                .setFontSize(22f)
                .setMultipliedLeading(1.15f)
                .setTextAlignment(TextAlignment.CENTER)
        )

        document.add(
            Paragraph(storeName.uppercase())
                .setFont(regular)
                .setFontSize(13f)
                .setFontColor(GRAY_TEXT)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(4f)
                .setMarginBottom(18f)
        )
    }

    // -- Transaction info block --------------------------------------------

    private fun addTransactionInfo(
        document: Document,
        regular: PdfFont,
        bold: PdfFont,
        transactionId: String,
        startDate: Long,
        finishDate: Long,
    ) {
        val dateFmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("in", "ID"))
        val printFmt = SimpleDateFormat("dd/MM/yyyy", Locale("in", "ID"))
        document.add(labelValueLine("No. Transaksi: ", transactionId, regular, bold))
        document.add(
            labelValueLine(
                "Tanggal: ",
                when {
                    startDate == finishDate -> startDate.toFormattedDate()
                    else -> "${startDate.toFormattedDate()} - ${finishDate.toFormattedDate()}"
                },
                regular,
                bold
            )
        )
        document.add(labelValueLine("Tanggal Cetak: ", printFmt.format(Date()), regular, bold))
    }

    private fun labelValueLine(
        label: String,
        value: String,
        regular: PdfFont,
        bold: PdfFont,
        marginLeft: Float = 0F
    ): Paragraph {
        return Paragraph()
            .add(Text(label).setFont(regular).setFontSize(12f))
            .add(Text(value).setFont(bold).setFontSize(12f))
            .setMarginLeft(marginLeft)
    }

    private fun addDivider(
        document: Document,
        thickness: Float,
        marginTop: Float = 0F,
        marginBottom: Float = 0F
    ) {
        document.add(
            LineSeparator(SolidLine(thickness))
                .setMarginTop(marginTop)
                .setMarginBottom(marginBottom)
        )
    }

    private suspend fun addTransactionItemBlock(
        document: Document,
        regular: PdfFont,
        bold: PdfFont,
        transaction: Transaction
    ) {

        val headerTable = Table(UnitValue.createPercentArray(floatArrayOf(70f, 30f)))
            .useAllAvailableWidth()
            .setMarginTop(12f)
            .setMarginBottom(12F)

        headerTable.addCell(
            Cell(1, 2).add(
                Paragraph(transaction.createdAt.toFormattedDateTime())
                    .setFont(bold)
                    .setFontSize(15f)
            )
                .setBackgroundColor(HEADER_BG)
                .setBorder(Border.NO_BORDER)
                .setPadding(12f)
        )

        document.add(headerTable)

        if (transaction.customerName.value.isNotBlank()) {
            document.add(
                labelValueLine(
                    label = "Nama Pelanggan: ",
                    value = transaction.customerName.value,
                    regular = regular,
                    bold = bold,
                    marginLeft = 6F
                )
            )
        }

        transaction.items.sortedByDescending { it.type.name }
            .forEachIndexed { index, transactionItem ->
                when (transactionItem.type) {
                    TransactionItemType.Product -> {

                        val product = transactionItem.products.first()
                        val quantity = product.quantity.value
                        val unit = product.unit.value
                        val sellPrice = product.sellPrice.value
                        document.add(
                            productRow(
                                name = product.productName.value,
                                quantityAndUnit = "${quantity.toDisplayText()} $unit",
                                price = sellPrice.toRupiah(),
                                subtotal = (quantity * sellPrice).toRupiah(),
                                regularFont = regular,
                                boldFont = bold
                            )
                        )
                    }

                    TransactionItemType.Bundle -> {
                        document.add(
                            productRow(
                                name = "${transactionItem.name.value} (${transactionItem.quantity.value.toDisplayText()})",
                                quantityAndUnit = "",
                                price = transactionItem.sellPrice.value.toRupiah(),
                                subtotal = (transactionItem.sellPrice.value * transactionItem.quantity.value).toRupiah(),
                                regularFont = regular,
                                boldFont = bold
                            )
                        )

                        val contentOfBundleAndPromoPriceTable =
                            Table(UnitValue.createPercentArray(floatArrayOf(50F, 50F)))
                                .useAllAvailableWidth()

                        contentOfBundleAndPromoPriceTable.addCell(
                            Cell()
                                .add(
                                    Paragraph(
                                        "Isi Konten"
                                    )
                                        .setFontSize(12F)
                                        .setFont(regular)
                                )
                                .setTextAlignment(TextAlignment.LEFT)
                                .setBorder(Border.NO_BORDER)
                                .setPaddingLeft(6F)
                        )

                        contentOfBundleAndPromoPriceTable.addCell(
                            Cell()
                                .add(
                                    Paragraph(
                                        "Harga Paket"
                                    )
                                        .setFontSize(12F)
                                        .setFont(bold)
                                )
                                .setTextAlignment(TextAlignment.RIGHT)
                                .setBorder(Border.NO_BORDER)
                                .setPaddingRight(6F)
                        )

                        document.add(contentOfBundleAndPromoPriceTable)

                        transactionItem.products.forEach { product ->
                            val quantity = product.quantity.value
                            val unit = product.unit.value
                            val sellPrice = product.sellPrice.value
                            document.add(
                                productRow(
                                    name = product.productName.value,
                                    quantityAndUnit = "${quantity.toDisplayText()} $unit",
                                    price = sellPrice.toRupiah(),
                                    subtotal = (quantity * sellPrice).toRupiah(),
                                    regularFont = regular,
                                    boldFont = bold,
                                    marginLeft = 12F
                                )
                            )
                        }
                    }
                }
            }

        val total = transaction.items
            .map { transactionItem ->
                when (transactionItem.type) {
                    TransactionItemType.Product -> {
                        transactionItem.quantity.value * transactionItem.sellPrice.value
                    }

                    TransactionItemType.Bundle -> {
                        transactionItem.products
                            .map { product ->
                                (product.sellPrice.value * product.quantity.value) * transactionItem.quantity.value
                            }
                            .sumOf { subtotalForEachItem -> subtotalForEachItem }
                    }
                }
            }
            .sumOf { subtotalForEachItem -> subtotalForEachItem }

        document.add(
            subtotalAndTotalRow(
                label = getString(
                    getSystemResourceEnvironment(),
                    Res.string.title_total,
                    total.toRupiah()
                ),
                labelFont = bold
            )
        )
    }

    private fun subtotalAndTotalRow(
        label: String,
        labelFont: PdfFont,
    ): Table {
        val table = Table(UnitValue.createPercentArray(floatArrayOf(100f)))
            .useAllAvailableWidth()
            .setMarginTop(4f)

        table.addCell(
            Cell().add(Paragraph(label).setFont(labelFont).setFontSize(12f))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPaddingRight(6F)
        )

        return table
    }

    private fun twoColRow(
        label: String,
        value: String,
        labelFont: PdfFont,
        valueFont: PdfFont,

        ): Table {
        val table = Table(UnitValue.createPercentArray(floatArrayOf(60f, 40f)))
            .useAllAvailableWidth()
            .setMarginTop(2f)

        table.addCell(
            Cell().add(Paragraph(label).setFont(labelFont).setFontSize(12f))
                .setBorder(Border.NO_BORDER)
                .setPaddingLeft(0f)
                .setPaddingTop(3f)
                .setPaddingBottom(3f)
        )
        table.addCell(
            Cell().add(
                Paragraph(value).setFont(valueFont).setFontSize(12f)
                    .setTextAlignment(TextAlignment.RIGHT)
            )
                .setBorder(Border.NO_BORDER)
                .setPaddingRight(0f)
                .setPaddingTop(3f)
                .setPaddingBottom(3f)
        )

        return table
    }

    private fun productRow(
        name: String,
        quantityAndUnit: String,
        price: String,
        subtotal: String,
        regularFont: PdfFont,
        boldFont: PdfFont,
        marginLeft: Float = 0F
    ): Table {
        //  Product Name - Quantity & Unit - Price - Subtotal
        val table =
            Table(
                UnitValue.createPercentArray(floatArrayOf(40f, 10F, 20F, 30f))
            ).useAllAvailableWidth()

        table.addCell(
            Cell()
                .add(
                    Paragraph(name)
                        .setFontSize(12F)
                        .setFont(boldFont)
                        .setMarginLeft(marginLeft)
                )
                .setBorder(Border.NO_BORDER)
                .setPaddingLeft(6F)
        )

        table.addCell(
            Cell()
                .add(
                    Paragraph(quantityAndUnit)
                        .setFontSize(12F)
                        .setFont(regularFont)
                )
                .setBorder(Border.NO_BORDER)
        )

        table.addCell(
            Cell()
                .add(
                    Paragraph(price)
                        .setFontSize(12F)
                        .setFont(boldFont)
                )
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT)
        )

        table.addCell(
            Cell()
                .add(
                    Paragraph(subtotal)
                        .setFontSize(12F)
                        .setFont(boldFont)
                )
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPaddingLeft(6F)
                .setPaddingRight(6F)
        )

        return table
    }

    private fun productAndBundleName(
        name: String,
        quantity: String,
        price: String,
        nameFont: PdfFont,
        quantityFont: PdfFont,
        priceFont: PdfFont,
        paddingLeft: Float = 0F
    ): Table {
        val table = Table(UnitValue.createPercentArray(floatArrayOf(60f, 20F, 20f)))
            .useAllAvailableWidth()
            .setMarginTop(3f)

        table.addCell(
            Cell()
                .add(
                    Paragraph(
                        when {
                            paddingLeft > 0F -> "- $name"
                            else -> name
                        }
                    )
                        .setFont(nameFont)
                        .setFontSize(12f)
                )
                .setBorder(Border.NO_BORDER)
                .setPaddingLeft(paddingLeft)
        )

        table.addCell(
            Cell()
                .add(
                    Paragraph(quantity)
                        .setFont(quantityFont)
                        .setFontSize(12f)
                )
                .setBorder(Border.NO_BORDER)
        )

        table.addCell(
            Cell().add(
                Paragraph(price)
                    .setFont(priceFont)
                    .setFontSize(12f)
                    .setTextAlignment(TextAlignment.RIGHT)
            )
                .setBorder(Border.NO_BORDER)
        )
        return table
    }

    // -- Grand total & signature --------------------------------------

    private fun addGrandTotal(
        document: Document,
        regular: PdfFont,
        bold: PdfFont,
        items: List<TransactionItem>
    ) {
        val grandTotal = items.fold(BigDecimal.ZERO) { acc, item ->
            acc + item.sellPrice.value.multiply(item.quantity.value)
        }
        document.add(
            Paragraph()
                .add(Text("TOTAL: ").setFont(bold).setFontSize(15f))
                .add(
                    Text(formatRupiah(grandTotal)).setFont(bold).setFontSize(15f)
                )
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(6f)
        )
    }

    private fun addSignature(document: Document, regular: PdfFont) {
        document.add(
            Paragraph("( " + "_".repeat(28) + " )")
                .setFont(regular)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(50f)
        )
    }

    // -- Formatting ------------------------------------------------------

    private fun formatRupiah(amount: BigDecimal): String {
        val symbols = DecimalFormatSymbols(Locale.US).apply { groupingSeparator = '.' }
        val formatter = DecimalFormat("#,###", symbols)
        return "Rp" + formatter.format(amount.setScale(0, RoundingMode.HALF_UP))
    }

    private fun formatQuantity(amount: BigDecimal): String {
        // Show as integer when there's no fractional part, otherwise keep decimals.
        return if (amount.stripTrailingZeros().scale() <= 0) {
            amount.setScale(0, RoundingMode.HALF_UP).toPlainString()
        } else {
            amount.stripTrailingZeros().toPlainString()
        }
    }
}