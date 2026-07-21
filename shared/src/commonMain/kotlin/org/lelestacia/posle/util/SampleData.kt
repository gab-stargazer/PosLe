package org.lelestacia.posle.util

import org.lelestacia.posle.data.entity.TransactionItemType
import org.lelestacia.posle.domain.model.Product
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.domain.model.TransactionItem
import org.lelestacia.posle.domain.model.TransactionProduct
import org.lelestacia.posle.domain.model.Variant
import java.math.BigDecimal

object SampleData {
    val products = listOf(
        Product(
            id = 1,
            name = Name("Sate Ayam"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Porsi"),
            imageUri = null
        ),
        Product(
            id = 2,
            name = Name("Es Teh Manis"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Gelas"),
            imageUri = null
        ),
        Product(
            id = 3,
            name = Name("Nasi Putih"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Porsi"),
            imageUri = null
        ),
        Product(
            id = 4,
            name = Name("Kerupuk"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Bungkus"),
            imageUri = null
        )
    )

    val indonesianFoodProducts = listOf(
        // 1. Nasi Goreng (Fried Rice)
        Product(
            id = 1,
            name = Name("Nasi Goreng Spesial"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Plate"),
            imageUri = null,
            variants = emptyList()
        ),
        // 2. Soto Ayam (Chicken Soup)
        Product(
            id = 2,
            name = Name("Soto Ayam Lamongan"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Bowl"),
            imageUri = null,
            variants = emptyList()
        ),
        // 3. Gudeg Yogyakarta (Young Jackfruit Stew)
        Product(
            id = 3,
            name = Name("Gudeg Manggar"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Plate"),
            imageUri = null,
            variants = emptyList()
        ),
        // 4. Rendang Daging Sapi (Beef Rendang)
        Product(
            id = 4,
            name = Name("Rendang Daging Premium"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Portion"),
            imageUri = null,
            variants = emptyList()
        ),
        // 5. Sate Ayam (Chicken Satay)
        Product(
            id = 5,
            name = Name("Sate Ayam Madura"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Skewer Set"),
            imageUri = null,
            variants = emptyList()
        ),
        // 6. Pempek Palembang (Fish Cake Soup)
        Product(
            id = 6,
            name = Name("Pempek Kapal Selam Komplit"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Serving"),
            imageUri = null,
            variants = emptyList()
        ),
        // 7. Rawon Daging Sapi (Black Beef Soup)
        Product(
            id = 7,
            name = Name("Rawon Daging Spesial"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Bowl"),
            imageUri = null,
            variants = emptyList()
        ),
        // 8. Nasi Padang (Miniature Rice Meal)
        Product(
            id = 8,
            name = Name("Nasi Padang Komplit"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Plate"),
            imageUri = null,
            variants = emptyList()
        ),
        // 9. Gado-Gado (Mixed Vegetable Salad)
        Product(
            id = 9,
            name = Name("Gado-Gado Klasik"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Plate"),
            imageUri = null,
            variants = emptyList()
        ),
        // 10. Ketoprak (Tofu and Tauge Salad)
        Product(
            id = 10,
            name = Name("Ketoprak Segar"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Plate"),
            imageUri = null,
            variants = emptyList()
        ),
        // 11. Mie Ayam Bakso (Chicken Noodles with Meatballs)
        Product(
            id = 11,
            name = Name("Mie Ayam Premium"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Bowl"),
            imageUri = null,
            variants = emptyList()
        ),
        // 12. Tumpeng Mini (Miniature Rice Cone Meal)
        Product(
            id = 12,
            name = Name("Tumpeng Lauk Komplit"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Set"),
            imageUri = null,
            variants = emptyList()
        ),
        // 13. Bakso Malang (Meatball Soup)
        Product(
            id = 13,
            name = Name("Bakso Malang Komplit"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Bowl"),
            imageUri = null,
            variants = emptyList()
        ),
        // 14. Sayur Asem (Tamarind Vegetable Soup)
        Product(
            id = 14,
            name = Name("Sayur Asem Nusantara"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Bowl"),
            imageUri = null,
            variants = emptyList()
        ),
        // 15. Nasi Kuning (Yellow Rice)
        Product(
            id = 15,
            name = Name("Nasi Kuning Komplit"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Plate"),
            imageUri = null,
            variants = emptyList()
        ),
        // 16. Soto Betawi (Betawi Style Soup)
        Product(
            id = 16,
            name = Name("Soto Betawi Daging"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Bowl"), imageUri = null,
            variants = emptyList()
        ),
        // 17. Tahu Isi (Stuffed Tofu)
        Product(
            id = 17,
            name = Name("Tahu Isi Goreng"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Serving Platter"),
            imageUri = null,
            variants = emptyList()
        ),
        // 18. Klepon (Sweet Rice Cake)
        Product(
            id = 18,
            name = Name("Jajanan Pasar Klasik (Klepon)"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Set of 5"),
            imageUri = null,
            variants = emptyList()
        ),
        // 19. Nasi Uduk (Coconut Rice)
        Product(
            id = 19,
            name = Name("Nasi Uduk Komplit"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Plate"),
            imageUri = null,
            variants = emptyList()
        ),
        // 20. Bakso Kuah Kuning (Yellow Broth Meatballs)
        Product(
            id = 20,
            name = Name("Bakso Kuah Kuning Original"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Bowl"),
            imageUri = null,
            variants = emptyList()
        ),
        // 21. Ayam Bakar Madu (Grilled Honey Chicken)
        Product(
            id = 21,
            name = Name("Ayam Bakar Murni"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Half Chicken"),
            imageUri = null,
            variants = emptyList()
        ),
        // 22. Soto Betawi (Alternative/Variation)
        Product(
            id = 22,
            name = Name("Soto Daging Sapi Premium"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Bowl"),
            imageUri = null,
            variants = emptyList()
        ),
        // 23. Martabak Manis (Sweet Savory Pancake)
        Product(
            id = 23,
            name = Name("Martabak Manis Cokelat Keju"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Piece"),
            imageUri = null,
            variants = emptyList()
        ),
        // 24. Tahu Tek (Tofu and Soy Sauce)
        Product(
            id = 24,
            name = Name("Tahu Tek Komplit"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Plate"),
            imageUri = null,
            variants = emptyList()
        ),
        // 25. Nasi Jamblang (River Rice Special)
        Product(
            id = 25,
            name = Name("Nasi Jamblang Spesial"),
            buyPrice = Price(BigDecimal("15000")),
            sellPrice = Price(BigDecimal("15000")),
            stock = Amount(0F),
            unit = Unit("Plate"),
            imageUri = null,
            variants = emptyList()
        )
    )


    val sampleTransaction = Transaction(
        id = 1001,
        customerName = Name("Budi Santoso"),
        isRecapped = false,
        createdAt = 1752700000000L, // ~ July 2025
        updatedAt = null,
        items = listOf(

            // 1. Simple product purchase
            TransactionItem(
                id = 1,
                type = TransactionItemType.Product,
                referenceId = 501, // Product.id
                name = Name("Kopi Kapal Api Sachet"),
                quantity = Amount(3f),
                sellPrice = Price(BigDecimal("2500")),
                note = null,
                createdAt = 1752700000000L,
                updatedAt = null,
                products = listOf(
                    TransactionProduct(
                        productId = 501,
                        productName = Name("Kopi Kapal Api Sachet"),
                        skuNumber = SkuNumber("KKA-001"),
                        imageUri = "https://cdn.example.com/products/kapal-api.jpg",
                        buyPrice = Price(BigDecimal("1800")),
                        sellPrice = Price(BigDecimal("2500")),
                        unit = Unit("Bungkus"),
                        note = null,
                        quantity = Amount(1f), // 1 sachet per unit purchased
                        variants = emptyList()
                    )
                )
            ),

            // 2. Product with variant (e.g. size), fractional quantity example (bulk/loose item)
            TransactionItem(
                id = 2,
                type = TransactionItemType.Product,
                referenceId = 502,
                name = Name("Gula Pasir Curah"),
                quantity = Amount(0.5f), // 0.5 kg
                sellPrice = Price(BigDecimal("7500")),
                note = "Dibungkus plastik",
                createdAt = 1752700000000L,
                updatedAt = null,
                products = listOf(
                    TransactionProduct(
                        productId = 502,
                        productName = Name("Gula Pasir"),
                        skuNumber = SkuNumber("GP-CURAH"),
                        imageUri = "https://cdn.example.com/products/gula-curah.jpg",
                        buyPrice = Price(BigDecimal("12000")),
                        sellPrice = Price(BigDecimal("15000")),
                        unit = Unit("Kg"),
                        note = null,
                        quantity = Amount(1f),
                        variants = emptyList<Variant>()
                    )
                )
            ),

            // 3. Bundle purchase: "Paket Sarapan Hemat" (Breakfast Bundle)
            TransactionItem(
                id = 3,
                type = TransactionItemType.Bundle,
                referenceId = 9001, // Bundle.id
                name = Name("Paket Sarapan Hemat"),
                quantity = Amount(2f), // 2 bundles bought
                sellPrice = Price(BigDecimal("15000")),
                note = "Tanpa sambal",
                createdAt = 1752700000000L,
                updatedAt = null,
                products = listOf(
                    TransactionProduct(
                        productId = 601,
                        productName = Name("Indomie Goreng"),
                        skuNumber = SkuNumber("IDG-001"),
                        imageUri = "https://cdn.example.com/products/indomie-goreng.jpg",
                        buyPrice = Price(BigDecimal("2500")),
                        sellPrice = Price(BigDecimal("3500")),
                        unit = Unit("Bungkus"),
                        note = null,
                        quantity = Amount(1f), // per bundle
                        variants = emptyList()
                    ),
                    TransactionProduct(
                        productId = 602,
                        productName = Name("Telur Ayam"),
                        skuNumber = SkuNumber("TLR-001"),
                        imageUri = "https://cdn.example.com/products/telur.jpg",
                        buyPrice = Price(BigDecimal("1800")),
                        sellPrice = Price(BigDecimal("2500")),
                        unit = Unit("Butir"),
                        note = null,
                        quantity = Amount(2f), // 2 eggs per bundle
                        variants = emptyList()
                    ),
                    TransactionProduct(
                        productId = 603,
                        productName = Name("Teh Kotak"),
                        skuNumber = SkuNumber("TK-001"),
                        imageUri = "https://cdn.example.com/products/teh-kotak.jpg",
                        buyPrice = Price(BigDecimal("3000")),
                        sellPrice = Price(BigDecimal("4000")),
                        unit = Unit("Kotak"),
                        note = null,
                        quantity = Amount(1f), // per bundle
                        variants = emptyList<Variant>()
                    )
                )
            )
        )
    )
}
