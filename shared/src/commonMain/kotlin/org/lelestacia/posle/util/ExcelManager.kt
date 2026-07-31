package org.lelestacia.posle.util

import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.lelestacia.posle.domain.model.Category
import org.lelestacia.posle.domain.model.Product
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigDecimal

object ExcelManager {

    private const val COL_ID = 0
    private const val COL_SKU = 1
    private const val COL_NAME = 2
    private const val COL_CATEGORIES = 3
    private const val COL_UNIT = 4
    private const val COL_BUY_PRICE = 5
    private const val COL_SELL_PRICE = 6
    private const val COL_STOCK = 7

    fun exportProductsToExcel(products: List<Product>): ByteArray {
        val workbook: Workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Products")

        // Header
        val headerRow = sheet.createRow(0)
        headerRow.createCell(COL_ID).setCellValue("ID")
        headerRow.createCell(COL_SKU).setCellValue("SKU")
        headerRow.createCell(COL_NAME).setCellValue("Name")
        headerRow.createCell(COL_CATEGORIES).setCellValue("Categories")
        headerRow.createCell(COL_UNIT).setCellValue("Unit")
        headerRow.createCell(COL_BUY_PRICE).setCellValue("Buy Price")
        headerRow.createCell(COL_SELL_PRICE).setCellValue("Sell Price")
        headerRow.createCell(COL_STOCK).setCellValue("Stock")

        // Data
        products.forEachIndexed { index, product ->
            val row = sheet.createRow(index + 1)
            row.createCell(COL_ID).setCellValue(product.id.toDouble())
            row.createCell(COL_SKU).setCellValue(product.skuNumber?.value ?: "")
            row.createCell(COL_NAME).setCellValue(product.name.value)
            row.createCell(COL_CATEGORIES).setCellValue(
                product.categories.joinToString(",") { it.name.value }
            )
            row.createCell(COL_UNIT).setCellValue(product.unit.value)
            row.createCell(COL_BUY_PRICE).setCellValue(product.buyPrice.value.toDouble())
            row.createCell(COL_SELL_PRICE).setCellValue(product.sellPrice.value.toDouble())
            row.createCell(COL_STOCK).setCellValue(product.stock.value.toDouble())
        }

        val out = ByteArrayOutputStream()
        workbook.write(out)
        workbook.close()
        return out.toByteArray()
    }

    fun importProductsFromExcel(bytes: ByteArray): List<Product> {
        val workbook = XSSFWorkbook(ByteArrayInputStream(bytes))
        val sheet = workbook.getSheetAt(0)
        val products = mutableListOf<Product>()

        for (rowIndex in 1..sheet.lastRowNum) {
            val row = sheet.getRow(rowIndex) ?: continue
            
            val id = row.getCell(COL_ID)?.numericCellValue?.toInt() ?: 0
            val sku = row.getCell(COL_SKU)?.stringCellValue?.takeIf { it.isNotBlank() }
            val name = row.getCell(COL_NAME)?.stringCellValue ?: ""
            val categoriesStr = row.getCell(COL_CATEGORIES)?.stringCellValue ?: ""
            val unit = row.getCell(COL_UNIT)?.stringCellValue ?: ""
            val buyPrice = row.getCell(COL_BUY_PRICE)?.numericCellValue ?: 0.0
            val sellPrice = row.getCell(COL_SELL_PRICE)?.numericCellValue ?: 0.0
            val stock = row.getCell(COL_STOCK)?.numericCellValue ?: 0.0

            if (name.isBlank()) continue

            products.add(
                Product(
                    id = id,
                    name = Name(name),
                    skuNumber = sku?.let { SkuNumber(it) },
                    unit = Unit(unit),
                    buyPrice = Price(BigDecimal.valueOf(buyPrice)),
                    sellPrice = Price(BigDecimal.valueOf(sellPrice)),
                    stock = Amount(BigDecimal.valueOf(stock)),
                    categories = categoriesStr.split(",")
                        .filter { it.isNotBlank() }
                        .map { Category(id = 0, name = Name(it.trim())) }
                )
            )
        }

        workbook.close()
        return products
    }
}
