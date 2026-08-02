package org.lelestacia.posle.util

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

fun BigDecimal.toRupiah(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
    return formatter.format(this)
}

fun BigDecimal.toDisplayText(): String {
    return if (this.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0) {
        this.toBigInteger().toString()
    } else {
        this.stripTrailingZeros().toPlainString()
    }
}

/**
 * Returns only the digit characters of [this] string, preserving their order.
 * Used to enforce numeric-only input in quantity fields.
 */
fun String.digitsOnly(): String = filter { it.isDigit() }