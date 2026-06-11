package org.lelestacia.posle.data.converter

import androidx.room.TypeConverter
import java.math.BigDecimal

object BigDecimalConverter {

    @TypeConverter
    fun fromString(text: String): BigDecimal {
        return BigDecimal(text)
    }

    @TypeConverter
    fun toString(num: BigDecimal): String {
        return num.toString()
    }
}