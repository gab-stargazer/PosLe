package org.lelestacia.posle.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.FileStorage
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesFileSerializer
import org.lelestacia.posle.data.createDataStore
import org.lelestacia.posle.data.dataStoreFileName

fun createDataStore(context: Context): DataStore<Preferences> = createDataStore(
    storage = FileStorage (
        serializer = PreferencesFileSerializer,
        produceFile = { context.applicationContext.filesDir.resolve(dataStoreFileName) }
    )
)