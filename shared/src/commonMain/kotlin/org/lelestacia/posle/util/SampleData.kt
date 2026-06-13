package org.lelestacia.posle.util

import org.lelestacia.posle.domain.model.Product
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
}
