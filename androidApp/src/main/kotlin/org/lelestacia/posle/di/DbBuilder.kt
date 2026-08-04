package org.lelestacia.posle.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.lelestacia.posle.data.PosLeDB

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<PosLeDB> {
    val dbFile = context.getDatabasePath("my_room.db")
    return Room.databaseBuilder<PosLeDB>(
        context = context,
        name = dbFile.absolutePath
    )
}