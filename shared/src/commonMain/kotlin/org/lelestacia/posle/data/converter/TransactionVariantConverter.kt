package org.lelestacia.posle.data.converter

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import org.lelestacia.posle.domain.model.Variant

object TransactionVariantConverter {

    @TypeConverter
    fun fromString(value: String): List<Variant> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromList(list: List<Variant>): String {
        return Json.encodeToString(list)
    }
}
