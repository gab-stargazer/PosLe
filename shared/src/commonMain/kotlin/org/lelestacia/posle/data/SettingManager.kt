package org.lelestacia.posle.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingManager(
    private val dataStore: DataStore<Preferences>
) {

    val productVolatileKey = booleanPreferencesKey("is_product_volatile")
    val amountPreciseKey = booleanPreferencesKey("is_amount_precise")
    val customerNameKey = booleanPreferencesKey("is_customer_name_needed")

    fun readSettings(): Flow<PosLeSettings> {
        return dataStore.data.map {
            PosLeSettings(
                isProductVolatile = it[productVolatileKey] ?: false,
                isAmountPrecise = it[amountPreciseKey] ?: false,
                isCustomerNameNeeded = it[customerNameKey] ?: false
            )
        }
    }

    suspend fun updateProductVolatile(newValue: Boolean) {
        dataStore.edit { it[productVolatileKey] = newValue }
    }

    suspend fun updateAmountPrecise(newValue: Boolean) {
        dataStore.edit { it[amountPreciseKey] = newValue }
    }

    suspend fun updateCustomerNameNeeded(newValue: Boolean) {
        dataStore.edit { it[customerNameKey] = newValue }
    }
}

data class PosLeSettings(
    val isProductVolatile: Boolean = false,
    val isAmountPrecise: Boolean = false,
    val isCustomerNameNeeded: Boolean = false
)