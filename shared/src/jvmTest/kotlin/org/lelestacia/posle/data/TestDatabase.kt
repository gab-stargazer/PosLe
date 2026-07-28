package org.lelestacia.posle.data

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

fun createTestDatabase(): PosLeDB {
    return Room.inMemoryDatabaseBuilder<PosLeDB> {
        AppDatabaseConstructor.initialize()
    }.setDriver(BundledSQLiteDriver())
        .build()
}
