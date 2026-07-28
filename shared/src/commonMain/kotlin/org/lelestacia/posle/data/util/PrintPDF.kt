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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility for generating professionally formatted PDF transaction reports using iText7.
 */
object TransactionReportGenerator {

    private object Styles {
        // Colors
        val HEADER_BG = DeviceRgb(0xF0, 0xF0, 0xF0)
        val GRAY_TEXT = DeviceRgb(0x88, 0x88, 0x88)
        val BLUE_BADGE = DeviceRgb(0x1E, 0x5C, 0x99)

        // Font Sizes
        const val TITLE_SIZE = 22f
        const val SUBTITLE_SIZE = 13f
        const val SECTION_HEADER_SIZE = 15f
        const val BODY_SIZE = 12f

        // Layout
        const val PAGE_MARGIN = 40f
        const val CARD_PADDING = 12f
        const val DIVIDER_THICKNESS_LARGE = 1.5f
        const val DIVIDER_THICKNESS_SMALL = 0.75f
    }

    /**
     * Internal context to hold PDF generation state and simplify method signatures.
     */
    private data class GenerationContext(
        val document: Document,
        val regularFont: PdfFont,
        val boldFont: PdfFont
    )

    /**
     * Generates a PDF report into the provided [OutputStream].
     * Returns true if successful, false otherwise.
     */
    suspend fun generate(
        outputStream: OutputStream,
        storeName: String,
        transactionId: String,
        startDate: Long,
        finishDate: Long,
        transactions: List<Transaction>
    ): Boolean {
        println("PDF Generator: Starting generation for Transaction ID: $transactionId")
        return try {
            val pdfDoc = PdfDocument(PdfWriter(outputStream))
            val document = Document(pdfDoc, PageSize.A4).apply {
                setMargins(Styles.PAGE_MARGIN, Styles.PAGE_MARGIN, Styles.PAGE_MARGIN, Styles.PAGE_MARGIN)
            }

            val context = GenerationContext(
                document = document,
                regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA),
                boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
            )

            println("PDF Generator: Writing Header and Metadata")
            writeHeaderSection(context, storeName)
            writeMetadataSection(context, transactionId, startDate, finishDate - 1)
            addDivider(context, Styles.DIVIDER_THICKNESS_LARGE, marginBottom = 20f)

            println("PDF Generator: Processing ${transactions.size} transactions")
            transactions.forEachIndexed { index, transaction ->
                if (index % 10 == 0 && index > 0) {
                    println("PDF Generator: Processed $index / ${transactions.size} transactions")
                }
                writeTransactionCard(context, transaction)
            }

            addDivider(context, Styles.DIVIDER_THICKNESS_SMALL, marginTop = 10f)

            println("PDF Generator: Closing document")
            document.close()
            println("PDF Generator: Generation successful")
            true
        } catch (e: Exception) {
            println("PDF Generator: Generation failed with error: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    private fun writeHeaderSection(context: GenerationContext, storeName: String) {
        context.document.add(
            Paragraph("LAPORAN TRANSAKSI")
                .setFont(context.boldFont)
                .setFontSize(Styles.TITLE_SIZE)
                .setMultipliedLeading(1.15f)
                .setTextAlignment(TextAlignment.CENTER)
        )

        context.document.add(
            Paragraph(storeName.uppercase())
                .setFont(context.regularFont)
                .setFontSize(Styles.SUBTITLE_SIZE)
                .setFontColor(Styles.GRAY_TEXT)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(4f)
                .setMarginBottom(18f)
        )
    }

    private fun writeMetadataSection(
        context: GenerationContext,
        transactionId: String,
        startDate: Long,
        finishDate: Long
    ) {
        val printFmt = SimpleDateFormat("dd/MM/yyyy", Locale("in", "ID"))

        context.document.add(createLabelValueLine(context, "No. Transaksi: ", transactionId))
        context.document.add(
            createLabelValueLine(
                context,
                "Tanggal: ",
                when {
                    startDate == finishDate -> startDate.toFormattedDate()
                    else -> "${startDate.toFormattedDate()} - ${finishDate.toFormattedDate()}"
                }
            )
        )
        context.document.add(
            createLabelValueLine(context, "Tanggal Cetak: ", printFmt.format(Date()))
        )
    }

    private suspend fun writeTransactionCard(context: GenerationContext, transaction: Transaction) {
        // Date Header Table
        val headerTable = Table(UnitValue.createPercentArray(floatArrayOf(100f)))
            .useAllAvailableWidth()
            .setMarginTop(12f)
            .setMarginBottom(12F)

        headerTable.addCell(
            Cell().add(
                Paragraph(transaction.createdAt.toFormattedDateTime())
                    .setFont(context.boldFont)
                    .setFontSize(Styles.SECTION_HEADER_SIZE)
            )
                .setBackgroundColor(Styles.HEADER_BG)
                .setBorder(Border.NO_BORDER)
                .setPadding(Styles.CARD_PADDING)
        )
        context.document.add(headerTable)

        // Customer Info
        if (transaction.customerName.value.isNotBlank()) {
            context.document.add(
                createLabelValueLine(
                    context,
                    label = "Nama Pelanggan: ",
                    value = transaction.customerName.value,
                    marginLeft = 6F
                )
            )
        }

        // Transaction Items (Products and Bundles)
        transaction.items.sortedByDescending { it.type.name }.forEach { item ->
            writeTransactionItem(context, item)
        }

        // Summary Total for this transaction
        val total = calculateTransactionTotal(transaction)
        context.document.add(
            createSummaryRow(
                context,
                label = getString(
                    getSystemResourceEnvironment(),
                    Res.string.title_total,
                    total.toRupiah()
                )
            )
        )
    }

    private fun writeTransactionItem(context: GenerationContext, item: TransactionItem) {
        when (item.type) {
            TransactionItemType.Product -> {
                val product = item.products.first()
                context.document.add(
                    createDataRow(
                        context,
                        name = product.productName.value,
                        quantity = "${product.quantity.value.toDisplayText()} ${product.unit.value}",
                        price = product.sellPrice.value.toRupiah(),
                        subtotal = (product.quantity.value * product.sellPrice.value).toRupiah(),
                        isHeader = true
                    )
                )
            }

            TransactionItemType.Bundle -> {
                // Bundle Main Row
                context.document.add(
                    createDataRow(
                        context,
                        name = "${item.name.value} (${item.quantity.value.toDisplayText()})",
                        quantity = "",
                        price = item.sellPrice.value.toRupiah(),
                        subtotal = (item.sellPrice.value * item.quantity.value).toRupiah(),
                        isHeader = true
                    )
                )

                // Sub-header for bundle contents
                val subHeader = Table(UnitValue.createPercentArray(floatArrayOf(50F, 50F))).useAllAvailableWidth()
                subHeader.addCell(
                    Cell().add(Paragraph("Isi Konten").setFontSize(Styles.BODY_SIZE).setFont(context.regularFont))
                        .setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingLeft(6F)
                )
                subHeader.addCell(
                    Cell().add(Paragraph("Harga Paket").setFontSize(Styles.BODY_SIZE).setFont(context.boldFont))
                        .setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPaddingRight(6F)
                )
                context.document.add(subHeader)

                // Individual items in bundle
                item.products.forEach { p ->
                    context.document.add(
                        createDataRow(
                            context,
                            name = p.productName.value,
                            quantity = "${p.quantity.value.toDisplayText()} ${p.unit.value}",
                            price = p.sellPrice.value.toRupiah(),
                            subtotal = (p.quantity.value * p.sellPrice.value).toRupiah(),
                            isHeader = false,
                            marginLeft = 12F
                        )
                    )
                }
            }
        }
    }

    private fun createDataRow(
        context: GenerationContext,
        name: String,
        quantity: String,
        price: String,
        subtotal: String,
        isHeader: Boolean,
        marginLeft: Float = 0F
    ): Table {
        val font = if (isHeader) context.boldFont else context.regularFont
        // Widths: Name(40%), Qty(10%), Price(20%), Subtotal(30%)
        val table = Table(UnitValue.createPercentArray(floatArrayOf(40f, 10F, 20F, 30f))).useAllAvailableWidth()

        table.addCell(
            Cell().add(Paragraph(name).setFontSize(Styles.BODY_SIZE).setFont(font).setMarginLeft(marginLeft))
                .setBorder(Border.NO_BORDER).setPaddingLeft(6F)
        )
        table.addCell(
            Cell().add(Paragraph(quantity).setFontSize(Styles.BODY_SIZE).setFont(context.regularFont))
                .setBorder(Border.NO_BORDER)
        )
        table.addCell(
            Cell().add(Paragraph(price).setFontSize(Styles.BODY_SIZE).setFont(font))
                .setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT)
        )
        table.addCell(
            Cell().add(Paragraph(subtotal).setFontSize(Styles.BODY_SIZE).setFont(font))
                .setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(6F).setPaddingRight(6F)
        )

        return table
    }

