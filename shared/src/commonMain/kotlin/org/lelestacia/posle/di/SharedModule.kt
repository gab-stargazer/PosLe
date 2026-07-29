package org.lelestacia.posle.di

import androidx.compose.material3.SnackbarHostState
import org.koin.core.module.dsl.binds
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.lelestacia.posle.data.PosLeDB
import org.lelestacia.posle.data.SettingManager
import org.lelestacia.posle.data.TransactionRunner
import org.lelestacia.posle.data.TransactionRunnerImpl
import org.lelestacia.posle.data.dao.BatchDao
import org.lelestacia.posle.data.dao.BundleDao
import org.lelestacia.posle.data.dao.CategoryDao
import org.lelestacia.posle.data.dao.ProductDao
import org.lelestacia.posle.data.dao.StockDao
import org.lelestacia.posle.data.dao.TransactionDao
import org.lelestacia.posle.data.dao.VariantDao
import org.lelestacia.posle.data.repository.BundleRepositoryImpl
import org.lelestacia.posle.data.repository.CategoryRepositoryImpl
import org.lelestacia.posle.data.repository.ProductRepositoryImpl
import org.lelestacia.posle.data.repository.StockRepositoryImpl
import org.lelestacia.posle.data.repository.TransactionRepositoryImpl
import org.lelestacia.posle.data.repository.VariantRepositoryImpl
import org.lelestacia.posle.domain.repository.BundleRepository
import org.lelestacia.posle.domain.repository.CategoryRepository
import org.lelestacia.posle.domain.repository.ProductRepository
import org.lelestacia.posle.domain.repository.StockRepository
import org.lelestacia.posle.domain.repository.TransactionRepository
import org.lelestacia.posle.domain.repository.VariantRepository

val sharedModule = module {

    single<ProductDao> {
        get<PosLeDB>().productDao()
    }

    single<StockDao> {
        get<PosLeDB>().stockDao()
    }

    single<TransactionDao> {
        get<PosLeDB>().transactionDao()
    }

    single<VariantDao> {
        get<PosLeDB>().variantDao()
    }

    single<CategoryDao> {
        get<PosLeDB>().categoryDao()
    }

    single<BundleDao> {
        get<PosLeDB>().bundleDao()
    }

    single<BatchDao> {
        get<PosLeDB>().batchDao()
    }

    single { SnackbarHostState() }

    singleOf(::SettingManager)

    singleOf(::TransactionRunnerImpl) {
        binds(listOf(TransactionRunner::class ))
    }

    singleOf(::ProductRepositoryImpl) {
        binds(listOf(ProductRepository::class))
    }

    singleOf(::CategoryRepositoryImpl) {
        binds(listOf(CategoryRepository::class))
    }

    singleOf(::StockRepositoryImpl) {
        binds(listOf(StockRepository::class))
    }

    singleOf(::VariantRepositoryImpl) {
        binds(listOf(VariantRepository::class))
    }

    singleOf(::TransactionRepositoryImpl) {
        binds(listOf(TransactionRepository::class))
    }

    singleOf(::BundleRepositoryImpl) {
        binds(listOf(BundleRepository::class))
    }
}