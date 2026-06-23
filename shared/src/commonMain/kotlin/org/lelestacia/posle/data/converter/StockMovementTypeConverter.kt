package org.lelestacia.posle.data.converter

import androidx.room.TypeConverter
import org.lelestacia.posle.data.entity.StockMovementType

object StockMovementTypeConverter {

    @TypeConverter
    fun fromString(value: String): StockMovementType {
        return StockMovementType.valueOf(value)
    }

    @TypeConverter
    fun toString(value: StockMovementType): String {
        return value.name
    }
}
