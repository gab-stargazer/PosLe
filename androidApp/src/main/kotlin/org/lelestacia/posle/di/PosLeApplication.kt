package org.lelestacia.posle.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PosLeApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(androidModule)
            androidContext(this@PosLeApplication)
        }
    }
}