package com.eniessi.radiantboard.core.di

import com.eniessi.radiantboard.core.domain.GetLatestMatchAnalysisUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetLatestMatchAnalysisUseCase(get(), get()) }
}
