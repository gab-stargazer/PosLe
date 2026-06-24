package org.lelestacia.posle.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.sqlite.driver.AndroidSQLiteDriver
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.lelestacia.posle.data.PosLeDB
import org.lelestacia.posle.util.FileStorage
import org.lelestacia.posle.util.createDataStore

val androidModule = module {
    single<PosLeDB> {
        getDatabaseBuilder(androidContext())
            .setQueryCoroutineContext(Dispatchers.IO)
            .setDriver(AndroidSQLiteDriver())
            .addMigrations(PosLeDB.MIGRATION_1_2)
            .addMigrations(PosLeDB.MIGRATION_2_3)
            .addMigrations(PosLeDB.MIGRATION_3_4)
            .build()
    }

    single<DataStore<Preferences>> {
        createDataStore(androidContext())
    }

    singleOf(::FileStorage)
}