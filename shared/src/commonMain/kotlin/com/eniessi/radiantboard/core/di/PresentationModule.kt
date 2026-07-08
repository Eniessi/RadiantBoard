package com.eniessi.radiantboard.core.di

import com.eniessi.radiantboard.core.presentation.MatchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { MatchViewModel(get()) }
}
