package org.lelestacia.posle.data

import androidx.compose.runtime.Immutable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.lelestacia.posle.util.Name

class SettingManager(
    private val dataStore: DataStore<Preferences>
) {

    val productVolatileKey = booleanPreferencesKey("is_product_volatile")
    val amountPreciseKey = booleanPreferencesKey("is_amount_precise")
    val customerNameKey = booleanPreferencesKey("is_customer_name_needed")
    val transactionRecapKey = booleanPreferencesKey("is_transaction_recap_needed")
    val productStockTracked = booleanPreferencesKey("is_product_stock_tracked")
    val storeNameKey = stringPreferencesKey("store_name")

    fun getSettings(): Flow<PosLeSettings> {
        return dataStore.data.map {
            PosLeSettings(
                isProductVolatile = it[productVolatileKey] ?: false,
                isAmountPrecise = it[amountPreciseKey] ?: false,
                isCustomerNameNeeded = it[customerNameKey] ?: false,
                isTransactionRecapNeeded = it[transactionRecapKey] ?: false,
                isProductStockTracked = it[productStockTracked] ?: false,
                storeName = Name(it[storeNameKey] ?: "")
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

    suspend fun updateTransactionRecapNeeded(newValue: Boolean) {
        dataStore.edit { it[transactionRecapKey] = newValue }
    }

    suspend fun updateProductStockTracked(newValue: Boolean) {
        dataStore.edit { it[productStockTracked] = newValue }
    }

    suspend fun updateStoreName(newName: Name) {
        dataStore.edit { it[storeNameKey] = newName.value }
    }
}

@Immutable
data class PosLeSettings(
    val isProductVolatile: Boolean = false,
    val isAmountPrecise: Boolean = false,
    val isCustomerNameNeeded: Boolean = false,
    val isTransactionRecapNeeded: Boolean = false,
    val isProductStockTracked: Boolean = false,

    //  Text Based Settings
    val storeName: Name = Name(""),
)