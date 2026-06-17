package org.lelestacia.posle.util

import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.model.TransactionItem
import java.math.BigDecimal

object SampleData {
    val products = listOf(
        Product(
            id = 1,
            name = Name("Sate Ayam"),
            price = Price(BigDecimal("15000")),
            unit = Unit("Porsi"),
            imageUri = null
        ),
        Product(
            id = 2,
            name = Name("Es Teh Manis"),
            price = Price(BigDecimal("5000")),
            unit = Unit("Gelas"),
            imageUri = null
        ),
        Product(
            id = 3,
            name = Name("Nasi Putih"),
            price = Price(BigDecimal("5000")),
            unit = Unit("Porsi"),
            imageUri = null
        ),
        Product(
            id = 4,
            name = Name("Kerupuk"),
            price = Price(BigDecimal("1000")),
            unit = Unit("Bungkus"),
            imageUri = null
        )
    )

    val largeTransaction = List(10) { transaction ->
        Transaction(
            id = transaction,
            customerName = Name("Gourmet Customer"),
            items = List(30) {
                TransactionItem(
                    id = it + 1,
                    productName = Name("Item Menu ${it + 1}"),
                    productPrice = Price(BigDecimal((1000 * (it + 1)))),
                    productUnit = Unit("Porsi"),
                    productAmount = Amount(1f)
                )
            },
            createdAt = 1718236800000L
        )
    }
}
