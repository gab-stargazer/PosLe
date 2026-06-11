package org.lelestacia.posle.util

import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
@JvmInline
value class Name(val value: String)

@Serializable(PriceSerializer::class)
@JvmInline
value class Price(val value: BigDecimal)

@Serializable
@JvmInline
value class Unit(val value: String)

@Serializable
@JvmInline
value class Amount(val value: Float)

@JvmInline
value class SelectedTabIndex(val value: Int)