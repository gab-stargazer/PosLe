package org.lelestacia.posle.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.FileStorage
import androidx.datastore.preferences.core.Preferences

fun createDataStore(storage: FileStorage<Preferences>): DataStore<Preferences> =
    DataStoreFactory.create(storage = storage)

const val dataStoreFileName = "dice.preferences_pb"