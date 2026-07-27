package com.eniessi.radiantboard.core.di

import com.eniessi.radiantboard.core.domain.FilterUserContextKillsUseCase
import com.eniessi.radiantboard.core.domain.GetLatestMatchAnalysisUseCase
import com.eniessi.radiantboard.core.domain.heuristics.EvaluateRoundHeuristicsUseCase
import com.eniessi.radiantboard.core.domain.heuristics.FirstBloodHeuristic
import com.eniessi.radiantboard.core.domain.heuristics.MatchHeuristic
import com.eniessi.radiantboard.core.domain.heuristics.TradeKillHeuristic
import com.eniessi.radiantboard.core.domain.heuristics.UntradedDeathHeuristic
import org.koin.dsl.module

val domainModule = module {
    factory { GetLatestMatchAnalysisUseCase(get(), get()) }
    factory { FilterUserContextKillsUseCase() }

    single { TradeKillHeuristic() }
    single { FirstBloodHeuristic() }
    single { UntradedDeathHeuristic() }

    single<MatchHeuristic> { get<TradeKillHeuristic>() }
    single<MatchHeuristic> { get<FirstBloodHeuristic>() }
    single<MatchHeuristic> { get<UntradedDeathHeuristic>() }

    factory {
        EvaluateRoundHeuristicsUseCase(
            heuristics = setOf(
                get<TradeKillHeuristic>(),
                get<FirstBloodHeuristic>(),
                get<UntradedDeathHeuristic>()
            )
        )
    }
}
