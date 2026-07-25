package org.lelestacia.posle.util

import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
@JvmInline
value class Name(val value: String) {
}

@Serializable(PriceSerializer::class)
@JvmInline
value class Price(val value: BigDecimal)

@Serializable
@JvmInline
value class Unit(val value: String)

@Serializable(with = AmountSerializer::class)
@JvmInline
value class Amount(val value: BigDecimal)

@JvmInline
value class SelectedTabIndex(val value: Int)

@Serializable
@JvmInline
value class SkuNumber(val value: String)
