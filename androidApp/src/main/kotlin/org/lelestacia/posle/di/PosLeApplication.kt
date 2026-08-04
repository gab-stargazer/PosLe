package org.lelestacia.posle.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PosLeApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        //  Apache POI 5.x needs a StAX implementation on Android — the platform
        //  ships no javax.xml.stream. Without these factories, exporting to
        //  .xlsx crashes with NoClassDefFoundError once POI writes sheet XML.
        System.setProperty(
            "javax.xml.stream.XMLInputFactory",
            "com.fasterxml.aalto.stax.InputFactoryImpl"
        )
        System.setProperty(
            "javax.xml.stream.XMLOutputFactory",
            "com.fasterxml.aalto.stax.OutputFactoryImpl"
        )
        System.setProperty(
            "javax.xml.stream.XMLEventFactory",
            "com.fasterxml.aalto.stax.EventFactoryImpl"
        )

        startKoin {
            modules(sharedModule, androidModule)
            androidContext(this@PosLeApplication)
        }
    }
}