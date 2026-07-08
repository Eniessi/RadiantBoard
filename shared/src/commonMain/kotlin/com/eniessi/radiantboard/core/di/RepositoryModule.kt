package com.eniessi.radiantboard.core.di

import com.eniessi.radiantboard.core.domain.ValorantRepository
import com.eniessi.radiantboard.core.network.ValorantRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<ValorantRepository> { ValorantRepositoryImpl(get()) }
}
