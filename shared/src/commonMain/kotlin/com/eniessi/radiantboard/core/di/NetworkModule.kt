package com.eniessi.radiantboard.core.di

import com.eniessi.radiantboard.core.network.httpClient
import org.koin.dsl.module

val networkModule = module {
    single { httpClient }
}
