package org.lelestacia.posle.util

import kotlinx.serialization.Serializable
import java.math.BigDecimal

/**
 * Inline value class representing a name (Customer, Product, Bundle).
 */
@Serializable
@JvmInline
value class Name(val value: String)

/**
 * Inline value class for monetary values, utilizing [BigDecimal] for precision.
 */
@Serializable(PriceSerializer::class)
@JvmInline
value class Price(val value: BigDecimal)

/**
 * Inline value class for units of measurement.
 */
@Serializable
@JvmInline
value class Unit(val value: String)

/**
 * Inline value class for quantity/amount values.
 */
@Serializable(with = AmountSerializer::class)
@JvmInline
value class Amount(val value: BigDecimal)

/**
 * Inline value class for UI tab indices.
 */
@JvmInline
value class SelectedTabIndex(val value: Int)

/**
 * Inline value class for Stock Keeping Unit numbers.
 */
@Serializable
@JvmInline
value class SkuNumber(val value: String)
