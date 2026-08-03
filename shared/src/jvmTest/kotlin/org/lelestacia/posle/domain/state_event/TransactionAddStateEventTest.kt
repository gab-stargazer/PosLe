package org.lelestacia.posle.domain.state_event

import org.lelestacia.posle.domain.model.BundleProduct
import org.lelestacia.posle.domain.model.CartItems
import org.lelestacia.posle.util.Amount
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.Price
import org.lelestacia.posle.util.SkuNumber
import org.lelestacia.posle.util.Unit
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class TransactionAddStateEventTest {

    private val timestamp = 1_700_000_000_000L

    private fun productCart(
        productId: Int,
        quantity: String,
        productName: String = "Beras 5kg"
    ): CartItems.ProductCartItem = CartItems.ProductCartItem(
        id = 1,
        productId = productId,
        productName = Name(productName),
        skuNumber = SkuNumber("BR5-001"),
        imageUri = null,
        productQuantity = Amount(BigDecimal(quantity)),
        productBuyPrice = Price(BigDecimal("80000")),
        productSellPrice = Price(BigDecimal("90000")),
        productUnit = Unit("bungkus"),
        productNote = null
    )

    private fun bundleCart(
        bundleId: Int = 1,
        products: List<Triple<Int, String, String>>,
        bundleQuantity: String = "1"
    ): CartItems.BundleCartItem = CartItems.BundleCartItem(
        id = 1,
        bundleId = bundleId,
        bundleName = Name("Paket Sembako"),
        bundleQuantity = Amount(BigDecimal(bundleQuantity)),
        bundleTotalPrice = Price(BigDecimal.ZERO),
        bundleNote = null,
        bundleProducts = products.map { (productId, quantity, name) ->
            BundleProduct(
                productId = productId,
                productName = Name(name),
                skuNumber = SkuNumber(""),
                imageUri = null,
                buyPrice = Price(BigDecimal.ZERO),
                sellPrice = Price(BigDecimal("10000")),
                sellPriceIndividual = Price(BigDecimal("10000")),
                quantity = Amount(BigDecimal(quantity)),
                unit = Unit("pcs"),
                createdAt = timestamp,
                updatedAt = null
            )
        }
    )

    // ═══════════════════════════════════════════════════════════════════════
    //  cartQuantityInCart
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    fun `cartQuantityInCart returns zero for empty cart`() {
        assertEquals(BigDecimal.ZERO, cartQuantityInCart(productId = 1, cartItems = emptyList()))
    }

    @Test
    fun `cartQuantityInCart sums plain product lines for the product`() {
        val cart = listOf(
            productCart(productId = 1, quantity = "2"),
            productCart(productId = 1, quantity = "3"),
            productCart(productId = 2, quantity = "5")
        )

        assertEquals(BigDecimal("5"), cartQuantityInCart(productId = 1, cartItems = cart))
        assertEquals(BigDecimal("5"), cartQuantityInCart(productId = 2, cartItems = cart))
        assertEquals(BigDecimal.ZERO, cartQuantityInCart(productId = 99, cartItems = cart))
    }

    @Test
    fun `cartQuantityInCart multiplies bundle count by per-bundle quantity`() {
        val cart = listOf(
            bundleCart(
                products = listOf(
                    Triple(1, "4", "Beras"),
                    Triple(2, "2", "Gula")
                ),
                bundleQuantity = "2"
            )
        )

        assertEquals(BigDecimal("8"), cartQuantityInCart(productId = 1, cartItems = cart))
        assertEquals(BigDecimal("4"), cartQuantityInCart(productId = 2, cartItems = cart))
        assertEquals(BigDecimal.ZERO, cartQuantityInCart(productId = 3, cartItems = cart))
    }

    @Test
    fun `cartQuantityInCart combines plain products and bundle contents`() {
        val cart = listOf(
            productCart(productId = 1, quantity = "3"),
            bundleCart(
                products = listOf(
                    Triple(1, "4", "Beras"),
                    Triple(2, "2", "Gula")
                ),
                bundleQuantity = "2"
            )
        )

        // 3 plain units + 2 bundles × 4 units inside = 11
        assertEquals(BigDecimal("11"), cartQuantityInCart(productId = 1, cartItems = cart))
        assertEquals(BigDecimal("4"), cartQuantityInCart(productId = 2, cartItems = cart))
    }
}
