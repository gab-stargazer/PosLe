package org.lelestacia.posle.util

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

fun BigDecimal.toRupiah(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
    return formatter.format(this)
}

fun Float.toDisplayText(): String {
    return if (this % 1F == 0F) {
        this.roundToInt().toString()
    } else {
        this.toString()
    }
}