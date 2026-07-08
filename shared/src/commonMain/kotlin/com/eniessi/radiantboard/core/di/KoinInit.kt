package com.eniessi.radiantboard.core.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.KoinApplication
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}): KoinApplication {
    return startKoin {
        appDeclaration()
        modules(
            networkModule,
            repositoryModule
        )
    }
}