    private fun createLabelValueLine(
        context: GenerationContext,
        label: String,
        value: String,
        marginLeft: Float = 0F
    ): Paragraph {
        return Paragraph()
            .add(Text(label).setFont(context.regularFont).setFontSize(Styles.BODY_SIZE))
            .add(Text(value).setFont(context.boldFont).setFontSize(Styles.BODY_SIZE))
            .setMarginLeft(marginLeft)
            .setMarginBottom(3f)
    }

    private fun createSummaryRow(context: GenerationContext, label: String): Table {
        val table = Table(UnitValue.createPercentArray(floatArrayOf(100f)))
            .useAllAvailableWidth()
            .setMarginTop(4f)

        table.addCell(
            Cell().add(Paragraph(label).setFont(context.boldFont).setFontSize(Styles.BODY_SIZE))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPaddingRight(6F)
        )

        return table
    }

    private fun addDivider(
        context: GenerationContext,
        thickness: Float,
        marginTop: Float = 0F,
        marginBottom: Float = 0F
    ) {
        context.document.add(
            LineSeparator(SolidLine(thickness))
                .setMarginTop(marginTop)
                .setMarginBottom(marginBottom)
        )
    }

    private fun calculateTransactionTotal(transaction: Transaction): BigDecimal {
        return transaction.items.sumOf { item ->
            when (item.type) {
                TransactionItemType.Product -> item.quantity.value * item.sellPrice.value
                TransactionItemType.Bundle -> item.products.sumOf { p ->
                    (p.sellPrice.value * p.quantity.value) * item.quantity.value
                }
            }
        }
    }
}
