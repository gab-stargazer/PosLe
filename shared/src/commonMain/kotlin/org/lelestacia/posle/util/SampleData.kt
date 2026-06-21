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
            stock = Amount(0F),
            unit = Unit("Porsi"),
            imageUri = null
        ),
        Product(
            id = 2,
            name = Name("Es Teh Manis"),
            price = Price(BigDecimal("5000")),
            stock = Amount(0F),
            unit = Unit("Gelas"),
            imageUri = null
        ),
        Product(
            id = 3,
            name = Name("Nasi Putih"),
            price = Price(BigDecimal("5000")),
            stock = Amount(0F),
            unit = Unit("Porsi"),
            imageUri = null
        ),
        Product(
            id = 4,
            name = Name("Kerupuk"),
            price = Price(BigDecimal("1000")),
            stock = Amount(0F),
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
                    productId = it + 1,
                    productName = Name("Item Menu ${it + 1}"),
                    productPrice = Price(BigDecimal((1000 * (it + 1)))),
                    productUnit = Unit("Porsi"),
                    productAmount = Amount(1f)
                )
            },
            createdAt = 1718236800000L
        )
    }

    val indonesianFoodProducts = listOf(
        // 1. Nasi Goreng (Fried Rice)
        Product(
            id = 1,
            name = Name("Nasi Goreng Spesial"),
            price = Price(25000.toBigDecimal()), // Base price
            stock = Amount(0F),
            unit = Unit("Plate"),
            imageUri = null,
            variants = emptyList()
        ),
        // 2. Soto Ayam (Chicken Soup)
        Product(
            id = 2,
            name = Name("Soto Ayam Lamongan"),
            price = Price(18000.toBigDecimal()),
            stock = Amount(0F),
            unit = Unit("Bowl"),
            imageUri = null,
            variants = emptyList()
        ),
        // 3. Gudeg Yogyakarta (Young Jackfruit Stew)
        Product(
            id = 3,
            name = Name("Gudeg Manggar"),
            price = Price(35000.toBigDecimal()),
            stock = Amount(0F),
            unit = Unit("Plate"),
            imageUri = null,
            variants = emptyList()
        ),
        // 4. Rendang Daging Sapi (Beef Rendang)
        Product(
            id = 4,
            name = Name("Rendang Daging Premium"),
            price = Price(75000.toBigDecimal()),
            stock = Amount(0F),
            unit = Unit("Portion"),
            imageUri = null,
            variants = emptyList()
        ),
        // 5. Sate Ayam (Chicken Satay)
        Product(
            id = 5,
            name = Name("Sate Ayam Madura"),
            price = Price(30000.toBigDecimal()),
            stock = Amount(0F),
            unit = Unit("Skewer Set"),
            imageUri = null,
            variants = emptyList()
        ),
        // 6. Pempek Palembang (Fish Cake Soup)
        Product(
            id = 6,
            name = Name("Pempek Kapal Selam Komplit"),
            price = Price(32000.toBigDecimal()),
            stock = Amount(0F),
            unit = Unit("Serving"),
            imageUri = null,
            variants = emptyList()
        ),
        // 7. Rawon Daging Sapi (Black Beef Soup)
        Product(
            id = 7,
            name = Name("Rawon Daging Spesial"),
            price = Price(40000.toBigDecimal()),
            stock = Amount(0F),
            unit = Unit("Bowl"),
            imageUri = null,
            variants = emptyList()
        ),
        // 8. Nasi Padang (Miniature Rice Meal)
        Product(
            id = 8,
            name = Name("Nasi Padang Komplit"),
            price = Price(28000.toBigDecimal()),
            stock = Amount(0F),
            unit = Unit("Plate"),
            imageUri = null,
            variants = emptyList()
        ),
        // 9. Gado-Gado (Mixed Vegetable Salad)
        Product(
            id = 9,
            name = Name("Gado-Gado Klasik"),
            price = Price(22000.toBigDecimal()),
            stock = Amount(0F),
            unit = Unit("Plate"),
            imageUri = null,
            variants = emptyList()
        ),
        // 10. Ketoprak (Tofu and Tauge Salad)
        Product(
            id = 10,
            name = Name("Ketoprak Segar"),
            price = Price(18000.toBigDecimal()),
            stock = Amount(0F),
            unit = Unit("Plate"),
            imageUri = null,
            variants = emptyList()
        ),
        // 11. Mie Ayam Bakso (Chicken Noodles with Meatballs)
        Product(
            id = 11,
            name = Name("Mie Ayam Premium"),
            price = Price(20000.toBigDecimal()),
            stock = Amount(0F),
            unit = Unit("Bowl"),
            imageUri = null,
            variants = emptyList()
        ),
        // 12. Tumpeng Mini (Miniature Rice Cone Meal)
        Product(
            id = 12,
            name = Name("Tumpeng Lauk Komplit"),
            price = Price(50000.toBigDecimal()),
            stock = Amount(0F),
            unit = Unit("Set"),
            imageUri = null,
            variants = emptyList()
        ),
        // 13. Bakso Malang (Meatball Soup)
        Product(
            id = 13,
            name = Name("Bakso Malang Komplit"),
            price = Price(25000.toBigDecimal()),
            stock = Amount(0F),
            unit = Unit("Bowl"),
            imageUri = null,
            variants = emptyList()
        ),
        // 14. Sayur Asem (Tamarind Vegetable Soup)
        Product(
            id = 14,
            name = Name("Sayur Asem Nusantara"),
            price = Price(15000.toBigDecimal()),
            stock = Amount(0F),
            unit = Unit("Bowl"),
            imageUri = null,
            variants = emptyList()
        ),
        // 15. Nasi Kuning (Yellow Rice)
        Product(
            id = 15,
            name = Name("Nasi Kuning Komplit"),
            price = Price(30000.toBigDecimal()),
            stock = Amount(0F),
            unit = Unit("Plate"),
            imageUri = null,
            variants = emptyList()
        ),
        // 16. Soto Betawi (Betawi Style Soup)
        Product(
            id = 16,
            name = Name("Soto Betawi Daging"),
            price = Price(38000.toBigDecimal()),
            stock = Amount(0F),
            unit = Unit("Bowl"), imageUri = null,
            variants = emptyList()
        ),
        // 17. Tahu Isi (Stuffed Tofu)
        Product(
            id = 17,
            name = Name("Tahu Isi Goreng"),
            price = Price(15000.toBigDecimal()),
            stock = Amount(0F),
            unit = Unit("Serving Platter"),
            imageUri = null,
            variants = emptyList()
        ),
        // 18. Klepon (Sweet Rice Cake)
        Product(
            id = 18,
            name = Name("Jajanan Pasar Klasik (Klepon)"),
            price = Price(10000.toBigDecimal()),
            stock = Amount(0F),
            unit = Unit("Set of 5"),
            imageUri = null,
            variants = emptyList()
        ),
        // 19. Nasi Uduk (Coconut Rice)
        Product(
            id = 19,
            name = Name("Nasi Uduk Komplit"),
            price = Price(27000.toBigDecimal()),
            stock = Amount(0F),
            unit = Unit("Plate"),
            imageUri = null,
            variants = emptyList()
        ),
        // 20. Bakso Kuah Kuning (Yellow Broth Meatballs)
        Product(
            id = 20,
            name = Name("Bakso Kuah Kuning Original"),
            price = Price(23000.toBigDecimal()),
            stock = Amount(0F),
            unit = Unit("Bowl"),
            imageUri = null,
            variants = emptyList()
        ),
        // 21. Ayam Bakar Madu (Grilled Honey Chicken)
        Product(
            id = 21,
            name = Name("Ayam Bakar Murni"),
            price = Price(45000.toBigDecimal()),
            stock = Amount(0F),
            unit = Unit("Half Chicken"),
            imageUri = null,
            variants = emptyList()
        ),
        // 22. Soto Betawi (Alternative/Variation)
        Product(
            id = 22,
            name = Name("Soto Daging Sapi Premium"),
            price = Price(42000.toBigDecimal()),
            stock = Amount(0F),
            unit = Unit("Bowl"),
            imageUri = null,
            variants = emptyList()
        ),
        // 23. Martabak Manis (Sweet Savory Pancake)
        Product(
            id = 23,
            name = Name("Martabak Manis Cokelat Keju"),
            price = Price(40000.toBigDecimal()),
            stock = Amount(0F),
            unit = Unit("Piece"),
            imageUri = null,
            variants = emptyList()
        ),
        // 24. Tahu Tek (Tofu and Soy Sauce)
        Product(
            id = 24,
            name = Name("Tahu Tek Komplit"),
            price = Price(19000.toBigDecimal()),
            stock = Amount(0F),
            unit = Unit("Plate"),
            imageUri = null,
            variants = emptyList()
        ),
        // 25. Nasi Jamblang (River Rice Special)
        Product(
            id = 25,
            name = Name("Nasi Jamblang Spesial"),
            price = Price(31000.toBigDecimal()),
            stock = Amount(0F),
            unit = Unit("Plate"),
            imageUri = null,
            variants = emptyList()
        )
    )
}
