package org.lelestacia.posle.di

import androidx.sqlite.driver.AndroidSQLiteDriver
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.binds
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.lelestacia.posle.data.PosLeDB
import org.lelestacia.posle.data.dao.ProductDao
import org.lelestacia.posle.data.dao.TransactionDao
import org.lelestacia.posle.data.repository.ProductRepositoryImpl
import org.lelestacia.posle.data.repository.TransactionRepositoryImpl
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.domain.repository.TransactionRepository
import org.lelestacia.posle.util.FileStorage

val androidModule = module {
    single<PosLeDB> {
        getDatabaseBuilder(androidContext())
            .setQueryCoroutineContext(Dispatchers.IO)
            .setDriver(AndroidSQLiteDriver())
            .build()
    }

    single<ProductDao> {
        get<PosLeDB>().productDao()
    }

    single<TransactionDao> {
        get<PosLeDB>().transactionDao()
    }

    singleOf(::FileStorage)

    singleOf(::ProductRepositoryImpl) {
        binds(listOf(ProductRepository::class))
    }

    singleOf(::TransactionRepositoryImpl) {
        binds(listOf(TransactionRepository::class))
    }
}